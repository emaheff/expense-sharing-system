package logic;

import java.util.*;

public class CalculationEngine {

    public List<Debt> calculateBalances(Event event) {
        // Step 1: Calculate total expenses per category
        Map<Category, Double> totalExpensesPerCategory = calculateTotalExpensesPerCategory(event);

        // Step 2: Map consumers per category
        Map<Category, List<Participant>> consumerPerCategory = mapConsumersPerCategory(event);

        // Step 3: Calculate total expenses and total consumption per participant
        Map<Participant, Double> totalExpenseByParticipant = calculateTotalExpensesByParticipant(event);
        Map<Participant, Double> totalConsumedByParticipant =
                calculateTotalConsumedByParticipant(event, totalExpensesPerCategory, consumerPerCategory);

        // Step 4: Determine creditors and debtors based on net balances
        List<Participant> creditors = new ArrayList<>();
        List<Participant> debtors = new ArrayList<>();
        calculateNetBalances(totalExpenseByParticipant, totalConsumedByParticipant, creditors, debtors);

        // Step 5: Generate final list of debts
        return generateDebts(creditors, debtors);
    }


    private Map<Category, Double> calculateTotalExpensesPerCategory(Event event) {
        Map<Category, Double> totalExpensesPerCategory = new HashMap<>();
        for (Participant participant: event.getParticipants()) {
            // for each participant iterate over all the category that the participant spent on and sum it on
            // totalExpensesPerCategory map per key
            for (Category category: participant.getExpenses().keySet()) {
                double valueFromParticipantMap = participant.getExpenses().get(category);
                double currentValueInTotalExpensesPerCategory = totalExpensesPerCategory.getOrDefault(category,0.0);
                totalExpensesPerCategory.put(category,currentValueInTotalExpensesPerCategory + valueFromParticipantMap);
            }
        }
        return totalExpensesPerCategory;
    }

    private Map<Category, List<Participant>> mapConsumersPerCategory(Event event) {
        Map<Category, List<Participant>> consumerPerCategory = new HashMap<>();
        for (Category category: event.getCategories()) {
            List<Participant> categoryConsumedParticipants = new ArrayList<>();
            for (Participant participant: event.getParticipants()) {
                for (Category consumedCategory: participant.getConsumedCategories()) {
                    if (category.equals(consumedCategory)) {
                        categoryConsumedParticipants.add(participant);
                    }
                }
            }
            consumerPerCategory.put(category, categoryConsumedParticipants);
        }
        return consumerPerCategory;
    }

    private Map<Participant, Double> calculateTotalExpensesByParticipant(Event event) {
        Map<Participant, Double> totalExpenseByParticipant = new HashMap<>();
        for (Participant participant : event.getParticipants()) {
            totalExpenseByParticipant.put(participant, participant.getTotalExpense());
        }
        return totalExpenseByParticipant;
    }

    private Map<Participant, Double> calculateTotalConsumedByParticipant(
            Event event,
            Map<Category, Double> totalExpensesPerCategory,
            Map<Category, List<Participant>> consumerPerCategory
    ) {
        Map<Participant, Double> totalConsumedByParticipant = new HashMap<>();

        for (Category category : event.getCategories()) {
            double totalSpent = totalExpensesPerCategory.getOrDefault(category, 0.0);
            List<Participant> consumers = consumerPerCategory.getOrDefault(category, new ArrayList<>());

            int numConsumers = consumers.size();
            if (numConsumers == 0) continue;

            double perParticipantCost = totalSpent / numConsumers;

            for (Participant participant : consumers) {
                double currentTotal = totalConsumedByParticipant.getOrDefault(participant, 0.0);
                totalConsumedByParticipant.put(participant, currentTotal + perParticipantCost);
            }
        }

        return totalConsumedByParticipant;
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


    public void applyParticipationFees(Event event) {
        // splits the participant fee between all categories
    }

    public List<Debt> minimizeTransfers(List<Debt> debts) {
        return debts; // TODO: mathematical implementation
    }
}
