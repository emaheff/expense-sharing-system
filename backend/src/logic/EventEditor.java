package logic;

import java.util.*;

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

    public static boolean removeCategory(Event event, Category toRemove) {
        for (Participant participant : event.getParticipants()) {
            if (participant.getExpenses().containsKey(toRemove) || participant.getConsumedCategories().contains(toRemove)) {
                return false; // category is in use
            }
        }
        event.getCategories().remove(toRemove);
        return true;
    }

    public static void renameCategory(Category category, String newName) {
        category.setName(newName);
    }

    public static void addParticipant(Event event, Participant newParticipant) {
        event.getParticipants().add(newParticipant);
    }

    public static boolean removeParticipant(Event event, Participant toRemove) {
        return event.getParticipants().remove(toRemove);
    }

    public static void renameParticipant(Participant participant, String newName) {
        participant.setName(newName);
    }

    public static void editParticipantExpense(Participant p, Category category, double newAmount) {
        if (newAmount <= 0) {
            p.getExpenses().remove(category);
        } else {
            p.getExpenses().put(category, newAmount);
        }
    }

    public static void addParticipationExpense(Participant participant, Category category, double expense) {
        participant.getExpenses().put(category, expense);
    }

    public static void editParticipantConsumption(Participant p, List<Category> newCategories) {
        p.setConsumedCategories(newCategories);
    }
}