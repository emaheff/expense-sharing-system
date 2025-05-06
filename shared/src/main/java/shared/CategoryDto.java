package shared;

import java.util.Objects;

public class CategoryDto {
    private String name;

    public CategoryDto() {}

    public CategoryDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks equality between this category and another object.
     * If both categories have the same name - return true.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDto)) return false;
        CategoryDto other = (CategoryDto) o;
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
        return Objects.hash(name);
    }
}
