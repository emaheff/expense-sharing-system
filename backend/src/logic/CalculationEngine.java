package logic;

import java.util.*;

public class CalculationEngine {

    public void calculateBalances(Event event) {

        // Step 1: Calculate total expenses and total consumption per participant
        Map<Participant, Double> totalExpenseByParticipant = calculateTotalExpensesByParticipant(event);
        Map<Participant, Double> totalConsumedByParticipant =
                calculateTotalConsumedByParticipant(event);

        // Step 2: Determine creditors and debtors based on net balances
        List<Participant> creditors = new ArrayList<>();
        List<Participant> debtors = new ArrayList<>();
        calculateNetBalances(totalExpenseByParticipant, totalConsumedByParticipant, creditors, debtors);

        // Step 3: Generate final list of debts
        event.setDebts(generateDebts(creditors, debtors));
    }

    private Map<Participant, Double> calculateTotalExpensesByParticipant(Event event) {
        Map<Participant, Double> totalExpenseByParticipant = new HashMap<>();
        for (Participant participant : event.getParticipants()) {
            totalExpenseByParticipant.put(participant, participant.getTotalExpense());
        }
        return totalExpenseByParticipant;
    }

    private Map<Participant, Double> calculateTotalConsumedByParticipant(Event event) {
        Map<Participant, Double> totalConsumed = new HashMap<>();

        for (Category category : event.getCategories()) {
            List<Participant> consumers = event.getConsumedPerCategory().get(category);
            Double totalExpense = event.getTotalExpensePerCategory().get(category);

            if (consumers != null && !consumers.isEmpty() && totalExpense != null) {
                double sharePerParticipant = totalExpense / consumers.size();

                for (Participant participant : consumers) {
                    totalConsumed.put(participant,
                            totalConsumed.getOrDefault(participant, 0.0) + sharePerParticipant);
                }
            }
        }

        return totalConsumed;
    }


    private void calculateNetBalances(
            Map<Participant, Double> totalExpensesByParticipant,
            Map<Participant, Double> totalConsumedByParticipant,
            List<Participant> creditors,
            List<Participant> debtors
    ) {
        for (Participant participant : totalExpensesByParticipant.keySet()) {
            double paid = totalExpensesByParticipant.getOrDefault(participant, 0.0);
            double consumed = totalConsumedByParticipant.getOrDefault(participant, 0.0);
            double netBalance = paid - consumed;

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

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Participant creditor = creditors.get(0);
            Participant debtor = debtors.get(0);

            double amountToReceive = creditor.getBalance();
            double amountToPay = -debtor.getBalance();

            double transferAmount = Math.min(amountToReceive, amountToPay);

            debts.add(new Debt(debtor, creditor, transferAmount));

            creditor.setBalance(creditor.getBalance() - transferAmount);
            debtor.setBalance(debtor.getBalance() + transferAmount);

            if (Math.abs(creditor.getBalance()) < 0.1) {
                creditors.remove(0);
            }

            if (Math.abs(debtor.getBalance()) < 0.1) {
                debtors.remove(0);
            }
        }

        return debts;
    }

    public List<Debt> minimizeTransfers(List<Debt> debts) {
        return debts; // TODO: mathematical implementation
    }
}
