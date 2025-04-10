package ui;

import logic.Category;
import logic.Debt;
import logic.Event;
import logic.Participant;

import java.util.List;
import java.util.Map;

public class EventPresenter {

    public static String formatParticipants(Event event, Map<Participant, Double> totalConsumedMap) {
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
            double totalConsumed = totalConsumedMap.getOrDefault(participant, 0.0);
            result.append("\n\t- Total Consumed: ").append(String.format("%.2f", totalConsumed)).append("\n\n");
        }

        return result.toString();
    }

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
