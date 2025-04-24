package storage;

import logic.Category;
import logic.Event;
import logic.Participant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ParticipantDao manages database operations related to participants,
 * including retrieval, insertion, linking to events, and loading related expenses and consumptions.
 */
public class ParticipantDao {

    /**
     * Finds a participant in the database by phone number.
     * If not found, inserts the participant and returns the new ID.
     *
     * @param participant the participant to find or insert
     * @return the ID of the participant in the database
     */
    private static int getOrCreateParticipant(Participant participant) {

        if (participant.getName() == null || participant.getName().isBlank()) {
            throw new IllegalArgumentException("Participant name cannot be null or empty");
        }

        String selectSql = "SELECT id FROM participants WHERE phone = ?";
        String insertSql = "INSERT INTO participants (name, phone, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            // checks if given participant already exist in database according to phone number
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, participant.getPhoneNumber());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int existingId = rs.getInt("id");
                    participant.setId(existingId);
                    return existingId;
                }
            }

            // participant does not exist in participants table - insert it.
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, participant.getName());
                insertStmt.setString(2, participant.getPhoneNumber());
                insertStmt.setString(3, participant.getEmail()); // can be null

                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    participant.setId(newId);
                    return newId;
                } else {
                    throw new SQLException("Participant insertion failed, no ID returned.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getOrCreateParticipant: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Links a participant to an event in the join table.
     */
    private static void linkParticipantToEvent(int participantId, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, participant_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to link participant to event: " + e.getMessage());
        }
    }

    /**
     * Saves and links all participants in an event to the database.
     *
     * @param event the event containing participants to be saved
     */
    public static void saveEventParticipants(Event event) {
        int eventId = event.getId();

        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before linking participants.");
        }

        for (Participant participant : event.getParticipants()) {
            try {
                int participantId = getOrCreateParticipant(participant);
                linkParticipantToEvent(participantId, eventId);
            } catch (Exception e) {
                System.err.println("Failed to save/link participant: " + participant.getName() + ". Reason: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves all participants linked to a specific event and loads their consumptions and expenses.
     *
     * @param eventId the event ID
     * @return a list of Participant objects
     */
    public static List<Participant> getParticipantsFromEvent(int eventId) {
        List<Participant> participants = new ArrayList<>();
        String sql = """
        SELECT p.id, p.name, p.phone, p.email
        FROM participants p
        JOIN event_participants ep ON p.id = ep.participant_id
        WHERE ep.event_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participant participant = new Participant(rs.getString("name"));
                participant.setId(rs.getInt("id"));
                participant.setPhoneNumber(rs.getString("phone"));
                participant.setEmail(rs.getString("email"));
                participants.add(participant);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load participants for event: " + e.getMessage());
        }

        List<Category> categories = CategoryDao.getCategoriesForEvent(eventId);
        loadParticipantConsumptions(participants, eventId, categories);
        loadParticipantExpenses(participants, eventId, categories);

        return participants;
    }

    /**
     * Loads consumption data for each participant and updates their consumed categories.
     */
    private static void loadParticipantConsumptions(List<Participant> participants, int eventId, List<Category> categories) {
        String sql = """
        SELECT participant_id, category_id
        FROM consumptions
        WHERE event_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int participantId = rs.getInt("participant_id");
                int categoryId = rs.getInt("category_id");

                Participant participant = findParticipantById(participants, participantId);
                Category category = CategoryDao.findCategoryById(categories, categoryId);

                if (participant != null && category != null) {
                    participant.getConsumedCategories().add(category);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load participant consumptions: " + e.getMessage());
        }
    }

    /**
     * Loads expense data for each participant and updates their expense map.
     */
    private static void loadParticipantExpenses(List<Participant> participants, int eventId, List<Category> categories) {
        String sql = """
        SELECT participant_id, category_id, amount
        FROM expenses
        WHERE event_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int participantId = rs.getInt("participant_id");
                int categoryId = rs.getInt("category_id");
                double amount = rs.getDouble("amount");

                Participant participant = findParticipantById(participants, participantId);
                Category category = CategoryDao.findCategoryById(categories, categoryId);

                if (participant != null && category != null) {
                    participant.getExpenses().put(category, amount);
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load participant expenses: " + e.getMessage());
        }
    }

    /**
     * Finds a participant in a list by their ID.
     *
     * @param participants the list of participants to search
     * @param id the ID to match
     * @return the matching Participant or null if not found
     */
    public static Participant findParticipantById(List<Participant> participants, int id) {
        for (Participant participant : participants) {
            if (participant.getId() == id) return participant;
        }
        return null;
    }

}
