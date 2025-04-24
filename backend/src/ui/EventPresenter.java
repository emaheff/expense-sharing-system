package ui;

import logic.Category;
import logic.Debt;
import logic.Event;
import logic.Participant;

import java.util.List;
import java.util.Map;

/**
 * EventPresenter is responsible for formatting and presenting event data to the user.
 * It generates formatted strings for participants and debts, used for UI display.
 */
public class EventPresenter {

    /**
     * Formats detailed participant information from the event,
     * including expenses, consumptions, total paid, and total consumed.
     *
     * @param event the event containing participants
     * @return a formatted string representing all participants and their details
     */
    public static String formatParticipants(Event event) {
        StringBuilder result = new StringBuilder();
        for (Participant participant : event.getParticipants()) {
            result.append(participant.getName()).append("\n\t- Expenses:\n\t\t");

            for (Map.Entry<Category, Double> entry : participant.getExpenses().entrySet()) {
                result.append(entry.getKey().getName()).append(": ")
                        .append(entry.getValue()).append(", ");
            }

            result.append("\n\t- Consumed:\n\t\t");
            for (Category consumedCategory : participant.getConsumedCategories()) {
                result.append(consumedCategory.getName()).append(", ");
            }

            result.append("\n\t- Total Paid: ").append(participant.getTotalExpense());
            double totalConsumed = participant.getTotalConsumed();
            result.append("\n\t- Total Consumed: ").append(String.format("%.2f", totalConsumed)).append("\n\n");
        }

        return result.toString();
    }

    /**
     * Formats the list of debts into a readable string for display.
     *
     * @param debts the list of debts
     * @return formatted string representing all debts or a message if none exist
     */
    public static String formatDebts(List<Debt> debts) {
        if (debts == null || debts.isEmpty()) {
            return "--- Debts ---\nNo one owes money to no one";
        }

        StringBuilder sb = new StringBuilder("--- Debts ---\n");
        for (Debt debt : debts) {
            sb.append(debt).append("\n");
        }

        return sb.toString().stripTrailing();
    }
}
