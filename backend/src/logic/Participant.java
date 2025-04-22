package logic;
import java.util.*;

public class Participant implements Comparable<Participant> {
    private String name;
    private Map<Category, Double> expenses;
    private double totalExpenses;
    private List<Category> consumedCategories;
    private double totalConsumed;
    private double balance;
    private String email;
    private String phoneNumber;
    private int id;

    public Participant(String name, String phoneNumber) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
        this.balance = 0.0;
        email = "";
        this.phoneNumber = phoneNumber;
        totalConsumed = 0.0;
    }

    public Participant(String name) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
        this.balance = 0.0;
        email = "";
        this.phoneNumber = "";
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

    public void setConsumedCategories(List<Category> consumedCategories) {
        this.consumedCategories = consumedCategories;
    }

    public void setTotalConsumed(double totalConsumed) {
        this.totalConsumed = totalConsumed;
    }

    public double getTotalConsumed() {
        return totalConsumed;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant)) return false;
        Participant other = (Participant) o;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        return Objects.equals(this.phoneNumber, other.phoneNumber);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(phoneNumber, email);
    }

    @Override
    public int compareTo(Participant other) {
        return Double.compare(this.balance, other.balance);
    }

    @Override
    public String toString() {
        return "Participant name: " + name + " Participant id: " + id;
    }
}
