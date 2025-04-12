package ui;

import logic.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CategoryInteractionHandler class tests")
class CategoryInteractionHandlerTest {

    @Test
    @DisplayName("Check that getCategoryFromNumber returns the correct category and removes it from the list")
    void testGetCategoryFromNumber() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Food"));
        categories.add(new Category("Drinks"));
        categories.add(new Category("Gas"));

        Category chosenCategory = CategoryInteractionHandler.getCategoryFromNumber(2, categories);

        assertEquals("Drinks", chosenCategory.getName());
        assertEquals(2, categories.size());
        assertFalse(categories.contains(chosenCategory));
    }
}
