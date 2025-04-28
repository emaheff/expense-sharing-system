package backend.logic;

/**
 * Represents a debt between two participants in an event.
 * A debt is defined by a debtor (who owes money), a creditor (to whom the money is owed),
 * and the amount owed.
 */
public class Debt {

    private Participant debtor;
    private Participant creditor;
    private double amount;

    /**
     * Constructs a Debt object representing an amount owed from one participant to another.
     *
     * @param debtor   the participant who owes the money
     * @param creditor the participant to whom the money is owed
     * @param amount   the amount of money owed
     */
    public Debt(Participant debtor, Participant creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
    }

    /**
     * @return the participant who owes the money
     */
    public Participant getDebtor() {
        return debtor;
    }

    /**
     * @return the participant to whom the money is owed
     */
    public Participant getCreditor() {
        return creditor;
    }

    /**
     * @return the amount of money owed
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns a string description of the debt in the format:
     * "[debtor name] owes [amount] to [creditor name]".
     *
     * @return formatted string representation of the debt
     */
    public String toString() {
        return String.format("%s owes %.2f to %s", debtor.getName(), amount, creditor.getName()) ;
    }
}
