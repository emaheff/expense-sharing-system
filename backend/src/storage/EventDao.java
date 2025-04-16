package storage;

import logic.Event;

import java.sql.*;

public class EventDao {

    public static boolean insertOrUpdateEvent(Event event) {
        String insertSql = "INSERT INTO events (name, date, participation_fee) VALUES (?, CURRENT_DATE, ?)";
        String updateSql = "UPDATE events SET name = ?, participation_fee = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (event.getId() != 0) {
                // UPDATE
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, event.getEventName());
                    stmt.setDouble(2, event.getParticipationFee());
                    stmt.setInt(3, event.getId());
                    int rowsUpdated = stmt.executeUpdate();
                    return rowsUpdated > 0;
                }
            } else {
                // INSERT
                try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, event.getEventName());
                    stmt.setDouble(2, event.getParticipationFee());
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

}
