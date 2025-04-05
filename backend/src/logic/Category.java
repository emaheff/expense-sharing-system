package logic;

import java.util.*;

public class Category {

    private String name;
    private List<Participant> consumedParticipants;
    private Map<Participant, Double> spentParticipants;

    public Category(String name) {
        this.name = name;
        consumedParticipants = new ArrayList<>();
        spentParticipants = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Participant> getConsumedParticipants() {
        return consumedParticipants;
    }

    public Map<Participant, Double> getSpentParticipants() {
        return spentParticipants;
    }

    public void addConsumedParticipant(Participant participant) {
        consumedParticipants.add(participant);
    }

    public void addSpentParticipant(Participant participant, double amount) {
        spentParticipants.put(participant, amount);
    }

    private double totalExpense() {
        double totalSpent = 0.0;
        for (Double amount: spentParticipants.values()) {
            totalSpent += amount;
        }
        return totalSpent;
    }

    public double getExpensePerParticipant() {
        if (consumedParticipants.isEmpty()) {
            return 0.0;
        }
        return totalExpense() / consumedParticipants.size();
    }

    public double getTotalExpense() {
        double totalExpense = 0.0;
        for (Double amount: spentParticipants.values()) {
            totalExpense += amount;
        }
        return totalExpense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
