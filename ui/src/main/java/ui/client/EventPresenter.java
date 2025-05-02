package ui.client;

import shared.CategoryDto;
import shared.DebtDto;
import shared.EventDto;
import shared.ParticipantDto;

import java.util.List;
import java.util.Map;

public class EventPresenter {

    /**
     * Formats the list of debts into a readable string for display.
     *
     * @param debts the list of debts
     * @return formatted string representing all debts or a message if none exist
     */
    public static String formatDebts(List<DebtDto> debts) {
        if (debts == null || debts.isEmpty()) {
            return "--- Debts ---\nNo one owes money to no one";
        }

        StringBuilder sb = new StringBuilder("--- Debts ---\n");
        for (DebtDto debt : debts) {
            sb.append(debt).append("\n");
        }

        return sb.toString().stripTrailing();
    }

    /**
     * Formats detailed participant information from the event,
     * including expenses, consumptions, total paid, and total consumed.
     *
     * @param event the event containing participants
     * @return a formatted string representing all participants and their details
     */
    public static String formatParticipants(EventDto event) {
        StringBuilder result = new StringBuilder();
        for (ParticipantDto participant : event.getParticipants()) {
            result.append(participant.getName()).append("\n\t- Expenses:\n\t\t");

            for (Map.Entry<CategoryDto, Double> entry : participant.getExpenses().entrySet()) {
                result.append(entry.getKey().getName()).append(": ")
                        .append(entry.getValue()).append(", ");
            }

            result.append("\n\t- Consumed:\n\t\t");
            for (CategoryDto consumedCategory : participant.getConsumedCategories()) {
                result.append(consumedCategory.getName()).append(", ");
            }

            result.append("\n\t- Total Paid: ").append(participant.getTotalExpense());
            double totalConsumed = participant.getTotalConsumed();
            result.append("\n\t- Total Consumed: ").append(String.format("%.2f", totalConsumed)).append("\n\n");
        }

        return result.toString();
    }
}
