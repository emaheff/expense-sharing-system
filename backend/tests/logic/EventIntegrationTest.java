package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        david = new Participant("David");
        alice = new Participant("Alice");

        food = new Category("Food");
        drinks = new Category("Drinks");

        david.addExpense(food, 100.0);
        alice.addExpense(food, 50.0);

        david.addConsumedCategory(food);
        alice.addConsumedCategory(food);

        event = new Event("BBQ", 10.0);
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
    @DisplayName("Check latestTotalConsumed from CalculationEngine")
    void testLatestTotalConsumedMap() {
        event.finalizeCalculations();

        CalculationEngine engine = new CalculationEngine();
        engine.calculateBalances(event);

        Map<Participant, Double> latestTotalConsumed = engine.getLatestConsumptionMap();

        assertEquals(2, latestTotalConsumed.size());
        assertTrue(latestTotalConsumed.containsKey(david));
        assertTrue(latestTotalConsumed.containsKey(alice));

        double davidConsumption = latestTotalConsumed.get(david);
        double aliceConsumption = latestTotalConsumed.get(alice);

        assertTrue(davidConsumption > 0);
        assertTrue(aliceConsumption > 0);
    }
}
