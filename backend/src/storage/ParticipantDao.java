package storage;

import logic.Event;
import logic.Participant;

import java.sql.*;

public class ParticipantDao {

    private static int getOrCreateParticipant(Participant participant) {
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

    private static void linkParticipantToEvent(int participantId, int eventId) {
        String sql = "INSERT INTO event_participants (event_id, participant_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to link participant to event: " + e.getMessage());
        }
    }

    public static void saveEventParticipants(Event event) {
        int eventId = event.getId();

        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before linking participants.");
        }

        for (Participant participant : event.getParticipants()) {
            try {
                int participantId = ParticipantDao.getOrCreateParticipant(participant);
                ParticipantDao.linkParticipantToEvent(participantId, eventId);
            } catch (Exception e) {
                System.err.println("Failed to save/link participant: " + participant.getName() + ". Reason: " + e.getMessage());
            }
        }
    }

}
