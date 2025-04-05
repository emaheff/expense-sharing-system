package logic;

public class Debt {

    private Participant from;
    private Participant to;
    private double amount;

    public Debt(Participant from, Participant to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Participant getFrom() {
        return from;
    }

    public Participant getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public String toString() {
        return String.format("%s owes %f to %s", from.getName(), amount, to.getName()) ;
    }
}
