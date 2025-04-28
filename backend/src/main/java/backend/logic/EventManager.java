package backend.logic;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    /**
     * Manages a list of events and tracks the currently active event.
     * Typically used as a central access point to switch between or create events.
     */
    private List<Event> events;
    private Event currentEvent;

    /**
     * Constructs an empty EventManager.
     */
    public EventManager() {
        events = new ArrayList<>();
    }

    /**
     * Adds a new event to the manager and sets it as the current event.
     *
     * @param event the new event to add and activate
     */
    public void createEvent(Event event) {
        events.add(event);
        currentEvent = event;
    }

    /**
     * Sets the current active event.
     *
     * @param currentEvent the event to set as current
     */
    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    /**
     * @return the currently active event
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }


}
