package logic;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event class test")
public class EventTest {

    private Participant david;
    private Participant alice;
    private Category food;
    private Category drinks;
    private Category gas;
    private Event event;

    @BeforeEach
    void setUp() {
        david = new Participant("David");
        alice = new Participant("Alice");

        food = new Category("Food");
        drinks = new Category("Drinks");
        gas = new Category("Gas");

        david.addExpense(food, 100.0);
        david.addExpense(drinks, 0.0); // Should not appear because it's 0
        alice.addExpense(food, 50.0);

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
    @DisplayName("Test that fillExpensePerCategory correctly fills expenses per category")
    void testFillExpensePerCategory() {

        // Arrange
        event.fillExpensePerCategory();

        // Act
        Map<Category, Map<Participant, Double>> result = event.getExpensePerCategory();

        // Assert - Food category
        assertTrue(result.get(food).containsKey(david));
        assertEquals(100.0, result.get(food).get(david));

        assertTrue(result.get(food).containsKey(alice));
        assertEquals(50.0, result.get(food).get(alice));

        // Assert - Drinks category should be empty
        assertTrue(result.get(drinks).isEmpty());
    }

    @Test
    @DisplayName("Test that fillConsumedPerCategory correctly fills the right participant per category")
    void testFillConsumedPerCategory() {

        // Arrange
        david.addConsumedCategory(food);
        alice.addConsumedCategory(food);
        alice.addConsumedCategory(drinks);

        event.fillConsumedPerCategory();

        // Act
        Map<Category, List<Participant>> result = event.getConsumedPerCategory();

        // Assert
        assertTrue(result.get(food).contains(david));
        assertTrue(result.get(food).contains(alice));
        assertTrue(result.get(drinks).contains(alice));

        assertNull(result.get(gas));

    }

    @Test
    @DisplayName("Test that fillTotalExpensePerCategory correctly fills the right total amount of expens per category")
    void testFillTotalExpensePerCategory() {

        // Arrange
        event.fillExpensePerCategory();
        event.fillTotalExpensePerCategory();

        // Act
        Map<Category, Double> result = event.getTotalExpensePerCategory();

        // Assert
        assertEquals(150.0, result.get(food));
    }

}
