package logic;

import java.util.List;

public class CalculationEngine {

    public List<Debt> calculateBalances(Event event) {
        return null;
    }

    public void applyParticipationFees(Event event) {
        // splits the participant fee between all categories
    }

    public List<Debt> minimizeTransfers(List<Debt> debts) {
        return debts; // TODO: mathematical implementation
    }
}
