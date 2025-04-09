package ui;

import logic.*;
import storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UserInterface {

    private EventManager eventManager;
    private boolean isRunning = true;
    private Scanner scanner;
    private CalculationEngine calculationEngine;


    public UserInterface(EventManager eventManager) {
        this.eventManager = eventManager;
        this.scanner = new Scanner(System.in);
        this.calculationEngine = new CalculationEngine();
    }

    // Entry point of the program's UI
    public void start() {
        while (isRunning) {
            displayMainMenu();
            int choice = readUserChoice();
            scanner.nextLine();
            handleMainMenuChoice(choice);
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== Expense Sharing System ===");
        System.out.println("Please choose an option:");
        System.out.println("1. Create new event");
        System.out.println("2. Load existing event");
        System.out.println("3. Save current event");
        System.out.println("4. Show event results");
        System.out.println("5. Edit current event");
        System.out.println("6. Exit");
    }

    private int readUserChoice() {
        System.out.print("Your choice: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next(); // Skip invalid input
        }
        return scanner.nextInt();
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                System.out.println("Creating new event...");
                createEvent();
                break;
            case 2:
                System.out.println("Loading existing event...");
                loadEventFlow();
                break;
            case 3:
                saveCurrentEvent();
                break;
            case 4:
                showResultsForCurrentEvent();
                break;
            case 5:
                handleEditEvent();
                break;
            case 6:
                System.out.println("Exiting. Goodbye!");
                isRunning = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleEditEventChoice(int choice) {
        switch (choice) {
            case 1:
                renameEvent();
                break;
            case 2:
                editParticipationFee();
                break;
            case 3:
                handleManageCategories();
                break;
            case 4:
                handleManageParticipants();
                break;
            case 5:
                start();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleManageParticipants() {
        displayManageParticipantMenu();
        int choice = readUserChoice();
        scanner.nextLine();
        handleManagerParticipantChoice(choice);
    }

    private void handleManagerParticipantChoice(int choice) {
        switch (choice) {
            case 1:
                handleAddNewParticipant();
                break;
            case 2:
                handleRemoveParticipant();
                break;
            case 3:
                handleEditParticipant();
                break;
            case 4:
                handleEditEvent();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleEditParticipant() {
        Participant participantToEdit = getParticipantFromUser("Enter Participant Number you wants to edit:" +
                " if there are none enter 0");
        if (participantToEdit == null) {
            System.out.println("There is no participant!");
            return;
        }
        displayParticipantEditMenu(participantToEdit);
        int choice = readUserChoice();
        scanner.nextLine();
        handleEditParticipantChoice(choice, participantToEdit);

    }

    private void handleEditParticipantChoice(int choice, Participant participant) {
        switch (choice) {
            case 1:
                handleRenameParticipant();
                break;
            case 2:
                handleEditExpensesPerCategory(participant);
                break;
            case 3:
                handleEditConsumedCategories(participant);
                break;
            case 4:
                handleManageParticipants();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleEditConsumedCategories(Participant participant) {
        List<Category> consumedCategory = new ArrayList<>(eventManager.getCurrentEvent().getCategories());
        Category consumedCategoryToEdit = getCategoryFromUser("Enter category Number you wants to edit its consumed:" +
                " if there are none enter 0", consumedCategory);
        System.out.println("Enter 1 if you want to add this category as category that the participant consumed\n" +
                "Enter 2 if you want to remove this category from the categories that the participant consumed");
        int userNumber = -1;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Your choice: ");
                userNumber = Integer.parseInt(scanner.nextLine());
                if (userNumber == 1) {
                    validInput = true;
                    boolean isCategoryExist = false;
                    for (Category category: participant.getConsumedCategories()) {
                        if (category.equals(consumedCategoryToEdit)) {
                            isCategoryExist =true;
                            break;
                        }
                    }
                    if (!isCategoryExist) {
                        participant.addConsumedCategory(consumedCategoryToEdit);
                        break;
                    }
                }
                if (userNumber == 2) {
                    validInput = true;
                    participant.getConsumedCategories().remove(consumedCategoryToEdit);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

    }

    private void handleEditExpensesPerCategory(Participant participant) {
        List<Category> expenseCategories = new ArrayList<>(eventManager.getCurrentEvent().getCategories());
        Category expenseCategoryToEdit = getCategoryFromUser("Enter category Number you wants to edit its expense:" +
                " if there are none enter 0", expenseCategories);
        double expense = 0;
        while (true) {
            assert expenseCategoryToEdit != null;
            System.out.printf("Enter the amount of money that %s spent on %s:%n", participant.getName(), expenseCategoryToEdit.getName());
            String expenseInput = scanner.nextLine();
            try {
                expense = Double.parseDouble(expenseInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid expense.");
            }
        }
        participant.getExpenses().put(expenseCategoryToEdit, expense);
        displayParticipantEditMenu(participant);
    }

    private void handleRenameParticipant() {
        Participant participantToRename = getParticipantFromUser("Enter Participant Number you wants to rename:" +
                " if there are none enter 0");
        if (participantToRename == null) {
            System.out.println("There is no participant!");
            return;
        }
        System.out.printf("Enter new name for %s%n", participantToRename.getName());
        String newName = scanner.nextLine();
        EventEditor.renameParticipant(participantToRename, newName);
    }

    private void displayParticipantEditMenu(Participant participant) {
        System.out.println("=== Edit Participant: " + participant.getName() + " ===");
        System.out.println("""
                1. Rename participant
                2. Edit expenses per category
                3. Edit consumed categories
                4. Back to previous menu
                """);
    }

    private void handleAddNewParticipant() {
        addParticipant(eventManager.getCurrentEvent());
    }

    private void handleRemoveParticipant() {
        Participant participantToRemove = getParticipantFromUser("Enter Participant Number you wants to remove:" +
                " if there are none enter 0");
        if (participantToRemove == null) {
            System.out.println("There is no participant!");
            return;
        }
        EventEditor.removeParticipant(eventManager.getCurrentEvent(), participantToRemove);
    }

    private void displayManageParticipantMenu() {
        System.out.println("""
                === Manage Participants ===
                1. Add new participant
                2. Remove participant
                3. Edit existing participant
                4. Back to previous menu
                """);
    }

    private void handleManageCategories() {
        displayManageCategoryMenu();
        int choice = readUserChoice();
        scanner.nextLine();
        handleManagerCategoryChoice(choice);
    }

    private void handleManagerCategoryChoice(int choice) {
        switch (choice) {
            case 1:
                handleAddNewCategory();
                break;
            case 2:
                handleRenameCategory();
                break;
            case 3:
                handleRemoveCategory();
                break;
            case 4:
                handleEditEvent();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleRemoveCategory() {
        Category categoryToRemove = getCategoryFromUser("Enter Category Number you wants to remove:" +
                " if there are none enter 0", eventManager.getCurrentEvent().getCategories());
        if (categoryToRemove == null) {
            System.out.println("There is no category!");
            return;
        }
        for (Participant participant: eventManager.getCurrentEvent().getParticipants()) {
            for (Category expenseCategory: participant.getExpenses().keySet()) {
                if (expenseCategory.equals(categoryToRemove)) {
                    participant.getExpenses().remove(expenseCategory);
                }
            }
            participant.getConsumedCategories().removeIf(consumedCategory -> consumedCategory.equals(categoryToRemove));
        }

    }

    private void handleRenameCategory() {
        Category categoryToRename = getCategoryFromUser("Enter Category Number you wants to rename:" +
                " if there are none enter 0", eventManager.getCurrentEvent().getCategories());
        if (categoryToRename == null) {
            System.out.println("There is no category!");
            return;
        }
        System.out.printf("Enter new name to %s:%n", categoryToRename.getName());
        String newCategoryName = scanner.nextLine();
        for (Category category: eventManager.getCurrentEvent().getCategories()) {
            if (category.equals(categoryToRename)) {
                category.setName(newCategoryName);
            }
        }
    }

    private Participant getParticipantFromUser(String header) {
        List<Participant> participants = new ArrayList<>(eventManager.getCurrentEvent().getParticipants());
        displayNumberedParticipants(participants);
        System.out.println(header);
        System.out.println(displayNumberedParticipants(participants));

        int userNumber = -1;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Your choice: ");
                userNumber = Integer.parseInt(scanner.nextLine());
                if (userNumber == 0) {
                    return null;
                }
                if (userNumber >= 1 && userNumber <= participants.size()) {
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 1 and " + participants.size() + ", or 0 to skip.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        return getParticipantFromNumber(userNumber, participants);
    }

    private Category getCategoryFromUser(String header, List<Category> categories) {
        List<Category> categoriesDisplay = new ArrayList<>(categories);
        displayNumberedCategories(categoriesDisplay);
        System.out.println(header);
        System.out.println(displayNumberedCategories(categoriesDisplay));

        int userNumber = -1;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Your choice: ");
                userNumber = Integer.parseInt(scanner.nextLine());
                if (userNumber == 0) {
                    return null;
                }
                if (userNumber >= 1 && userNumber <= categoriesDisplay.size()) {
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 1 and " + categoriesDisplay.size() + ", or 0 to skip.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        return getCategoryFromNumber(userNumber, categoriesDisplay);
    }

    private void handleAddNewCategory() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        System.out.println("Enter a new category that you want to add:");
        String newCategoryName = scanner.nextLine();
        Category newCategory = new Category(newCategoryName);
        EventEditor.addCategory(currentEvent, newCategory);
        addNewCategoryToParticipants(newCategory);
    }

    private void addNewCategoryToParticipants(Category category) {
        for (Participant participant: eventManager.getCurrentEvent().getParticipants()) {
            System.out.printf("Did %s spent money on %s?\t(Y/N)%n", participant.getName(), category.getName());
            String userExpenseAnswer = scanner.nextLine();
            if (userExpenseAnswer.equalsIgnoreCase("Y")) {
                double expense = 0;
                while (true) {
                    System.out.printf("Enter the amount of money that %s spent on %s:%n", participant.getName(), category.getName());
                    String expenseInput = scanner.nextLine();
                    try {
                        expense = Double.parseDouble(expenseInput);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number. Please enter a valid expense.");
                    }
                }
                EventEditor.addParticipationExpense(participant, category, expense);
            }
            System.out.printf("Did %s consumed from %s category?\t(Y/N)%n", participant.getName(), category.getName());
            String userConsumptionAnswer = scanner.nextLine();
            if (userConsumptionAnswer.equalsIgnoreCase("Y")) {
                EventEditor.addParticipantConsumption(participant, category);
                }
            }
    }

    private void displayManageCategoryMenu() {
        System.out.println("""
                === Manage Categories ===
                1. Add new category
                2. Rename existing category
                3. Remove category
                4. Back to previous menu
                """);
    }

    private void editParticipationFee() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        double fee = 0;
        while (true) {
            System.out.println("Enter new participation fee: ");
            String feeInput = scanner.nextLine();
            try {
                fee = Double.parseDouble(feeInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid participation fee.");
            }
        }
        EventEditor.setParticipationFee(currentEvent, fee);
    }

    private void renameEvent() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        System.out.println("Enter a new name for this event:");
        String newEventName = scanner.nextLine();
        EventEditor.renameEvent(currentEvent, newEventName);
    }

    private void handleEditEvent() {
        displayEditMenu();
        int choice = readUserChoice();
        scanner.nextLine();
        handleEditEventChoice(choice);

    }

    private void displayEditMenu() {
        System.out.println("""
                === Edit Event ===
                1. Rename event
                2. Change participation fee
                3. Manage categories
                4. Manage participants
                5. Back to main menu
                """);
    }

    private void createEvent() {
        System.out.println("=== Create New Event ===");

        System.out.println("Enter event name: ");

        String name = scanner.nextLine();

        double fee = 0;
        while (true) {
            System.out.println("Enter participation fee: ");
            String feeInput = scanner.nextLine();
            try {
                fee = Double.parseDouble(feeInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid participation fee.");
            }
        }

        Event newEvent = new Event(name, fee);

        defineCategories(newEvent); // Define consumed/expenses categories for this new event
        eventManager.createEvent(newEvent);

        System.out.println("Event '" + name + "' created successfully.");

        addParticipants(newEvent);

        newEvent.finalizeCalculations();
    }

    private void defineCategories(Event newEvent) {
        boolean isMoreCategory = true;

        System.out.println("Enter Consumed Categories for this Event");
        while (isMoreCategory) {
            System.out.println("Enter Category Name:");
            String categoryName = scanner.nextLine();
            newEvent.addCategory(new Category(categoryName));
            System.out.println("Do You Want To Add Category? (Y/N)");
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase("Y")) {
                isMoreCategory = false;
            }
        }
    }

    private void saveCurrentEvent() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to save.");
            return;
        }

        StorageManager storage = new StorageManager();
        String eventName = currentEvent.getEventName();

        if (storage.doesEventFileExist(eventName)) {
            System.out.printf("A file named \"%s.json\" already exists. Overwrite? (yes/no): ", eventName);
            String answer = scanner.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                System.out.println("Save cancelled.");
                return;
            }
        }

        boolean success = storage.saveEventToFile(currentEvent);
        if (success) {
            System.out.printf("Event \"%s\" saved successfully.%n", eventName);
        } else {
            System.out.printf("Failed to save event \"%s\".%n", eventName);
        }
    }


    private void loadEventFlow() {
        StorageManager storage = new StorageManager();
        List<String> savedEvents = storage.getSavedEventNames();

        if (savedEvents.isEmpty()) {
            System.out.println("No saved events found.");
            return;
        }

        System.out.println("Choose an event to load:");
        for (int i = 0; i < savedEvents.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, savedEvents.get(i));
        }

        System.out.print("Enter number of event: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice < 1 || choice > savedEvents.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String selectedEventName = savedEvents.get(choice - 1);
        Event event = storage.loadEventByName(selectedEventName);

        if (event != null) {
            event.finalizeCalculations();
            eventManager.setCurrentEvent(event);
            System.out.printf("Event \"%s\" loaded successfully.%n", selectedEventName);
        } else {
            System.out.println("Failed to load event.");
        }
    }


    private void addParticipants(Event newEvent) {
        boolean isMoreParticipant = true;
        System.out.println(" === Adds Participants to Event ===");
        while (isMoreParticipant) {
            addParticipant(newEvent);

            // ask the user if there are more participants that the user want to add
            System.out.println("Do you want to add more participant? (Y/N)");
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase("Y"))
                isMoreParticipant = false;
        }
    }

    private void addParticipant(Event event) {
        System.out.println("Enter Participant Name:");
        String name = scanner.nextLine();
        Participant participant = new Participant(name);
        // ask the user to enter the categories that need to add to the participant expenses
        enterExpensesCategories(participant, event.getCategories());
        enterConsumedCategories(participant, event.getCategories());

        // adds the participant to the event
        event.addParticipant(participant);
    }

    private void enterExpensesCategories(Participant participant, List<Category> categories) {
        boolean isMoreCategory = true;
        // copy the categories list, so I can change it without effect the original list
        List<Category> displayCategories = new ArrayList<>(categories);
        System.out.println("Enter Categories that " + participant.getName() +" Expense Money on");

        while (isMoreCategory) {
            // ask from the user to choose category that the participant spent on
            System.out.println("Enter Category Number The Participant Spent Money On From These Categories:" +
                    " if there are none enter 0");
            System.out.println(displayNumberedCategories(displayCategories));

            // get user answer and create new category
            int userNumber = Integer.parseInt(scanner.nextLine());
            if (userNumber == 0)
                break;
            Category expenseCategory = getCategoryFromNumber(userNumber, displayCategories);

            // get the amount that the participant spent on the category
            System.out.println("Enter the amount of money you spent on this category.");
            double amount = Double.parseDouble(scanner.nextLine());

            // adds the expense to the participant expenses list
            ParticipantEditor.addExpense(participant, expenseCategory, amount);



            // ask the user if there are more categories that the participant spent money on
            System.out.println("Is there are more categories that the participant spent money on (Y/N)");
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase("Y"))
                isMoreCategory = false;

        }

    }

    private void enterConsumedCategories(Participant participant, List<Category> categories) {
        boolean isMoreCategory = true;
        // copy the categories list, so I can change it without effect the original list
        List<Category> displayCategories = new ArrayList<>(categories);
        System.out.println("Enter Categories that " + participant.getName() +" Consumed");

        while (isMoreCategory || !displayCategories.isEmpty()) {
            System.out.println("Enter Category Number The Participant Consumed From These Categories:" +
                    " if there are none enter 0");
            System.out.println(displayNumberedCategories(displayCategories));

            int userNumber = -1;
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.print("Your choice: ");
                    userNumber = Integer.parseInt(scanner.nextLine());
                    if (userNumber == 0) {
                        return;
                    }
                    if (userNumber >= 1 && userNumber <= displayCategories.size()) {
                        validInput = true;
                    } else {
                        System.out.println("Please enter a number between 1 and " + displayCategories.size() + ", or 0 to skip.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }

            Category consumedCategory = getCategoryFromNumber(userNumber, displayCategories);
            ParticipantEditor.addConsumedCategory(participant, consumedCategory);

            System.out.println("Are there more categories that the participant consumed? (Y/N)");
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase("Y")) {
                isMoreCategory = false;
            }
        }

    }

    private String displayNumberedCategories(List<Category> categories) {
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= categories.size(); i++) {
            result.append(i).append(". ").append(categories.get(i - 1).getName()).append("\n");
        }
        return result.toString();
    }

    private String displayNumberedParticipants(List<Participant> participants) {
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= participants.size(); i++) {
            result.append(i).append(". ").append(participants.get(i -1).getName()).append("\n");
        }
        return result.toString();
    }

    private Category getCategoryFromNumber(int userNumber, List<Category> displayedCategories) {
        Category result = displayedCategories.get(userNumber - 1);
        displayedCategories.remove(userNumber -1);
        return result;
    }

    private Participant getParticipantFromNumber(int userNumber, List<Participant> displayedParticipants) {
        return displayedParticipants.get(userNumber - 1);
    }

    private void showResultsForCurrentEvent() {
        Event currentEvent = eventManager.getCurrentEvent();

        if (currentEvent == null) {
            System.out.println("No active event selected.");
            return;
        }

        System.out.println("=== Event Results: " + currentEvent.getEventName() + " ===");

        calculationEngine.calculateBalances(currentEvent);

        Map<Participant, Double> totalConsumedMap = calculationEngine.getLatestConsumptionMap();

        System.out.print(EventPresenter.formatParticipants(currentEvent, totalConsumedMap));
        System.out.println(EventPresenter.formatDebts(currentEvent.getDebts()));
        System.out.println("=============================");

        System.out.println("=============================");
    }


}
