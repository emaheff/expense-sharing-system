package storage;

import logic.Category;
import logic.Event;
import logic.Participant;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantDaoTest {

    private Event event;
    private Participant p1;
    private Participant p2;

    @BeforeEach
    void setUp() {
        event = new Event("Test Event", 20.0, LocalDate.of(2025, 4, 20));

        p1 = new Participant("Alice");
        p1.setPhoneNumber("0501234567");
        p1.setEmail("alice@example.com");

        p2 = new Participant("Bob");
        p2.setPhoneNumber("0507654321");
        p2.setEmail("bob@example.com");

        event.addParticipant(p1);
        event.addParticipant(p2);

        EventDao.insertOrUpdateEvent(event);
        ParticipantDao.saveEventParticipants(event);
    }

    @AfterEach
    void tearDown() {
        EventDao.deleteEventById(event.getId());
        p1 = null;
        p2 = null;
    }

    @Test
    @DisplayName("Participants are correctly saved and linked to event")
    void testSaveAndLoadParticipants() {
        List<Participant> loadedParticipants = ParticipantDao.getParticipantsForEvent(event.getId());
        assertEquals(2, loadedParticipants.size(), "Should load 2 participants");

        boolean hasAlice = loadedParticipants.stream().anyMatch(p -> p.getName().equals("Alice"));
        boolean hasBob = loadedParticipants.stream().anyMatch(p -> p.getName().equals("Bob"));

        assertTrue(hasAlice);
        assertTrue(hasBob);
    }

    @Test
    @DisplayName("getParticipantsForEvent loads participants with correct consumption and expenses")
    void testGetParticipantsForEvent() {

        // Arrange
        Category food = new Category("Food");
        event.addCategory(food);
        List<Category> consumedCategories = new ArrayList<>();
        consumedCategories.add(food);
        p1.setConsumedCategories(consumedCategories);
        p1.addExpense(food, 50.0);


        EventDao.insertOrUpdateEvent(event);
        CategoryDao.saveEventCategories(event);
        ParticipantDao.saveEventParticipants(event);

        ExpenseDao.saveEventExpenses(event);
        ExpenseDao.saveEventConsumptions(event);

        // Act
        List<Participant> loaded = ParticipantDao.getParticipantsForEvent(event.getId());

        // Assert
        assertEquals(2, loaded.size(), "Should load 2 participant");
        Participant loadedP = loaded.stream()
                .filter(p -> p.getName().equals("Alice"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Alice not found"));

        assertEquals("Alice", loadedP.getName());
        assertTrue(loadedP.getConsumedCategories().contains(food), "Should contain category 'Food'");
        assertEquals(50.0, loadedP.getExpenses().get(food), 0.01, "Should have expense of 50 on 'Food'");
    }

    @Test
    @DisplayName("findParticipantById returns the correct participant by ID")
    void testFindParticipantById() {
        // Arrange
        List<Participant> participants = ParticipantDao.getParticipantsForEvent(event.getId());
        assertFalse(participants.isEmpty(), "Participants list should not be empty");

        Participant expected = participants.get(0);
        int knownId = expected.getId();

        // Act
        Participant found = ParticipantDao.findParticipantById(participants, knownId);

        // Assert
        assertNotNull(found, "Participant should be found");
        assertEquals(expected.getId(), found.getId());
        assertEquals(expected.getName(), found.getName());
    }

}
