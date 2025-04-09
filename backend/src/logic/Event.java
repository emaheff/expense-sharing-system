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

    public Map<Category, List<Participant>> getConsumedPerCategory() {
        return consumedPerCategory;
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
        for (Category category : categories) {
            Map<Participant, Double> participantExpenses = new HashMap<>();
            for (Participant participant : participants) {
                Double amount = participant.getExpenses().get(category);
                if (amount != null && amount > 0) {
                    participantExpenses.put(participant, amount);
                }
            }
            expensePerCategory.put(category, participantExpenses);
        }
    }

    public void fillConsumedPerCategory() {
        consumedPerCategory.clear();
        for (Category category: categories) {
            List<Participant> consumer = new ArrayList<>();
            for (Participant participant: participants) {
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


//    public String categoriesAndTotalExpensesToString() {
//        StringBuilder result = new StringBuilder();
//
//        for (Category category: totalExpensePerCategory.keySet()) {
//            result.append(category.getName()).append(": ").append(totalExpensePerCategory.get(category)).append("\n");
//        }
//        return result.toString();
//    }
//
//    public String participantsExpensesAndConsumptionToString() {
//        StringBuilder result = new StringBuilder();
//        for (Participant participant: participants) {
//            result.append(participant.getName()).append("\n\t").append("- Expenses:\n\t\t");
//            for (Category expenseCategory: participant.getExpenses().keySet()) {
//                result.append(expenseCategory.getName()).append(": ").append(participant.getExpenses().get(expenseCategory)).append(", ");
//            }
//            result.append("\n\t- Consumed:\n\t\t");
//            for (Category consumedCategory: participant.getConsumedCategories()) {
//                result.append(consumedCategory.getName()).append(", ");
//            }
//            double totalConsumed = 0.0;
//            for (Category category : totalExpensePerCategory.keySet()) {
//                if (participant.getConsumedCategories().contains(category)) {
//                    Double totalExpense = totalExpensePerCategory.get(category);
//                    List<Participant> consumers = consumedPerCategory.get(category);
//                    if (totalExpense != null && consumers != null && !consumers.isEmpty()) {
//                        totalConsumed += totalExpense / consumers.size();
//                    }
//                }
//            }
//            result.append("\n\t- Total Paid: ").append(participant.getTotalExpense())
//                    .append("\n\t- Total Consumed: ").append(totalConsumed).append("\n\n");
//        }
//        return result.toString();
//    }
//
//    public String  debtsToString () {
//        if (debts.isEmpty()) {
//            return "No one owes money to no one";
//        }
//
//        StringBuilder result = new StringBuilder(String.format("%s", debts.get(0)));
//
//        for (int i = 1; i < debts.size(); i++) {
//            result.append("\n").append((debts.get(i)));
//        }
//
//        result.append("}");
//        return result.toString();
//    }
}
