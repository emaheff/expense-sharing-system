package storage;

import logic.Category;

import java.sql.*;

public class CategoryDao {

    private static int getOrCreateCategory(Category category) {
        String selectSql = "SELECT id FROM categories WHERE name = ?";
        String insertSql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, category.getName());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int existingId = rs.getInt("id");
                    category.setId(existingId);
                    return existingId;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, category.getName());

                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    category.setId(newId);
                    return newId;
                } else {
                    throw new SQLException("Category insertion failed, no id returned");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getOrCreateCategory: " + e.getMessage());
            return -1;
        }
    }
}
