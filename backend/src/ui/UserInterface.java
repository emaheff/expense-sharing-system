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
        System.out.println("5. Exit");
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
                System.out.println("Exiting. Goodbye!");
                isRunning = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
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
            System.out.println("Enter Participant Name:");
            String name = scanner.nextLine();
            Participant participant = new Participant(name);
            // ask the user to enter the categories that need to add to the participant expenses
            enterExpensesCategories(participant, newEvent.getCategories());
            enterConsumedCategories(participant, newEvent.getCategories());

            // adds the participant to the event
            newEvent.addParticipant(participant);

            // ask the user if there are more participants that the user want to add
            System.out.println("Do you want to add more participant? (Y/N)");
            String userAnswer = scanner.nextLine();
            if (!userAnswer.equalsIgnoreCase("Y"))
                isMoreParticipant = false;
        }
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

    private Category getCategoryFromNumber(int userNumber, List<Category> displayedCategories) {
        Category result = displayedCategories.get(userNumber - 1);
        displayedCategories.remove(userNumber -1);
        return result;
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
