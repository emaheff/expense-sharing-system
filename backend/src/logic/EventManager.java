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

    public void editEvent(Event event) {}

    public Event loadEvent(String eventName) {
        return null;
    }

    public void saveEventToFile(Event event) {}

    public void switchEvent(Event event) {}

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public List<Event> getEvents() {
        return events;
    }

}
