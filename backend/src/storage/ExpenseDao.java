package storage;

import logic.Category;
import logic.Event;
import logic.Participant;

import java.sql.*;
import java.util.Map;

public class ExpenseDao {

    public static void saveEventExpenses(Event event) {
        int eventId = event.getId();
        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before saving expenses.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // delete exist expenses
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM expenses WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // enter new expenses
            String insertSql = "INSERT INTO expenses (event_id, participant_id, category_id, amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Participant participant : event.getParticipants()) {
                    for (Map.Entry<Category, Double> entry : participant.getExpenses().entrySet()) {
                        insertStmt.setInt(1, eventId);
                        insertStmt.setInt(2, participant.getId());
                        insertStmt.setInt(3, entry.getKey().getId());
                        insertStmt.setDouble(4, entry.getValue());
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
            }

        } catch (SQLException e) {
            System.err.println("Failed to save expenses: " + e.getMessage());
        }
    }

    public static void saveEventConsumptions(Event event) {
        int eventId = event.getId();
        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before saving consumptions.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // delete exist consumptions from this event if exist
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM consumptions WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // enter new consumptions
            String insertSql = "INSERT INTO consumptions (event_id, participant_id, category_id) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Participant participant : event.getParticipants()) {
                    for (Category category : participant.getConsumedCategories()) {
                        insertStmt.setInt(1, eventId);
                        insertStmt.setInt(2, participant.getId());
                        insertStmt.setInt(3, category.getId());
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
            }

        } catch (SQLException e) {
            System.err.println("Failed to save consumptions: " + e.getMessage());
        }
    }

}
