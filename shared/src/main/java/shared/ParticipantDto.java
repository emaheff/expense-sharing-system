package shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantDto {
    private String name;
    private String phone;
    private List<String> consumedCategories;
    private Map<String, Double> expenses;
    private double totalExpense;
    private double totalConsumed;
    private int id;

    public ParticipantDto() {}  // required for deserialization

    public ParticipantDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
        consumedCategories = new ArrayList<>();
        expenses = new HashMap<>();
    }

    public void setConsumedCategories(List<String> consumedCategories) {
        this.consumedCategories = consumedCategories;
    }

    public void setExpenses(Map<String, Double> expenses) {
        this.expenses = expenses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConsumedCategories() {
        return consumedCategories;
    }

    public Map<String, Double> getExpenses() {
        return expenses;
    }

    public void addConsumedCategory(String category) {
        consumedCategories.add(category);
    }

    public void addExpense(String category, double amount) {
        expenses.put(category, amount);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getTotalConsumed() {
        return totalConsumed;
    }

    public void setTotalConsumed(double totalConsumed) {
        this.totalConsumed = totalConsumed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Participant name: " + name + ". Phone number: " + phone;
    }
}
