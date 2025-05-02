package shared;

import java.time.LocalDate;

public class EventSummaryDto {
    private int id;
    private String name;
    private LocalDate date;

    public EventSummaryDto(int id, String name, LocalDate date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }
}
