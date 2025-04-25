package logic;
import java.util.*;

/**
 * Represents a participant in an event, with associated expenses, contact info, and consumption data.
 */
public class Participant implements Comparable<Participant> {
    private String name;
    private Map<Category, Double> expenses;
    private List<Category> consumedCategories;
    private double totalConsumed;
    private double balance;
    private String email;
    private String phoneNumber;
    private int id;

    /**
     * Constructs a Participant with the given name and phone number.
     *
     * @param name the participant's name
     * @param phoneNumber the participant's phone number
     */
    public Participant(String name, String phoneNumber) {
        this.name = name;
        this.expenses = new HashMap<>();
        this.consumedCategories = new ArrayList<>();
        this.balance = 0.0;
        email = "";
        this.phoneNumber = phoneNumber;
        totalConsumed = 0.0;
    }

    /**
     * Constructs a Participant with the given name. Phone number will be empty.
     *
     * @param name the participant's name
     */
    public Participant(String name) {
        this(name, "");
    }

    /**
     * Adds an expense in a specific category.
     *
     * @param category the category of the expense
     * @param amount the amount spent
     */
    public void addExpense(Category category, Double amount) {
        if (amount < 0.1)
            return;
        expenses.put(category, amount);
    }

    /**
     * Adds a consumed category for this participant.
     *
     * @param category the consumed category
     */
    public void addConsumedCategory(Category category) {
        consumedCategories.add(category);
    }

    /**
     * @return the participant's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the participant's email
     */
    public String getEmail() {return email;}

    /**
     * @return the participant's phone number
     */
    public String getPhoneNumber() {return phoneNumber;}

    /**
     * @return a map of expenses by category
     */
    public Map<Category, Double> getExpenses() {
        return expenses;
    }

    /**
     * @return a list of consumed categories
     */
    public List<Category> getConsumedCategories() {
        return consumedCategories;
    }

    /**
     * Calculates the total amount of money spent by the participant.
     *
     * @return the total expenses
     */
    public double getTotalExpense() {
        double totalExpense = 0.0;
        for (Category category: expenses.keySet()) {
           totalExpense += expenses.get(category);
        }
        return totalExpense;
    }

    /**
     * Sets the participant's balance (e.g., debt or credit).
     *
     * @param balance the updated balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * @return the participant's current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @return the participant's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Updates the participant's name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the participant's email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Updates the participant's phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the participant's ID.
     *
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the list of consumed categories.
     *
     * @param consumedCategories the new consumed categories list
     */
    public void setConsumedCategories(List<Category> consumedCategories) {
        this.consumedCategories = consumedCategories;
    }

    /**
     * Sets the total consumption value.
     *
     * @param totalConsumed the total value of consumption
     */
    public void setTotalConsumed(double totalConsumed) {
        this.totalConsumed = totalConsumed;
    }

    /**
     * @return the total consumption value
     */
    public double getTotalConsumed() {
        return totalConsumed;
    }


    /**
     * Equality check for Participant. Uses ID if available, otherwise compares phone number.
     *
     * @param o the object to compare with
     * @return true if the same participant
     */
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

    /**
     * Computes the hash code for this participant. Prefers ID if present.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        if (id != 0) {
            return Objects.hash(id);
        }
        return Objects.hash(phoneNumber, email);
    }

    /**
     * Compares participants by balance for sorting.
     *
     * @param other the other participant to compare
     * @return comparison result by balance
     */
    @Override
    public int compareTo(Participant other) {
        return Double.compare(this.balance, other.balance);
    }

    /**
     * @return a string summary of the participant
     */
    @Override
    public String toString() {
        return "Participant name: " + name + "\nParticipant id: " + id;
    }
}
