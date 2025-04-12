package logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Category class test")
public class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Food");
    }

    @AfterEach
    void tearDown() {
        category = null;
    }

    @Test
    @DisplayName("Check if category created in the right way")
    void testCreateCategory() {
        assertEquals("Food", category.getName());
    }

    @Test
    @DisplayName("Check if equals method works fine")
    void testEqualsCategory() {
        Category food = new Category("Food");

        assertEquals(category, food);
    }
}