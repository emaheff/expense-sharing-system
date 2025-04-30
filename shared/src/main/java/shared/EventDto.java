package shared;

import java.time.LocalDate;
import java.util.List;

public class EventDto {
    private String name;
    private LocalDate date;
    private double participationFee;
    private List<ParticipantDto> participants;
    private List<CategoryDto> categories;

    public EventDto() {}

    public EventDto(String name, LocalDate date, double participationFee,
                    List<ParticipantDto> participants, List<CategoryDto> categories) {
        this.name = name;
        this.date = date;
        this.participationFee = participationFee;
        this.participants = participants;
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getParticipationFee() {
        return participationFee;
    }

    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    // setters omitted for brevity but can be added as needed
}
