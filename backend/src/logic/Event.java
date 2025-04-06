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
    private Map<Category, Double> expensePerCategory;
    private Map<Category, List<Participant>> consumedPerCategory;
    private boolean isFinalized;
    private boolean isDraft;

    public Event(String name, double participationFee, boolean isDraft) {
        this.eventName = name;
        this.participationFee = participationFee;
        this.isDraft = isDraft;
        this.isFinalized = false;
        this.categories = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.debts = new ArrayList<>();
        this.expensePerCategory = new HashMap<>();
        this.consumedPerCategory = new HashMap<>();
    }


    // adds new participant to participants list
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    // removes a participant from participants list
    public void removeParticipant(Participant participant) {}

    public void addCategory(Category category) {
        categories.add(category);
    }

    //
    public List<Debt> calculateBalances() {
        return null;
    }

    // adds participant fee for the event participation
    public void applyParticipantFees() {}

    public String getEventName() {
        return eventName;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public boolean isDraft() {
        return isDraft;
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

    public List<Debt> getDebts(){
        return debts;
    }

    public double getParticipationFee() {
        return participationFee;
    }

    public Category getCategoryByName(String categoryName) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        return null;
    }




    public String toString() {
        return String.format("""
                === Event Summary ===
                Event name: %s
                Participation Fee: %f
                
                --- Categories and Total Expenses ---
                %s
                
                --- Participants: Expense and Consumption ---
                %s
                
                --- Debts ---
                %s""", eventName, participationFee, categoriesAndTotalExpensesToString(), participantsExpensesAndConsumptionToString(), debtsToString());
    }


    // this method set for each category in this event all the participant that consumed a category
    public void setParticipantsConsumedPerCategory() {
        // iterate throw all the categories in this event
        for (Category category: categories) {
            // for each category, iterate throw all participants in this event
            for (Participant participant: participants) {
                // for each category that a participant consumed
                for (Category consumedCategory: participant.getConsumedCategories()) {
                    // if this event category is equals to the consumed category then add the participant to
                    // a list of participants that consumed this category.
                    if (category.equals(consumedCategory)) {
                        category.addConsumedParticipant(participant);
                    }
                }
            }
        }
        fillConsumedPerCategory();
    }

    public void setParticipantsExpensePerCategory() {
        for (Category category: categories) {
            for (Participant participant: participants) {
                for (Category expenseCategory: participant.getExpenses().keySet()) {
                    if (category.equals(expenseCategory)) {
                        category.addSpentParticipant(participant, participant.getExpenses().get(expenseCategory));
                    }
                }
            }
        }
        fillExpensePerCategoryMap();
    }

    public void setExpensePerCategory(Map<Category, Double> expenseMap) {
        this.expensePerCategory = expenseMap;
    }

    private void fillExpensePerCategoryMap() {
        for (Category category: categories) {
            if (category.getName().equalsIgnoreCase("ParticipationFee")) {
                expensePerCategory.put(category, participationFee * participants.size());
            } else {
                expensePerCategory.put(category, category.getTotalExpense());
            }
        }
    }

    private void fillConsumedPerCategory() {
        for (Category category: categories) {
            consumedPerCategory.put(category, category.getConsumedParticipants());
        }
    }


    private String categoriesAndTotalExpensesToString() {
        StringBuilder result = new StringBuilder();

        for (Category category: categories) {
            result.append(category.getName()).append(": ").append(category.getTotalExpense()).append("\n");
        }
        return result.toString();
    }

    private String participantsExpensesAndConsumptionToString() {
        StringBuilder result = new StringBuilder();
        for (Participant participant: participants) {
            result.append(participant.getName()).append("\n\t").append(" - Expenses:\n\t\t");
            for (Category expenseCategory: participant.getExpenses().keySet()) {
                result.append(expenseCategory.getName()).append(": ").append(participant.getExpenses().get(expenseCategory)).append(", ");
            }
            result.append("\n\t- Consumed:\n\t\t");
            for (Category consumedCategory: participant.getConsumedCategories()) {
                result.append(consumedCategory.getName()).append(", ");
            }
            double totalConsumed = 0.0;
            for (Category totalCategory: expensePerCategory.keySet()) {
                if (participant.getConsumedCategories().contains(totalCategory)) {
                    totalConsumed += expensePerCategory.get(totalCategory);
                }
            }
            result.append("\n\t- Total Paid: ").append(participant.getTotalExpense())
                    .append("\n\t- Total Consumed: ").append(totalConsumed).append("\n\n");
        }
        return result.toString();
    }

    private String  debtsToString () {
        if (debts.isEmpty()) {
            return "No one owes money to no one";
        }

        StringBuilder result = new StringBuilder(String.format("%s", debts.get(0)));

        for (int i = 1; i < debts.size(); i++) {
            result.append("\n").append((debts.get(i)));
        }

        result.append("}");
        return result.toString();
    }
}
