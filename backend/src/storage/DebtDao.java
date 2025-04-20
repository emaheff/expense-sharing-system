package storage;

import logic.Debt;
import logic.Event;
import logic.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebtDao {

    public static void saveEventDebts(Event event) {
        int eventId = event.getId();

        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before saving debts.");
        }

        String deleteSql = "DELETE FROM debts WHERE event_id = ?";
        String insertSql = "INSERT INTO debts (event_id, from_participant_id, to_participant_id, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            // delete old debts if exist
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, eventId);
                deleteStmt.executeUpdate();
            }

            // enter new/updated debts to database
            for (Debt debt : event.getDebts()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, eventId);
                    insertStmt.setInt(2, debt.getDebtor().getId());
                    insertStmt.setInt(3, debt.getCreditor().getId());
                    insertStmt.setDouble(4, debt.getAmount());
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to save debts for event: " + e.getMessage());
        }
    }

    public static List<Debt> getDebtsForEvent(int eventId, List<Participant> participants) {
        List<Debt> debts = new ArrayList<>();

        String sql = """
        SELECT from_participant_id, to_participant_id, amount
        FROM debts
        WHERE event_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int fromId = rs.getInt("from_participant_id");
                int toId = rs.getInt("to_participant_id");
                double amount = rs.getDouble("amount");

                Participant from = ParticipantDao.findParticipantById(participants, fromId);
                Participant to = ParticipantDao.findParticipantById(participants, toId);

                if (from != null && to != null) {
                    debts.add(new Debt(from, to, amount));
                }
            }

        } catch (SQLException e) {
            System.err.println("Failed to load debts: " + e.getMessage());
        }

        return debts;
    }

}
