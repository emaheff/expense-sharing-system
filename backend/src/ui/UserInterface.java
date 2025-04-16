package ui;

import logic.*;
import storage.EventDao;
import storage.ParticipantDao;
import storage.StorageManager;

import java.util.List;
import java.util.Map;

public class UserInterface {

    private EventManager eventManager;
    private boolean isRunning = true;
    private CalculationEngine calculationEngine;


    public UserInterface(EventManager eventManager) {
        this.eventManager = eventManager;
        this.calculationEngine = new CalculationEngine();
    }

    // Entry point of the program's UI
    public void start() {
        while (isRunning) {
            MenuPrinter.displayMainMenu();
            int choice = UserInputHandler.getIntInput("Your choice: ");
            handleMainMenuChoice(choice);
        }
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

    public void handleEditEvent() {
        MenuPrinter.displayEditMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleEditEventChoice(choice);

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
                CategoryInteractionHandler.handleManageCategories(eventManager.getCurrentEvent());
                handleEditEvent();
                break;
            case 4:
                ParticipantInteractionHandler.handleManageParticipants(eventManager.getCurrentEvent());
                handleEditEvent();
                break;
            case 5:
                start();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void editParticipationFee() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        double fee = UserInputHandler.getDoubleInput("Enter new participation fee: ");
        currentEvent.setParticipationFee(fee);
    }

    private void renameEvent() {
        Event currentEvent = eventManager.getCurrentEvent();
        if (currentEvent == null) {
            System.out.println("No event selected to edit.");
            return;
        }
        String newEventName = UserInputHandler.getStringInput("Enter a new name for this event: ");
        currentEvent.setEventName(newEventName);
    }

    private void createEvent() {

        System.out.println("=== Create New Event ===");
        String name = UserInputHandler.getStringInput("Enter event name: ");
        double fee = UserInputHandler.getDoubleInput("Enter new participation fee: ");
        Event newEvent = new Event(name, fee);

        defineCategories(newEvent); // Define consumed/expenses categories for this new event
        eventManager.createEvent(newEvent);

        System.out.println("Event '" + name + "' created successfully.");

        ParticipantInteractionHandler.addParticipants(newEvent);

        newEvent.finalizeCalculations();
    }

    private void defineCategories(Event newEvent) {
        boolean isMoreCategory = true;

        System.out.println("Enter Consumed Categories for this Event");
        while (isMoreCategory) {
            String categoryName = UserInputHandler.getStringInput("Enter Category Name: ");
            newEvent.addCategory(new Category(categoryName));
            if (!UserInputHandler.getYesNoInput("Do You Want To Add Category? ")) {
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
            if (!UserInputHandler.getYesNoInput(String.format("A file named \"%s.json\" already exists. Overwrite? (yes/no): ", eventName))) {
                System.out.println("Save cancelled.");
                return;
            }
        }

        boolean dbSuccess = EventDao.insertOrUpdateEvent(currentEvent);
        if (dbSuccess) {
            ParticipantDao.saveEventParticipants(currentEvent);
            System.out.println("Event also saved to database.");
        } else {
            System.out.println("Failed to save event to database.");
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
        int choice = UserInputHandler.getIntInput("Your choice: ");

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

    private void showResultsForCurrentEvent() {
        Event currentEvent = eventManager.getCurrentEvent();

        if (currentEvent == null) {
            System.out.println("No active event selected.");
            return;
        }

        System.out.println("=== Event Results: " + currentEvent.getEventName() + " ===\nParticipation Fee: " + currentEvent.getParticipationFee() + "\n");

        calculationEngine.calculateBalances(currentEvent);

        Map<Participant, Double> totalConsumedMap = calculationEngine.getLatestConsumptionMap();

        System.out.print(EventPresenter.formatParticipants(currentEvent, totalConsumedMap));
        System.out.println(EventPresenter.formatDebts(currentEvent.getDebts()));
        System.out.println("=============================");

        System.out.println("=============================");
    }

}