package storage;

import logic.Category;
import logic.Event;
import logic.Participant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    private static int getOrCreateCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        try (Connection conn = DatabaseManager.getConnection()) {

            if (category.getId() != 0) {
                String updateSql = "UPDATE categories SET name = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, category.getName());
                    updateStmt.setInt(2, category.getId());
                    int affected = updateStmt.executeUpdate();
                    if (affected > 0) {
                        return category.getId();
                    }

                }
            }

            String selectSql = "SELECT id FROM categories WHERE name = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, category.getName());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int existingId = rs.getInt("id");
                    category.setId(existingId);
                    return existingId;
                }
            }

            String insertSql = "INSERT INTO categories (name) VALUES (?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, category.getName());
                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    category.setId(newId);
                    return newId;
                } else {
                    throw new SQLException("Category insertion failed, no ID returned.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getOrCreateCategory: " + e.getMessage());
            return -1;
        }
    }

    private static void linkCategoryToEvent(int categoryId, int eventId) {
        String sql = "INSERT INTO event_categories (event_id, category_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to link category to event: " + e.getMessage());
        }
    }

    public static void saveEventCategories(Event event) {
        int eventId = event.getId();

        if (eventId == 0) {
            throw new IllegalArgumentException("Event must be saved before linking categories");
        }

        for (Category category: event.getCategories()) {
            try {
                int categoryId = getOrCreateCategory(category);
                linkCategoryToEvent(categoryId, eventId);
            } catch (Exception e) {
                System.err.println("Failed to save/link categories: " + category.getName() + ". Reason: " +e.getMessage());
            }
        }
    }

    public static List<Category> getCategoriesForEvent(int eventId) {
        List<Category> categories = new ArrayList<>();
        String sql = """
        SELECT c.id, c.name
        FROM categories c
        JOIN event_categories ec ON c.id = ec.category_id
        WHERE ec.event_id = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category(rs.getString("name"));
                category.setId(rs.getInt("id"));
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("Failed to load categories for event: " + e.getMessage());
        }

        return categories;
    }

    public static Category findCategoryById(List<Category> categories, int id) {
        for (Category category : categories) {
            if (category.getId() == id) return category;
        }
        return null;
    }

}
