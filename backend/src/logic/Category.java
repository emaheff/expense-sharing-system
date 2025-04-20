package logic;

import java.util.*;

public class Category {

    private String name;
    private int id;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() { return id; }

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id) { this.id = id; }

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

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }


    @Override
    public String toString() {
        return name;
    }
}
