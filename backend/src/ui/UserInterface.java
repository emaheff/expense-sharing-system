package ui;

import logic.*;
import storage.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * UserInterface is the main UI handler for the expense sharing system.
 * It interacts with the user via console, manages navigation between menus,
 * and invokes the logic and persistence layers.
 */
public class UserInterface {

    private EventManager eventManager;
    private boolean isRunning = true;
    private CalculationEngine calculationEngine;


    public UserInterface(EventManager eventManager) {
        this.eventManager = eventManager;
        this.calculationEngine = new CalculationEngine();
    }

    /**
     * Entry point for running the UI loop.
     */
    public void start() {
        while (isRunning) {
            MenuPrinter.displayMainMenu();
            int choice = UserInputHandler.getIntInput("Your choice: ");
            handleMainMenuChoice(choice);
        }
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> createEvent();
            case 2 -> loadEventFlow();
            case 3 -> saveCurrentEvent();
            case 4 -> showResultsForCurrentEvent();
            case 5 -> handleEditEvent();
            case 6 -> deleteEvent();
            case 7 -> exportExcel();
            case 8 -> {
                System.out.println("Exiting. Goodbye!");
                isRunning = false;
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void exportExcel() {
        if (eventManager.getCurrentEvent() == null) {
            System.out.println("You need first to create or load an event");
            return;
        }
        ExcelExporter.exportToFile(eventManager.getCurrentEvent());
    }

    private void handleEditEvent() {
        MenuPrinter.displayEditMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleEditEventChoice(choice);
    }

    private void handleEditEventChoice(int choice) {
        switch (choice) {
            case 1 -> renameEvent();
            case 2 -> editEventDate();
            case 3 -> editParticipationFee();
            case 4 -> {
                CategoryInteractionHandler.handleManageCategories(eventManager.getCurrentEvent());
                handleEditEvent();
            }
            case 5 -> {
                ParticipantInteractionHandler.handleManageParticipants(eventManager.getCurrentEvent());
                handleEditEvent();
            }
            case 6 -> start();
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void editEventDate() {
        String stringDate = UserInputHandler.getStringInput("Enter date (dd/MM/yyyy): ");
        LocalDate date = localDateFromString(stringDate);
        eventManager.getCurrentEvent().setDate(date);
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
        String stringDate = UserInputHandler.getStringInput("Enter date (dd/MM/yyyy): ");
        LocalDate date = localDateFromString(stringDate);
        double fee = UserInputHandler.getDoubleInput("Enter new participation fee: ");
        Event newEvent = new Event(name, fee, date);

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

        boolean dbSuccess = EventDao.insertOrUpdateEvent(currentEvent);
        if (dbSuccess) {
            ParticipantDao.saveEventParticipants(currentEvent);
            CategoryDao.saveEventCategories(currentEvent);
            DebtDao.saveEventDebts(currentEvent);
            ExpenseDao.saveEventExpenses(currentEvent);
            ExpenseDao.saveEventConsumptions(currentEvent);
            System.out.println("Event also saved to database.");
        } else {
            System.out.println("Failed to save event to database.");
        }
    }

    private void loadEventFlow() {
        List<EventDao.EventSummary> savedEvents = EventDao.getAllEvents();

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

        int selectedEventId = savedEvents.get(choice - 1).getId();
        String selectedEventName = savedEvents.get(choice - 1).getName();
        Event event = EventDao.loadEventById(selectedEventId);

        if (event != null) {
            event.finalizeCalculations();
            eventManager.setCurrentEvent(event);
            System.out.printf("Event \"%s\" loaded successfully.%n", selectedEventName);
        } else {
            System.out.println("Failed to load event.");
        }
    }

    private void deleteEvent() {
        EventDao.deleteEventById(eventManager.getCurrentEvent().getId());
    }

    private void showResultsForCurrentEvent() {
        Event currentEvent = eventManager.getCurrentEvent();

        if (currentEvent == null) {
            System.out.println("No active event selected.");
            return;
        }

        System.out.println("=== Event Results: " + currentEvent.getEventName() + " ===\nParticipation Fee: " + currentEvent.getParticipationFee() + "\n");

        calculationEngine.calculateBalances(currentEvent);

        System.out.print(EventPresenter.formatParticipants(currentEvent));
        System.out.println(EventPresenter.formatDebts(currentEvent.getDebts()));
        System.out.println("=============================");

        System.out.println("=============================");
    }

    private LocalDate localDateFromString(String stringDate) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (date == null) {
            try {
                date = LocalDate.parse(stringDate, formatter);
            } catch (DateTimeException e) {
                System.out.println("Invalid date. Please use format dd/MM/yyyy and enter a real date.");
            }
        }
        return date;
    }

}