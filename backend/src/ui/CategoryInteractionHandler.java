package ui;

import logic.Category;
import logic.Event;
import logic.EventEditor;
import logic.Participant;

import java.util.ArrayList;
import java.util.List;

public class CategoryInteractionHandler {

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

    public static Category getCategoryFromNumber(int userNumber, List<Category> displayedCategories) {
        Category result = displayedCategories.get(userNumber - 1);
        displayedCategories.remove(userNumber -1);
        return result;
    }

    public static void handleManageCategories(Event event) {
        MenuPrinter.displayManageCategoryMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleManagerCategoryChoice(choice, event);
    }

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

    private static void handleRemoveCategory(Event event) {
        Category categoryToRemove = getCategoryFromUser("Enter Category Number you wants to remove:" +
                " if there are none enter 0", event.getCategories());
        if (categoryToRemove == null) {
            System.out.println("There is no category!");
            return;
        }
        for (Participant participant: event.getParticipants()) {
            for (Category expenseCategory: participant.getExpenses().keySet()) {
                if (expenseCategory.equals(categoryToRemove)) {
                    participant.getExpenses().remove(expenseCategory);
                }
            }
            participant.getConsumedCategories().removeIf(consumedCategory -> consumedCategory.equals(categoryToRemove));
        }
    }

    private static void handleAddNewCategory(Event event) {
        if (event == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        String newCategoryName = UserInputHandler.getStringInput("Enter a new category that you want to add: ");
        Category newCategory = new Category(newCategoryName);
        EventEditor.addCategory(event, newCategory);
        addNewCategoryToParticipants(newCategory, event);
    }

    private static void addNewCategoryToParticipants(Category category, Event event) {
        for (Participant participant: event.getParticipants()) {

            if (UserInputHandler.getYesNoInput(String.format("Did %s spent money on %s?", participant.getName(), category.getName()))) {
                double expense = UserInputHandler.getDoubleInput(String.format("Enter the amount of money that %s spent on %s:%n", participant.getName(), category.getName()));
                EventEditor.addParticipationExpense(participant, category, expense);
            }
            if (UserInputHandler.getYesNoInput(String.format("Did %s consumed from %s category?", participant.getName(), category.getName()))) {
                EventEditor.addParticipantConsumption(participant, category);
            }
        }
    }

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
