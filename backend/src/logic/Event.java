package logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private String eventName;
    private double participationFee;
    private List<Category> categories;
    private List<Participant> participants;
    private List<Debt> debts;
    private Map<Category, Double> totalExpensePerCategory;
    private Map<Category, List<Participant>> consumedPerCategory;
    private Map<Category, Map<Participant, Double>> expensePerCategory;
    private int id;

    public Event(String name, double participationFee) {
        this.eventName = name;
        this.participationFee = participationFee;
        this.categories = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.totalExpensePerCategory = new HashMap<>();
        this.consumedPerCategory = new HashMap<>();
        this.expensePerCategory = new HashMap<>();
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    // Getters and Setters
    public String getEventName() {
        return eventName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public double getParticipationFee() {
        return participationFee;
    }

    public void setEventName(String name) {
        this.eventName = name;
    }

    public void setParticipationFee(double amount) {
        this.participationFee = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Category, List<Participant>> getConsumedPerCategory() {
        return consumedPerCategory;
    }

    public Map<Category, Map<Participant, Double>> getExpensePerCategory() {
        return expensePerCategory;
    }

    public Map<Category, Double> getTotalExpensePerCategory() {
        return totalExpensePerCategory;
    }

    @Override
    public String toString() {
        return "Event{name='%s', participants=%d}".formatted(eventName, participants.size());
    }


    public void fillExpensePerCategory() {
        expensePerCategory.clear();
        // iterate throw all categories in this event
        for (Category category : categories) {
            Map<Participant, Double> participantExpenses = new HashMap<>();
            // iterate throw all participants in this event
            for (Participant participant : participants) {
                // getExpenses returns Map<Category, Double> - it's all the category that the participant spent money on
                // and the amount of money that he spent on the category
                Double amount = participant.getExpenses().get(category);
                if (amount != null && amount > 0) {
                    // put the amount of money that participant spent money on the current category int participantExpenses map
                    participantExpenses.put(participant, amount);
                }
            }
            expensePerCategory.put(category, participantExpenses);
        }
    }

    public void fillConsumedPerCategory() {
        consumedPerCategory.clear();
        // iterate throw all categories in this event
        for (Category category : categories) {
            List<Participant> consumer = new ArrayList<>();
            // iterate throw all participants in this event
            for (Participant participant : participants) {
                if (participant.getConsumedCategories().contains(category)) {
                    consumer.add(participant);
                }
            }
            consumedPerCategory.put(category, consumer);
        }
    }

    public void fillTotalExpensePerCategory() {
        totalExpensePerCategory.clear();
        for (Map.Entry<Category, Map<Participant, Double>> entry : expensePerCategory.entrySet()) {
            Category category = entry.getKey();
            Map<Participant, Double> participantExpenses = entry.getValue();

            double total = 0.0;
            for (double amount : participantExpenses.values()) {
                total += amount;
            }

            totalExpensePerCategory.put(category, total);
        }
    }

    public void finalizeCalculations() {
        fillConsumedPerCategory();
        fillExpensePerCategory();
        fillTotalExpensePerCategory();
        CalculationEngine engine = new CalculationEngine();
        engine.calculateBalances(this);
    }
}
