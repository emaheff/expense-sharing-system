package logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Participant implements Comparable<Participant> {
    private String name;
    private Map<Category, Double> expenses;
    private List<Category> consumedCategories;
    private double balance;

    public Participant(String name) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
        this.balance = 0.0;
    }

    public void addExpense(Category category, Double amount) {
        expenses.put(category, amount);
    }

    public void addConsumedCategory(Category category) {
        consumedCategories.add(category);
    }

    public String getName() {
        return name;
    }

    public Map<Category, Double> getExpenses() {
        return expenses;
    }

    public List<Category> getConsumedCategories() {
        return consumedCategories;
    }

    public double getTotalExpense() {
        double totalExpense = 0.0;
        for (Category category: expenses.keySet()) {
            if (!category.getName().equalsIgnoreCase("Participation Fee")) {
                totalExpense += expenses.get(category);
            }
        }
        return totalExpense;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConsumedCategories(List<Category> consumedCategories) {
        this.consumedCategories = consumedCategories;
    }

    @Override
    public int compareTo(Participant other) {
        return Double.compare(this.balance, other.balance);
    }
}
