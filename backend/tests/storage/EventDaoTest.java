package storage;

import logic.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventDao class test")
public class EventDaoTest {

    private Event event1;
    private Event event2;
    private boolean success;


    @BeforeEach
    void setUp() {
        // Arrange
        event1 = new Event("Hiking", 10.0, LocalDate.of(2023, 8, 5));
        event2 = new Event("Picnic", 15.0, LocalDate.of(2024, 3, 5));
        success = EventDao.insertOrUpdateEvent(event1);
        EventDao.insertOrUpdateEvent(event2);
    }

    @AfterEach
    void tearDown() {
        if (event1 != null)
            EventDao.deleteEventById(event1.getId());
        if (event2 != null)
            EventDao.deleteEventById(event2.getId());
    }

    @Test
    @DisplayName("Verify event insertion (new event is saved correctly)")
    void testInsertOrUpdateEvent() {
        // Try inserting the event
        assertTrue(success, "Event should be inserted successfully");
        assertTrue(event1.getId() > 0, "Inserted event should have a generated ID");

        // Try fetching it back
        Event loaded = EventDao.loadEventById(event1.getId());
        assertNotNull(loaded, "Loaded event should not be null");
        assertEquals(event1.getEventName(), loaded.getEventName());
        assertEquals(event1.getParticipationFee(), loaded.getParticipationFee());
        assertEquals(event1.getDate(), loaded.getDate());
    }

    @Test
    void testGetAllEvents_returnsCorrectSummaries() {
        // Act
        List<EventDao.EventSummary> summaries = EventDao.getAllEvents();

        // Assert
        List<String> eventNames = summaries.stream().map(EventDao.EventSummary::getName).toList();

        assertTrue(eventNames.contains("Picnic"));
        assertTrue(eventNames.contains("Hiking"));
    }

    @Test
    @DisplayName("Load event by ID returns correct event")
    void testLoadEventById() {
        // Act
        Event loaded = EventDao.loadEventById(event2.getId());

        // Assert
        assertNotNull(loaded, "Loaded event should not be null");
        assertEquals(event2.getId(), loaded.getId(), "Loaded event ID should match");
        assertEquals(event2.getEventName(), loaded.getEventName(), "Event name should match");
        assertEquals(event2.getParticipationFee(), loaded.getParticipationFee(), 0.001, "Participation fee should match");
        assertEquals(event2.getDate(), loaded.getDate(), "Event date should match");
    }

    @Test
    @DisplayName("Delete event by ID removes event from database")
    void testDeleteEventById() {
        // Act
        boolean deleted = EventDao.deleteEventById(event1.getId());

        // Assert
        assertTrue(deleted, "Event should be deleted successfully");

        Event shouldBeNull = EventDao.loadEventById(event1.getId());
        assertNull(shouldBeNull, "Event should not exist after deletion");

        event1 = null;
    }

    @Test
    @DisplayName("Deleting non-existent event returns false")
    void testDeleteNonExistentEvent() {
        int nonExistentId = 99999;

        boolean result = EventDao.deleteEventById(nonExistentId);

        assertFalse(result, "Deleting a non-existent event should return false");
    }
}