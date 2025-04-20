package storage;

import logic.Category;
import logic.Event;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryDaoTest {

    private Event event;
    private Category food;
    private Category drinks;

    @BeforeEach
    void setUp() {
        event = new Event("Category Test Event", 10.0, LocalDate.of(2025, 4, 21));

        food = new Category("Food");
        drinks = new Category("Drinks");

        event.addCategory(food);
        event.addCategory(drinks);

        EventDao.insertOrUpdateEvent(event);
        CategoryDao.saveEventCategories(event);
    }

    @AfterEach
    void tearDown() {
        EventDao.deleteEventById(event.getId());
    }

    @Test
    @DisplayName("Categories are saved and loaded correctly for an event")
    void testSaveAndLoadCategories() {
        List<Category> loaded = CategoryDao.getCategoriesForEvent(event.getId());

        assertEquals(2, loaded.size(), "Should load 2 categories");

        boolean hasFood = loaded.stream().anyMatch(c -> c.getName().equals("Food"));
        boolean hasDrinks = loaded.stream().anyMatch(c -> c.getName().equals("Drinks"));

        assertTrue(hasFood);
        assertTrue(hasDrinks);
    }

    @Test
    @DisplayName("findCategoryById returns correct category")
    void testFindCategoryById() {
        List<Category> loaded = CategoryDao.getCategoriesForEvent(event.getId());
        assertFalse(loaded.isEmpty(), "Loaded category list should not be empty");

        Category expected = loaded.get(0);
        int knownId = expected.getId();

        Category found = CategoryDao.findCategoryById(loaded, knownId);
        assertNotNull(found, "Category should be found");
        assertEquals(expected.getId(), found.getId());
        assertEquals(expected.getName(), found.getName());
    }

    @Test
    @DisplayName("findCategoryById returns null for unknown ID")
    void testFindCategoryById_notFound() {
        List<Category> loaded = CategoryDao.getCategoriesForEvent(event.getId());
        Category notFound = CategoryDao.findCategoryById(loaded, -999);
        assertNull(notFound, "Should return null for nonexistent category ID");
    }
}