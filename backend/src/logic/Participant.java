package logic;
import java.util.*;

public class Participant implements Comparable<Participant> {
    private String name;
    private Map<Category, Double> expenses;
    private List<Category> consumedCategories;
    private double balance;
    private String email;
    private String phoneNumber;
    private int id;

    public Participant(String name) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
        this.balance = 0.0;
        email = "";
        phoneNumber = "";
    }

    public void addExpense(Category category, Double amount) {
        if (amount < 0.1)
            return;
        expenses.put(category, amount);
    }

    public void addConsumedCategory(Category category) {
        consumedCategories.add(category);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {return email;}

    public String getPhoneNumber() {return phoneNumber;}

    public Map<Category, Double> getExpenses() {
        return expenses;
    }

    public List<Category> getConsumedCategories() {
        return consumedCategories;
    }

    public double getTotalExpense() {
        double totalExpense = 0.0;
        for (Category category: expenses.keySet()) {
           totalExpense += expenses.get(category);
        }
        return totalExpense;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant other = (Participant) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Participant other) {
        return Double.compare(this.balance, other.balance);
    }
}
