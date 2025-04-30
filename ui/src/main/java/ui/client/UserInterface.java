package ui.client;

import shared.CategoryDto;
import shared.EventDto;
import shared.ParticipantDto;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {

    private boolean isRunning = true;

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
//            case 2 -> loadEventFlow();
//            case 3 -> saveCurrentEvent();
//            case 4 -> showResultsForCurrentEvent();
//            case 5 -> handleEditEvent();
//            case 6 -> deleteEvent();
//            case 7 -> exportExcel();
            case 8 -> {
                System.out.println("Exiting. Goodbye!");
                isRunning = false;
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private void createEvent() {
        System.out.println("=== Create New Event ===");
        String name = UserInputHandler.getStringInput("Enter event name: ");
        String stringDate = UserInputHandler.getStringInput("Enter date (dd/MM/yyyy): ");
        LocalDate date = localDateFromString(stringDate);
        double fee = UserInputHandler.getDoubleInput("Enter new participation fee: ");

        List<CategoryDto> eventCategories = getCategoriesFromUser();
        List<ParticipantDto> eventParticipants = ParticipantInteractionHandler.getParticipantsFromUser();
        ParticipantInteractionHandler.setConsumers (eventCategories, eventParticipants);
        ParticipantInteractionHandler.setParticipantsExpenses(eventCategories, eventParticipants);
        EventDto newEvent = new EventDto(name, date, fee, eventParticipants, eventCategories);

        new EventApiClient().sendEvent(newEvent);


        // send POST request to server to create a new Event with these variables (name, date, participation fee, categories and participants)
    }

    private List<CategoryDto> getCategoriesFromUser() {
        List<CategoryDto> eventCategories = new ArrayList<>();
        boolean isMoreCategory = true;

        System.out.println("Enter Consumed Categories for this Event");
        while (isMoreCategory) {
            String categoryName = UserInputHandler.getStringInput("Enter Category Name: ");
            CategoryDto categoryDto = new CategoryDto(categoryName);
            eventCategories.add(categoryDto);
            if (!UserInputHandler.getYesNoInput("Do You Want To Add Category? ")) {
                isMoreCategory = false;
            }
        }
        return eventCategories;
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
