package backend.logic;

import java.util.*;

/**
 * CalculationEngine handles the logic for computing the financial balances
 * of participants in an event, including expenses, consumption, and resulting debts.
 */
public class CalculationEngine {

    /**
     * Main entry point to compute all balances and debts for the given event.
     * It calculates expenses, adjusts for participation fees, determines balances,
     * and sets the resulting list of debts in the event.
     *
     * @param event the event for which to perform the calculations
     */
    public void calculateBalances(Event event) {
        Map<Participant, Double> totalExpensesByParticipant = calculateTotalExpensesByParticipant(event);


        calculateTotalConsumedByParticipant(event);

        Map<Participant, Double> totalConsumedByParticipant = new HashMap<>();
        for (Participant p : event.getParticipants()) {
            totalConsumedByParticipant.put(p, p.getTotalConsumed());
        }

        updateEventDebts(event, totalExpensesByParticipant, totalConsumedByParticipant);
    }


    /**
     * Updates the list of debts in the event based on net balances between participants.
     */
    private void updateEventDebts(Event event,
                                  Map<Participant, Double> totalExpensesByParticipant,
                                  Map<Participant, Double> totalConsumedByParticipant) {

        List<Participant> creditors = new ArrayList<>();
        List<Participant> debtors = new ArrayList<>();

        calculateNetBalances(
                totalExpensesByParticipant,
                totalConsumedByParticipant,
                creditors,
                debtors,
                event.getParticipationFee(),
                event
        );

        event.setDebts(generateDebts(creditors, debtors));
    }

    /**
     * Calculates the total amount paid by each participant.
     *
     * @param event the event to analyze
     * @return a map of participants to their total expenses
     */
    private Map<Participant, Double> calculateTotalExpensesByParticipant(Event event) {
        Map<Participant, Double> totalExpenseByParticipant = new HashMap<>();
        for (Participant participant : event.getParticipants()) {
            totalExpenseByParticipant.put(participant, participant.getTotalExpense());
        }
        return totalExpenseByParticipant;
    }

    /**
     * Calculates and sets the total amount consumed by each participant,
     * based on category-level adjusted expenses.
     *
     * @param event the event to analyze
     */
    private void calculateTotalConsumedByParticipant(Event event) {
        Map<Category, Double> totalExpensePerCategory = event.getTotalExpensePerCategory();
        List<Participant> participants = event.getParticipants();
        double participationFee = event.getParticipationFee();
        double totalParticipationFee = participationFee * participants.size();

        double totalExpenses = calculateTotalExpenses(totalExpensePerCategory);

        Map<Category, Double> adjustedCategoryExpense =
                calculateAdjustedCategoryExpenses(totalExpensePerCategory, totalParticipationFee, totalExpenses);
        event.setAdjustedTotalExpensePerCategory(adjustedCategoryExpense);

        Map<Participant, Double> totalConsumedMap = calculateParticipantConsumption(adjustedCategoryExpense, event.getConsumedPerCategory());

        // update each participant's field directly
        for (Map.Entry<Participant, Double> entry : totalConsumedMap.entrySet()) {
            entry.getKey().setTotalConsumed(entry.getValue());
        }
    }

    /**
     * Computes the total expenses across all categories.
     */
    private double calculateTotalExpenses(Map<Category, Double> totalExpensePerCategory) {
        return totalExpensePerCategory.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Adjusts category expenses by subtracting proportional participation fee.
     *
     * @param totalExpensePerCategory raw expenses per category
     * @param totalParticipationFee total participation fee for all participants
     * @param totalExpenses sum of all raw expenses
     * @return map of categories to adjusted expenses
     */
    private Map<Category, Double> calculateAdjustedCategoryExpenses(
            Map<Category, Double> totalExpensePerCategory,
            double totalParticipationFee,
            double totalExpenses) {

        Map<Category, Double> adjusted = new HashMap<>();

        for (Map.Entry<Category, Double> entry : totalExpensePerCategory.entrySet()) {
            Category category = entry.getKey();
            double categoryExpense = entry.getValue();
            double proportion = categoryExpense / totalExpenses;
            double subsidy = totalParticipationFee * proportion;
            double adjustedExpense = categoryExpense - subsidy;
            adjusted.put(category, adjustedExpense);
        }
        return adjusted;
    }

    /**
     * Distributes adjusted expenses across participants who consumed each category.
     * Each consumer pays an equal share of the category's adjusted cost.
     */
    private Map<Participant, Double> calculateParticipantConsumption(
            Map<Category, Double> adjustedCategoryExpense,
            Map<Category, List<Participant>> consumedPerCategory) {

        Map<Participant, Double> totalConsumed = new HashMap<>();

        for (Map.Entry<Category, Double> entry : adjustedCategoryExpense.entrySet()) {
            Category category = entry.getKey();
            double expenseToShare = entry.getValue();

            List<Participant> consumers = consumedPerCategory.get(category);
            if (consumers == null || consumers.isEmpty()) continue;

            double sharePerParticipant = expenseToShare / consumers.size();

            for (Participant participant : consumers) {
                totalConsumed.put(
                        participant,
                        totalConsumed.getOrDefault(participant, 0.0) + sharePerParticipant
                );
            }
        }
        return totalConsumed;
    }

    /**
     * Calculates net balances for each participant and classifies them as creditors or debtors.
     * A positive balance indicates a creditor, negative indicates a debtor.
     */
    private void calculateNetBalances(
            Map<Participant, Double> totalExpensesByParticipant,
            Map<Participant, Double> totalConsumedByParticipant,
            List<Participant> creditors,
            List<Participant> debtors, double participationFee,
            Event event
    ) {
        for (Participant participant : event.getParticipants()) {
            double totalPaid = totalExpensesByParticipant.getOrDefault(participant, 0.0);
            double totalConsumed = totalConsumedByParticipant.getOrDefault(participant, 0.0);
            double netBalance = totalPaid - (totalConsumed + participationFee);

            participant.setBalance(netBalance);

            if (netBalance > 0) {
                creditors.add(participant);
            } else if (netBalance < 0) {
                debtors.add(participant);
            }
        }

        // Sort to ensure deterministic order for debt generation
        Collections.sort(creditors);
        Collections.sort(debtors);
    }

    /**
     * Creates a list of debts between debtors and creditors based on their net balances.
     * Transfers minimal amounts until all balances are close to zero.
     */
    private List<Debt> generateDebts(List<Participant> creditors, List<Participant> debtors) {
        List<Debt> debts = new ArrayList<>();

        // Temporary balances to avoid modifying the original participants
        Map<Participant, Double> tempBalances = new HashMap<>();
        creditors.forEach(c -> tempBalances.put(c, c.getBalance()));
        debtors.forEach(d -> tempBalances.put(d, d.getBalance()));

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Participant creditor = creditors.get(0);
            Participant debtor = debtors.get(0);

            double amountToReceive = tempBalances.get(creditor);
            double amountToPay = -tempBalances.get(debtor);

            double transferAmount = Math.min(amountToReceive, amountToPay);

            debts.add(new Debt(debtor, creditor, transferAmount));

            // Update temporary balances instead of real participant balance
            tempBalances.put(creditor, tempBalances.get(creditor) - transferAmount);
            tempBalances.put(debtor, tempBalances.get(debtor) + transferAmount);

            if (Math.abs(tempBalances.get(creditor)) < 0.1) {
                creditors.remove(0);
            }

            if (Math.abs(tempBalances.get(debtor)) < 0.1) {
                debtors.remove(0);
            }
        }

        return debts;
    }
}
