package storage;

import logic.Category;
import logic.Event;
import logic.Participant;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseDaoTest {
    private Event event;
    private Participant p1;
    private Participant p2;
    private Category drinks;
    private Category food;

    @BeforeEach
    void setUp() {
        event = new Event("Test Expenses", 30.0, LocalDate.of(2025, 4, 21));

        p1 = new Participant("Alice");
        p1.setPhoneNumber("0500000001");
        p1.setEmail("alice@example.com");

        p2 = new Participant("Bob");
        p2.setPhoneNumber("0500000002");
        p2.setEmail("bob@example.com");

        food = new Category("Food");
        drinks = new Category("Drinks");

        event.addParticipant(p1);
        event.addParticipant(p2);
        event.addCategory(food);
        event.addCategory(drinks);


        p1.addExpense(food, 60.0);
        p2.addExpense(drinks, 40.0);


        p1.setConsumedCategories(List.of(food));
        p2.setConsumedCategories(List.of(drinks));

        EventDao.insertOrUpdateEvent(event);
        ParticipantDao.saveEventParticipants(event);
        CategoryDao.saveEventCategories(event);
    }

    @AfterEach
    void tearDown() {
        EventDao.deleteEventById(event.getId());
    }

    @Test
    @DisplayName("Expenses are saved correctly and match expected values")
    void testSaveEventExpenses() {
        // Act
        ExpenseDao.saveEventExpenses(event);

        // Load participants again to verify expenses were saved and loaded correctly
        List<Participant> loaded = ParticipantDao.getParticipantsForEvent(event.getId());
        Participant loadedAlice = loaded.stream().filter(p -> p.getName().equals("Alice")).findFirst().orElse(null);
        Participant loadedBob = loaded.stream().filter(p -> p.getName().equals("Bob")).findFirst().orElse(null);

        assertNotNull(loadedAlice);
        assertNotNull(loadedBob);

        Map<Category, Double> aliceExpenses = loadedAlice.getExpenses();
        Map<Category, Double> bobExpenses = loadedBob.getExpenses();

        assertEquals(60.0, aliceExpenses.get(food), 0.01, "Alice should have 60 on food");
        assertEquals(40.0, bobExpenses.get(drinks), 0.01, "Bob should have 40 on drinks");
    }

    @Test
    @DisplayName("Consumptions are saved correctly and match expected categories per participant")
    void testSaveEventConsumptions() {
        // Act
        ExpenseDao.saveEventConsumptions(event);

        // Load participants again to verify consumption data
        List<Participant> loaded = ParticipantDao.getParticipantsForEvent(event.getId());
        Participant loadedAlice = loaded.stream().filter(p -> p.getName().equals("Alice")).findFirst().orElse(null);
        Participant loadedBob = loaded.stream().filter(p -> p.getName().equals("Bob")).findFirst().orElse(null);

        assertNotNull(loadedAlice);
        assertNotNull(loadedBob);

        assertTrue(loadedAlice.getConsumedCategories().contains(food), "Alice should have consumed 'Food'");
        assertTrue(loadedBob.getConsumedCategories().contains(drinks), "Bob should have consumed 'Drinks'");
    }
}
