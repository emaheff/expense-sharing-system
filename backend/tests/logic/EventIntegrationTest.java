package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration tests for Event and CalculationEngine")
public class EventIntegrationTest {

    private Participant david;
    private Participant alice;
    private Category food;
    private Category drinks;
    private Event event;

    @BeforeEach
    void setUp() {
        david = new Participant("David", "123654123");
        alice = new Participant("Alice", "123653214");

        food = new Category("Food");
        drinks = new Category("Drinks");

        david.addExpense(food, 100.0);
        alice.addExpense(food, 50.0);

        david.addConsumedCategory(food);
        alice.addConsumedCategory(food);

        LocalDate date = LocalDate.of(2023, 8, 5);
        event = new Event("BBQ", 10.0, date);
        event.addCategory(food);
        event.addCategory(drinks);
        event.addParticipant(david);
        event.addParticipant(alice);
    }

    @AfterEach
    void tearDown() {
        david = null;
        alice = null;
        food = null;
        drinks = null;
        event = null;
    }

    @Test
    void testFullEventCalculationFlow() {
        event.finalizeCalculations();


        List<Debt> debts = event.getDebts();
        assertEquals(1, debts.size());

        Debt debt = debts.get(0);
        assertEquals(alice, debt.getDebtor());
        assertEquals(david, debt.getCreditor());
        assertTrue(debt.getAmount() > 0);

        assertTrue(david.getBalance() > 0);
        assertTrue(alice.getBalance() < 0);
    }

    @Test
    @DisplayName("Check totalConsumed field is correctly updated in Participant")
    void testTotalConsumedFieldInParticipants() {
        event.finalizeCalculations();

        //
        double davidConsumption = david.getTotalConsumed();
        double aliceConsumption = alice.getTotalConsumed();

        assertTrue(davidConsumption > 0, "David should have consumed a positive amount");
        assertTrue(aliceConsumption > 0, "Alice should have consumed a positive amount");

        //
        assertNotEquals(0.0, davidConsumption, 0.01);
        assertNotEquals(0.0, aliceConsumption, 0.01);
    }

}
