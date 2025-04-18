package storage;

import logic.Event;
import logic.Participant;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public static List<EventSummary> getAllEvents() {
        List<EventSummary> events = new ArrayList<>();
        String sql = "SELECT id, name FROM events ORDER BY date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                events.add(new EventSummary(id, name));
            }

        } catch (SQLException e) {
            System.err.println("Failed to load event list: " + e.getMessage());
        }

        return events;
    }

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

                Event event = new Event(name, fee);
                event.setDate(date);
                event.setId(eventId);
                List<Participant> eventParticipants = ParticipantDao.getParticipantsForEvent(eventId);
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



    public static class EventSummary {
        private final int id;
        private final String name;

        public EventSummary(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return String.format("[ID: %d] %s", id, name);
        }
    }

}
