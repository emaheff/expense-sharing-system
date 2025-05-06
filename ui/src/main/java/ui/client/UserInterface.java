package ui.client;

import shared.CategoryDto;
import shared.EventDto;
import shared.EventSummaryDto;
import shared.ParticipantDto;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {

    private boolean isRunning = true;
    private int currentEventId = -1;

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
            case 3 -> showResultsForCurrentEvent();
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
        ParticipantInteractionHandler.setConsumers(eventCategories, eventParticipants);
        ParticipantInteractionHandler.setParticipantsExpenses(eventCategories, eventParticipants);
        EventDto newEvent = new EventDto(name, date, fee, eventParticipants, eventCategories);

        int createdId = EventApiClient.sendEvent(newEvent);
        if (createdId > 0) {
            this.currentEventId = createdId;
            System.out.println("Event created and saved with ID " + createdId);
        } else {
            System.out.println("Failed to create event.");
        }
    }

    private void loadEventFlow() {
        List<EventSummaryDto> summaries = EventApiClient.fetchEventSummaries();

        if (summaries.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        System.out.println("Choose an event to load:");
        for (int i = 0; i < summaries.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, summaries.get(i));
        }

        System.out.print("Enter number of event: ");
        int choice = UserInputHandler.getIntInput("Your choice: ");

        if (choice < 1 || choice > summaries.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        this.currentEventId = summaries.get(choice - 1).getId();
    }

    private void showResultsForCurrentEvent() {
        if (currentEventId <= -1) {
            System.out.println("No active event selected.");
            return;
        }

        EventDto eventDto = EventApiClient.fetchEventResultsById(currentEventId);
        if (eventDto == null) {
            System.out.println("Failed to fetch results for event #" + currentEventId);
            return;
        }

        System.out.println("=== Event Results: " + eventDto.getName() + " ===");
        System.out.println("Participation Fee: " + eventDto.getParticipationFee());
        System.out.println();

        System.out.print(EventPresenter.formatParticipants(eventDto));
        System.out.println(EventPresenter.formatDebts(eventDto.getDebts()));
        System.out.println("=============================");
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
