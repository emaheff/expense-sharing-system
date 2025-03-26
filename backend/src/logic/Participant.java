package logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Participant {
    private String name;
    private Map<Category, Double> expenses;
    private List<Category> consumedCategories;

    public Participant(String name) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
    }

    public void addExpense(Category category, Double amount) {}

    public void consumedCategory(Category category) {}

    public void editExpense(Category category, Double newAmount) {}
}
