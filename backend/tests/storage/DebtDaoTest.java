package storage;

import logic.Category;
import logic.Debt;
import logic.Event;
import logic.Participant;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DebtDaoTest {

    private Event event;
    private Participant alice;
    private Participant bob;
    private Category food;

    @BeforeEach
    void setUp() {

        event = new Event("Debt Test Event", 0.0, LocalDate.of(2025, 4, 21));

        alice = new Participant("Alice");
        alice.setPhoneNumber("0501234567");

        bob = new Participant("Bob");
        bob.setPhoneNumber("0507654321");

        food = new Category("Food");

        event.addParticipant(alice);
        event.addParticipant(bob);

        event.addCategory(food);
        EventDao.insertOrUpdateEvent(event);
        CategoryDao.saveEventCategories(event);


        List<Category> consumedCategories = new ArrayList<>();
        consumedCategories.add(food);
        bob.setConsumedCategories(consumedCategories);
        alice.addExpense(food, 100.0);


        EventDao.insertOrUpdateEvent(event);
        ParticipantDao.saveEventParticipants(event);
        CategoryDao.saveEventCategories(event);
        ExpenseDao.saveEventExpenses(event);
        ExpenseDao.saveEventConsumptions(event);


        event.finalizeCalculations();
        DebtDao.saveEventDebts(event);
    }

    @AfterEach
    void tearDown() {
        EventDao.deleteEventById(event.getId());
    }

    @Test
    @DisplayName("Debts are correctly saved to the database")
    void testSaveEventDebts() {
        List<Debt> debts = DebtDao.getDebtsForEvent(event.getId(), event.getParticipants());
        assertEquals(1, debts.size(), "There should be one debt");

        Debt debt = debts.get(0);
        assertEquals(bob.getId(), debt.getDebtor().getId());
        assertEquals(alice.getId(), debt.getCreditor().getId());
        assertEquals(100.0, debt.getAmount(), 0.01);
    }

    @Test
    @DisplayName("getDebtsForEvent returns empty list if no debts exist")
    void testGetDebtsForEvent_empty() {
        Event newEvent = new Event("Empty Debts", 0.0, LocalDate.of(2025, 4, 21));
        EventDao.insertOrUpdateEvent(newEvent);

        List<Debt> debts = DebtDao.getDebtsForEvent(newEvent.getId(), List.of());
        assertTrue(debts.isEmpty(), "Should return empty list for event with no debts");

        EventDao.deleteEventById(newEvent.getId());
    }
}


