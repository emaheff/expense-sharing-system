package shared;

public class EventDto {
    private String name;
    private String date; // ISO string format (e.g., "2025-06-01")
    private double participationFee;

    public EventDto() {}

    public EventDto(String name, String date, double fee) {
        this.name = name;
        this.date = date;
        this.participationFee = fee;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public double getParticipationFee() { return participationFee; }

    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setParticipationFee(double fee) { this.participationFee = fee; }
}
