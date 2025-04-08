package logic;

public class ParticipantEditor {

    public static void addExpense(Participant participant, Category category, double amount) {
        participant.addExpense(category, amount);
    }

    public static void addConsumedCategory(Participant participant, Category category) {
        participant.addConsumedCategory(category);
    }

    public static void addParticipationFeeConsumption(Participant participant) {
        participant.addConsumedCategory(new Category("Participation Fee"));
    }
}
