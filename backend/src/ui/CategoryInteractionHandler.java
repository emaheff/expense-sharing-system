package ui;

import logic.Category;
import logic.Event;
import logic.Participant;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all user interactions related to category management,
 * such as adding, renaming, removing categories and mapping them to participants.
 */
public class CategoryInteractionHandler {

    /**
     * Displays a list of categories and prompts the user to choose one.
     *
     * @param header the message displayed above the category list
     * @param categories the list of categories to display
     * @return the selected Category, or null if the user chose to skip
     */
    public static Category getCategoryFromUser(String header, List<Category> categories) {
        List<Category> categoriesDisplay = new ArrayList<>(categories);
        MenuPrinter.displayNumberedCategories(categoriesDisplay);
        System.out.println(header);
        System.out.println(MenuPrinter.displayNumberedCategories(categoriesDisplay));

        int choice = UserInputHandler.getIntInput("Your choice: ");
        boolean validInput = false;

        while (!validInput) {
            if (choice == 0) {
                return null;
            }
            if (choice >= 1 && choice <= categoriesDisplay.size()) {
                validInput = true;
            } else {
                System.out.println("Please enter a number between 1 and " + categoriesDisplay.size() + ", or 0 to skip.");
            }
        }
        return getCategoryFromNumber(choice, categoriesDisplay);
    }

    /**
     * Gets a category from a numbered list based on user input (1-based index).
     *
     * @param userNumber the number input from the user
     * @param displayedCategories the list of displayed categories
     * @return the selected Category
     */
    public static Category getCategoryFromNumber(int userNumber, List<Category> displayedCategories) {
        Category result = displayedCategories.get(userNumber - 1);
        displayedCategories.remove(userNumber -1);
        return result;
    }

    /**
     * Entry point for category management actions, allows user to choose an action.
     *
     * @param event the event whose categories are being managed
     */
    public static void handleManageCategories(Event event) {
        MenuPrinter.displayManageCategoryMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleManagerCategoryChoice(choice, event);
    }

    /**
     * Handles the specific action based on user choice in the category management menu.
     */
    private static void handleManagerCategoryChoice(int choice, Event event) {
        switch (choice) {
            case 1:
                handleAddNewCategory(event);
                break;
            case 2:
                handleRenameCategory(event);
                break;
            case 3:
                handleRemoveCategory(event);
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Removes a selected category from the event and updates all participants accordingly.
     */
    private static void handleRemoveCategory(Event event) {
        Category categoryToRemove = getCategoryFromUser("Enter Category Number you wants to remove:" +
                " if there are none enter 0", event.getCategories());
        if (categoryToRemove == null) {
            System.out.println("There is no category!");
            return;
        }
        for (Participant participant : event.getParticipants()) {
            participant.getExpenses().keySet().removeIf(expenseCategory -> expenseCategory.equals(categoryToRemove));
            participant.getConsumedCategories().removeIf(consumedCategory -> consumedCategory.equals(categoryToRemove));
        }
    }

    /**
     * Adds a new category to the event and asks user to map it to participant expenses and consumptions.
     */
    private static void handleAddNewCategory(Event event) {
        if (event == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        String newCategoryName = UserInputHandler.getStringInput("Enter a new category that you want to add: ");
        Category newCategory = new Category(newCategoryName);
        event.addCategory(newCategory);
        addNewCategoryToParticipants(newCategory, event);
    }

    /**
     * For each participant, asks whether they spent money or consumed from the newly added category.
     */
    private static void addNewCategoryToParticipants(Category category, Event event) {
        for (Participant participant: event.getParticipants()) {

            if (UserInputHandler.getYesNoInput(String.format("Did %s spent money on %s?", participant.getName(), category.getName()))) {
                double expense = UserInputHandler.getDoubleInput(String.format("Enter the amount of money that %s spent on %s:%n", participant.getName(), category.getName()));
                participant.getExpenses().put(category, expense);
            }
            if (UserInputHandler.getYesNoInput(String.format("Did %s consumed from %s category?", participant.getName(), category.getName()))) {
                participant.addConsumedCategory(category);
            }
        }
    }

    /**
     * Renames a category by asking the user for a new name.
     */
    private static void handleRenameCategory(Event event) {
        Category categoryToRename = getCategoryFromUser("Enter Category Number you wants to rename:" +
                " if there are none enter 0", event.getCategories());
        if (categoryToRename == null) {
            System.out.println("There is no category!");
            return;
        }
        String newCategoryName = UserInputHandler.getStringInput(String.format("Enter new name to %s:", categoryToRename.getName()));
        for (Category category: event.getCategories()) {
            if (category.equals(categoryToRename)) {
                category.setName(newCategoryName);
            }
        }
    }
}
