package backend.storage;

import backend.logic.Category;
import backend.logic.Event;
import backend.logic.Participant;
import shared.CategoryDto;
import shared.ParticipantDto;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventDao handles database interactions related to Event objects,
 * including creation, update, deletion, and full reconstruction from the database.
 */
public class EventDao {

    /**
     * Inserts a new event or updates an existing one based on the presence of an ID.
     *
     * @param event the event to insert or update
     * @return true if the operation was successful, false otherwise
     */
    public static boolean insertOrUpdateEvent(Event event) {
        String insertSql = "INSERT INTO events (name, date, participation_fee) VALUES (?, ?, ?)";
        String updateSql = "UPDATE events SET name = ?, date = ?, participation_fee = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (event.getId() != 0) {
                // UPDATE
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, event.getEventName());
                    stmt.setDate(2, Date.valueOf(event.getDate()));
                    stmt.setDouble(3, event.getParticipationFee());
                    stmt.setInt(4, event.getId());
                    int rowsUpdated = stmt.executeUpdate();
                    return rowsUpdated > 0;
                }
            } else {
                // INSERT
                try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, event.getEventName());
                    stmt.setDate(2, Date.valueOf(event.getDate()));
                    stmt.setDouble(3, event.getParticipationFee());
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        event.setId(rs.getInt(1));
                    }

                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to insert/update event in database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a summary list of all events from the database.
     *
     * @return a list of EventSummary objects (ID and name only)
     */
    public static List<EventSummary> getAllEvents() {
        List<EventSummary> events = new ArrayList<>();
        String sql = "SELECT id, name, date FROM events ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                LocalDate date = rs.getDate("date").toLocalDate();
                events.add(new EventSummary(id, name, date));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load event list: " + e.getMessage());
        }

        return events;
    }

    public static List<CategoryDto> getCategories(int eventId) {
        List<CategoryDto> categories = new ArrayList<>();
        String categoriesSql = """
                SELECT c.id, c.name
                FROM categories c
                JOIN event_categories ec ON c.id = ec.category_id
                WHERE ec.event_id = ?
                """;
        try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement categoryStmt = conn.prepareStatement(categoriesSql)) {
            categoryStmt.setInt(1, eventId);
            ResultSet rs = categoryStmt.executeQuery();

            while (rs.next()) {
                int categoryId = rs.getInt("id");
                String categoryName = rs.getString("name");

                CategoryDto categoryDto = new CategoryDto(categoryName);
                categoryDto.setId(categoryId);

                categories.add(categoryDto);
            }
        } catch (SQLException e) {
            System.err.println("Failed to load categories for event " + eventId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    public static List<ParticipantDto> getParticipants(int eventId) {
        List<ParticipantDto> participants = new ArrayList<>();

        String participantSql = """
        SELECT p.id, p.name, p.phone
        FROM participants p
        JOIN event_participants ep ON p.id = ep.participant_id
        WHERE ep.event_id = ?
        """;

        String consumptionSql = """
        SELECT c.name
        FROM consumptions cs
        JOIN categories c ON cs.category_id = c.id
        WHERE cs.event_id = ? AND cs.participant_id = ?
    """;

        String expenseSql = """
        SELECT c.name, e.amount
        FROM expenses e
        JOIN categories c ON e.category_id = c.id
        WHERE e.event_id = ? AND e.participant_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement participantStmt = conn.prepareStatement(participantSql)) {

            participantStmt.setInt(1, eventId);
            ResultSet rs = participantStmt.executeQuery();

            while (rs.next()) {
                int participantId = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");

                ParticipantDto dto = new ParticipantDto(name, phone);
                dto.setId(participantId);

                // Load consumed categories
                try (PreparedStatement csStmt = conn.prepareStatement(consumptionSql)) {
                    csStmt.setInt(1, eventId);
                    csStmt.setInt(2, participantId);
                    ResultSet csRs = csStmt.executeQuery();

                    List<String> consumed = new ArrayList<>();
                    while (csRs.next()) {
                        consumed.add(csRs.getString("name"));
                    }
                    dto.setConsumedCategories(consumed);

                }

                // Load expenses
                try (PreparedStatement exStmt = conn.prepareStatement(expenseSql)) {
                    exStmt.setInt(1, eventId);
                    exStmt.setInt(2, participantId);
                    ResultSet exRs = exStmt.executeQuery();

                    Map<String, Double> expenses = new HashMap<>();
                    while (exRs.next()) {
                        expenses.put(exRs.getString("name"), exRs.getDouble("amount"));
                    }
                    dto.setExpenses(expenses);
                }

                participants.add(dto);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load participants for event " + eventId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return participants;
    }

    public static void updateParticipantsForEvent(int eventId, List<ParticipantDto> participants) {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (ParticipantDto dto : participants) {


                int participantId = dto.getId();


                deleteParticipantDataForEvent(conn, eventId, participantId);


                if (dto.getExpenses() != null) {
                    for (Map.Entry<String, Double> entry : dto.getExpenses().entrySet()) {
                        String categoryName = entry.getKey();
                        double amount = entry.getValue();

                        Category category = new Category(categoryName);
                        int categoryId = CategoryDao.getOrCreateCategory(category);
                        CategoryDao.linkCategoryToEvent(categoryId, eventId);
                        insertExpense(conn, eventId, participantId, categoryId, amount);
                    }
                }

                if (dto.getConsumedCategories() != null) {
                    for (String categoryName : dto.getConsumedCategories()) {
                        Category category = new Category(categoryName);
                        int categoryId = CategoryDao.getOrCreateCategory(category);
                        CategoryDao.linkCategoryToEvent(categoryId, eventId);
                        insertConsumption(conn, eventId, participantId, categoryId);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to update participants for event " + eventId + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void insertExpense(Connection conn, int eventId, int participantId, int categoryId, double amount) throws SQLException {
        String sql = """
        INSERT INTO expenses (category_id, event_id, participant_id, amount)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (category_id, event_id, participant_id)
        DO UPDATE SET amount = EXCLUDED.amount
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, participantId);
            stmt.setDouble(4, amount);
            stmt.executeUpdate();
        }
    }

    private static void insertConsumption(Connection conn, int eventId, int participantId, int categoryId) throws SQLException {
        String sql = """
        INSERT INTO consumptions (category_id, event_id, participant_id)
        VALUES (?, ?, ?)
        ON CONFLICT (category_id, event_id, participant_id) DO NOTHING
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, participantId);
            stmt.executeUpdate();
        }
    }


    private static void deleteParticipantDataForEvent(Connection conn, int eventId, int participantId) throws SQLException {
        try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM expenses WHERE event_id = ? AND participant_id = ?");
             PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM consumptions WHERE event_id = ? AND participant_id = ?")) {

            stmt1.setInt(1, eventId);
            stmt1.setInt(2, participantId);
            stmt1.executeUpdate();

            stmt2.setInt(1, eventId);
            stmt2.setInt(2, participantId);
            stmt2.executeUpdate();
        }
    }





    /**
     * Loads a full Event object by its ID, including its participants, categories, and debts.
     *
     * @param eventId the ID of the event to load
     * @return the fully constructed Event, or null if not found or on error
     */
    public static Event loadEventById(int eventId) {
        String sql = "SELECT name, date, participation_fee FROM events WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                LocalDate date = rs.getDate("date").toLocalDate();
                double fee = rs.getDouble("participation_fee");

                Event event = new Event(name, fee, date);
                event.setId(eventId);
                List<Participant> eventParticipants = ParticipantDao.getParticipantsFromEvent(eventId);
                event.setParticipants(eventParticipants);
                event.setCategories(CategoryDao.getCategoriesForEvent(eventId));
                event.setDebts(DebtDao.getDebtsForEvent(eventId, eventParticipants));
                return event;
            }

        } catch (SQLException e) {
            System.err.println("Failed to load event by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Deletes an event from the database based on its ID.
     *
     * @param eventId the ID of the event to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean deleteEventById(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Failed to delete event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Simple projection class representing a minimal summary of an event.
     */
    public static class EventSummary {
        private final int id;
        private final String name;
        private final LocalDate date;

        public EventSummary(int id, String name, LocalDate date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public LocalDate getDate() { return date;}

        @Override
        public String toString() {
            return String.format("[ID: %d] %s", id, name);
        }
    }

}
