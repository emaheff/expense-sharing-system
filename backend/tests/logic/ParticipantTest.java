package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Participant class Tests")
class ParticipantTest {

    private Participant participant;
    @BeforeEach
    void setUp() {
        participant = new Participant("David");
    }

    @AfterEach
    void tearDown() {
        participant = null;
    }

    @Test
    @DisplayName("Check if participant created in the right way")
    void testCreateParticipant() {
        assertEquals("David", participant.getName());
    }

    @Test
    @DisplayName("Check if adding expense for participant works fine")
    void testAddExpense() {
        Category food = new Category("Food");
        participant.addExpense(food, 100.0);

        Map<Category, Double> expenses = participant.getExpenses();
        assertEquals(100.0, expenses.get(food));
    }

    @Test
    @DisplayName("Check if getTotalExpenses works fine")
    void testGetTotalExpenses() {
        Category food = new Category("Food");
        Category drinks = new Category("Drinks");

        participant.addExpense(food, 50.0);
        participant.addExpense(drinks, 30.0);

        double totalExpense = participant.getTotalExpense();
        assertEquals(80.0, totalExpense);
    }

    @Test
    @DisplayName("Checks adding consumption category to participant")
    void testAddConsumedCategory() {
        Category food = new Category("Food");

        participant.addConsumedCategory(food);

        assertTrue(participant.getConsumedCategories().contains(food));
    }

    @Test
    @DisplayName("Checks compareTo in participant for different balance")
    void testCompareToDifferentBalance() {
        Participant participant1 = new Participant("Elad");
        Participant participant2 = new Participant("Yosi");

        participant1.setBalance(1.0);
        participant2.setBalance(2.0);

        assertTrue(participant1.compareTo(participant2) < 0);
        assertTrue(participant2.compareTo(participant1) > 0);
    }

    @Test
    @DisplayName("Checks compareTo in participant for equal balance")
    void testCompareToEqualBalance() {
        Participant participant1 = new Participant("Elad");
        Participant participant2 = new Participant("Yosi");

        participant1.setBalance(1.0);
        participant2.setBalance(1.0);

        assertEquals(0, participant1.compareTo(participant2));
        assertEquals(0, participant2.compareTo(participant1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"David", "Alice", "Bob", "Emmanuel"})
    @DisplayName("Checks if the name that sends to the constructor saved fine")
    void testCreateParticipantWithDifferentNames(String name) {
        Participant p = new Participant(name);
        assertEquals(name, p.getName());
    }
}
