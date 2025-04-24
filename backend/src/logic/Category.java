package logic;

import java.util.*;

/**
 * Represents a category used in the system (e.g., for organizing expenses or consumptions).
 * Each category has a name and an optional identifier.
 */
public class Category {

    private String name;
    private int id;

    /**
     * Constructs a Category with the specified name.
     *
     * @param name the name of the category
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the category.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ID of the category.
     *
     * @return the category ID
     */
    public int getId() { return id; }

    /**
     * Sets the name of the category.
     *
     * @param name the new name for the category
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Sets the ID of the category.
     *
     * @param id the new ID for the category
     */
    public void setId(int id) { this.id = id; }

    /**
     * Checks equality between this category and another object.
     * If both categories have a non-zero ID, they are compared by ID.
     * Otherwise, they are compared by name.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category other = (Category) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        return Objects.equals(this.name, other.name);
    }

    /**
     * Returns the hash code for the category.
     * Uses the ID if set (non-zero); otherwise, uses the name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }

    /**
     * Returns a string representation of the category.
     * Currently, returns the category name.
     *
     * @return the category name
     */
    @Override
    public String toString() {
        return name;
    }
}
