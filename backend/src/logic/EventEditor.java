package logic;

public class EventEditor {

    public static void renameEvent(Event event, String newName) {
        event.setEventName(newName);
    }

    public static void setParticipationFee(Event event, double newFee) {
        event.setParticipationFee(newFee);
    }

    public static void addCategory(Event event, Category newCategory) {
        event.getCategories().add(newCategory);
    }

    public static void addParticipantConsumption(Participant participant, Category newCategory) {
        participant.addConsumedCategory(newCategory);
    }

    public static boolean removeParticipant(Event event, Participant toRemove) {
        return event.getParticipants().remove(toRemove);
    }

    public static void renameParticipant(Participant participant, String newName) {
        participant.setName(newName);
    }

    public static void addParticipationExpense(Participant participant, Category category, double expense) {
        participant.getExpenses().put(category, expense);
    }
}