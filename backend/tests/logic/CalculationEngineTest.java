package logic;

import org.junit.jupiter.api.*;

import javax.lang.model.type.PrimitiveType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculationEngine class tests")
class CalculationEngineTest {

    private Participant alice;
    private Participant bob;
    private Participant charlie;
    private Participant david;
    private Participant eve;
    private Category food;
    private Category drinks;
    private Category decoration;
    private Category activity;
    private Event event;
    private CalculationEngine engine;

    @BeforeEach
    void setUp() {
        david = new Participant("David");
        alice = new Participant("Alice");
        bob = new Participant("Bob");
        charlie = new Participant("Charlie");
        eve = new Participant("Eve");

        food = new Category("Food");
        drinks = new Category("Drinks");
        activity = new Category("Activity");
        decoration = new Category("Decorations");

        alice.addExpense(food, 200.0);
        bob.addExpense(drinks, 100.0);
        charlie.addExpense(activity, 150.0);
        david.addExpense(decoration, 50.0);

        alice.addConsumedCategory(food);
        alice.addConsumedCategory(drinks);

        bob.addConsumedCategory(food);
        bob.addConsumedCategory(drinks);

        charlie.addConsumedCategory(food);
        charlie.addConsumedCategory(activity);
        charlie.addConsumedCategory(decoration);


        event = new Event("Birthday Party", 10.0);
        event.addCategory(food);
        event.addCategory(drinks);
        event.addCategory(activity);
        event.addCategory(decoration);

        event.addParticipant(david);
        event.addParticipant(alice);
        event.addParticipant(bob);
        event.addParticipant(charlie);
        event.addParticipant(eve);

        event.fillExpensePerCategory();
        event.fillConsumedPerCategory();
        event.fillTotalExpensePerCategory();

        engine = new CalculationEngine();
    }

    @AfterEach
    void tearDown() {
        david = null;
        alice = null;
        bob = null;
        charlie = null;
        eve = null;

        food = null;
        drinks = null;
        activity = null;
        decoration = null;

        event = null;
        engine = null;
    }

    @Test
    @DisplayName("Check that calculateBalances works correctly and creates debts")
    void testCalculateBalancesCreatesDebts() {
        engine.calculateBalances(event);

        List<Debt> debts = event.getDebts();

        assertNotNull(debts);
        assertFalse(debts.isEmpty());

        for (Debt debt: debts) {
            if (debt.getDebtor().equals(bob)) {
                assertEquals(alice, debt.getCreditor());
                assertEquals(15.0, debt.getAmount());
            } else if (debt.getDebtor().equals(charlie)) {
                if (debt.getCreditor().equals(alice)) {
                    assertEquals(60.0, debt.getAmount());
                } else if (debt.getCreditor().equals(david)) {
                    assertEquals(40.0, debt.getAmount());
                }
            } else {
                assertEquals(alice, debt.getCreditor());
                assertEquals(10.0, debt.getAmount());
            }
        }
    }
}
