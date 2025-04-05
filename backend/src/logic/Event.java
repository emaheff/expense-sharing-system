package logic;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventName;
    private double participationFee;
    private List<Category> categories;
    private List<Participant> participants;
    private List<Debt> debts;
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

    public String toString() {
        return String.format("""
                Event name: %s
                Participation fee: %f
                Categories: %s
                Participants: %s
                Debts: %s""", eventName, participationFee, categoriesToString(), participantsToString(), debtsToString());
    }


    // this method set for each category in this event all the participant that consumed a category
    public void setConsumedPerCategory() {
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
    }

    public void setExpensePerCategory() {
        for (Category category: categories) {
            for (Participant participant: participants) {
                for (Category expenseCategory: participant.getExpenses().keySet()) {
                    if (category.equals(expenseCategory)) {
                        category.addSpentParticipant(participant, participant.getExpenses().get(expenseCategory));
                    }
                }
            }
        }
    }

    private String categoriesToString() {
        if (categories.isEmpty()) {
            return "There is no categories in this event";
        }

        StringBuilder result = new StringBuilder(String.format("{%s", categories.get(0).getName()));

        for (int i = 1; i < categories.size(); i++) {
            result.append(", ").append(categories.get(i).getName());
        }
        result.append("}");
        return result.toString();
    }

    private String participantsToString() {
        if (participants.isEmpty()) {
            return "There is no participants in this event";
        }

        StringBuilder result = new StringBuilder(String.format("{%s", participants.get(0).getName()));

        for (int i = 1; i < participants.size(); i++) {
            result.append(", ").append(participants.get(i).getName());
        }
        result.append("}");
        return result.toString();
    }

    private String  debtsToString () {
        if (debts.isEmpty()) {
            return "No one owes money to no one";
        }

        StringBuilder result = new StringBuilder(String.format("{%s", debts.get(0)));

        for (int i = 1; i < debts.size(); i++) {
            result.append(", ").append((debts.get(i)));
        }

        result.append("}");
        return result.toString();
    }
}
