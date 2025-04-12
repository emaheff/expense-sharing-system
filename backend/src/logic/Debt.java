package logic;

public class Debt {

    private Participant debtor;
    private Participant creditor;
    private double amount;

    public Debt(Participant debtor, Participant creditor, double amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
    }

    public Participant getDebtor() {
        return debtor;
    }

    public Participant getCreditor() {
        return creditor;
    }

    public double getAmount() {
        return amount;
    }

    public String toString() {
        return String.format("%s owes %.2f to %s", debtor.getName(), amount, creditor.getName()) ;
    }
}
