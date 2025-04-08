package logic;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private List<Event> events;
    private Event currentEvent;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void createEvent(Event event) {
        events.add(event);
        currentEvent = event;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }


}
