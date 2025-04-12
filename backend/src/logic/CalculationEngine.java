package logic;

import java.util.*;

public class CalculationEngine {
    private Map<Participant, Double> latestTotalConsumed = new HashMap<>();


    public void calculateBalances(Event event) {

        // Step 1: Calculate total expenses and total consumption per participant
        Map<Participant, Double> totalExpenseByParticipant = calculateTotalExpensesByParticipant(event);
        Map<Participant, Double> totalConsumedByParticipant =
                calculateTotalConsumedByParticipant(event);
        this.latestTotalConsumed = totalConsumedByParticipant;

        // Step 2: Determine creditors and debtors based on net balances
        List<Participant> creditors = new ArrayList<>();
        List<Participant> debtors = new ArrayList<>();
        calculateNetBalances(totalExpenseByParticipant, totalConsumedByParticipant, creditors, debtors, event.getParticipationFee(), event);

        // Step 3: Generate final list of debts
        event.setDebts(generateDebts(creditors, debtors));
    }

    public Map<Participant, Double> getLatestConsumptionMap() {
        return latestTotalConsumed;
    }


    private Map<Participant, Double> calculateTotalExpensesByParticipant(Event event) {
        Map<Participant, Double> totalExpenseByParticipant = new HashMap<>();
        for (Participant participant : event.getParticipants()) {
            totalExpenseByParticipant.put(participant, participant.getTotalExpense());
        }
        return totalExpenseByParticipant;
    }

    private Map<Participant, Double> calculateTotalConsumedByParticipant(Event event) {
        Map<Category, Double> totalExpensePerCategory = event.getTotalExpensePerCategory();
        List<Participant> participants = event.getParticipants();
        double participationFee = event.getParticipationFee();
        double totalParticipationFee = participationFee * participants.size();

        double totalExpenses = calculateTotalExpenses(totalExpensePerCategory);

        Map<Category, Double> adjustedCategoryExpense =
                calculateAdjustedCategoryExpenses(totalExpensePerCategory, totalParticipationFee, totalExpenses);

        return calculateParticipantConsumption(adjustedCategoryExpense, event.getConsumedPerCategory());
    }


    private double calculateTotalExpenses(Map<Category, Double> totalExpensePerCategory) {
        return totalExpensePerCategory.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private Map<Category, Double> calculateAdjustedCategoryExpenses(
            Map<Category, Double> totalExpensePerCategory,
            double totalParticipationFee,
            double totalExpenses) {

        Map<Category, Double> adjusted = new HashMap<>();

        for (Map.Entry<Category, Double> entry : totalExpensePerCategory.entrySet()) {
            Category category = entry.getKey();
            double expense = entry.getValue();
            double proportion = expense / totalExpenses;
            double subsidy = totalParticipationFee * proportion;
            double adjustedExpense = expense - subsidy;
            adjusted.put(category, adjustedExpense);
        }



        return adjusted;
    }

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




    private void calculateNetBalances(
            Map<Participant, Double> totalExpensesByParticipant,
            Map<Participant, Double> totalConsumedByParticipant,
            List<Participant> creditors,
            List<Participant> debtors, double participationFee,
            Event event
    ) {
        for (Participant participant : event.getParticipants()) {
            double paid = totalExpensesByParticipant.getOrDefault(participant, 0.0);
            double consumed = totalConsumedByParticipant.getOrDefault(participant, 0.0);
            double netBalance = paid - (consumed + participationFee);

            participant.setBalance(netBalance);

            if (netBalance > 0) {
                creditors.add(participant);
            } else if (netBalance < 0) {
                debtors.add(participant);
            }
        }

        Collections.sort(creditors);
        Collections.sort(debtors);
    }

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
