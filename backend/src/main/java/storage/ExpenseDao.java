package storage;

import logic.Category;
import logic.Event;
import logic.Participant;

import java.sql.*;
import java.util.Map;

/**
 * ExpenseDao handles database operations related to expenses and consumptions
 * made by participants during an event.
 */
public class ExpenseDao {

    /**
     * Saves the expenses of all participants in a given event.
     * This method deletes existing expenses for the event and replaces them
     * with the current expense map from each participant.
     *
     * @param event the event whose expenses are to be saved
     */
    public static void saveEventExpenses(Event event) {
        int eventId = event.getId();
        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before saving expenses.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Delete existing expenses for the event
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM expenses WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // Insert new expenses from participants
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

    /**
     * Saves the consumptions of all participants in a given event.
     * This method deletes existing consumption records and inserts current data
     * based on each participant's consumed categories.
     *
     * @param event the event whose consumptions are to be saved
     */
    public static void saveEventConsumptions(Event event) {
        int eventId = event.getId();
        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before saving consumptions.");
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            // Delete existing consumption records for the event
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM consumptions WHERE event_id = ?")) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // Insert new consumption records
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
