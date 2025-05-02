package shared;

public class DebtDto {
    private String from;
    private String to;
    private double amount;

    public DebtDto(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return from + " owes " + to + " " + String.format("%.2f", amount);
    }
}
