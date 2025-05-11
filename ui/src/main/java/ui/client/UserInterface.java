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
            case 4 -> handleEditEvent();
//            case 5 -> deleteEvent();
//            case 6 -> exportExcel();
            case 7 -> {
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
                handleManageCategories();
                handleEditEvent();
            }
        }
    }

    private void renameEvent() {
        if (currentEventId == 0) {
            System.out.println("No event selected to edit.");
            return;
        }
        String newEventName = UserInputHandler.getStringInput("Enter a new name for this event: ");
        int result = EventApiClient.changeEventName(currentEventId, newEventName);
        if (result > 0) {
            System.out.println("Event name updated successfully.");
        } else {
            System.out.println("Failed to update event name");
        }
    }

    private void editEventDate() {
        if (currentEventId == 0) {
            System.out.println("No event selected to edit.");
            return;
        }
        String stringDate = UserInputHandler.getStringInput("Enter date (dd/MM/yyyy): ");
        validateDate(stringDate);
        int result = EventApiClient.changeEventDate(currentEventId, stringDate);
        if (result > 0) {
            System.out.println("Event date updated successfully.");
        } else {
            System.out.println("Failed to update event date");
        }
    }

    private void editParticipationFee() {
        if (currentEventId == 0) {
            System.out.println("No event selected to edit.");
            return;
        }
        double participationFee = UserInputHandler.getDoubleInput("Enter participation fee: ");
        int result = EventApiClient.changeParticipationFee(currentEventId, participationFee);
        if (result > 0) {
            System.out.println("Event date updated successfully.");
        } else {
            System.out.println("Failed to update event date");
        }
    }

    private void handleManageCategories() {
        MenuPrinter.displayManageCategoryMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleManageCategoryChoice(choice);
    }

    private void handleManageCategoryChoice(int choice) {
        switch (choice) {
            case 1 -> handleAddNewCategory();
            case 2 -> handleRenameCategory();
        }
    }

    private void handleAddNewCategory() {
        if (currentEventId == 0) {
            System.out.println("No event selected to edit.");
            return;
        }
        String newCategoryName = UserInputHandler.getStringInput("Enter a new category that you want to add: ");
        CategoryDto newCategory = new CategoryDto(newCategoryName);
        addNewCategoryToParticipants(newCategory);

    }

    private void addNewCategoryToParticipants(CategoryDto newCategory) {
        // get list of participants from the server of this current event.
        // for each participant add the category to consumed category if the participant consumed it
        // in addition - ask the participant if he spent money on this category and how much.
        List<ParticipantDto> participants = EventApiClient.fetchParticipantsEvent(currentEventId);

        for (ParticipantDto participant: participants) {
            if (UserInputHandler.getYesNoInput(String.format("Did %s spent money on %s?", participant.getName(), newCategory.getName()))) {
                double expense = UserInputHandler.getDoubleInput(String.format("Enter the amount of money that %s spent on %s:%n", participant.getName(), newCategory.getName()));
                participant.getExpenses().put(newCategory.getName(), expense);
            }
            if (UserInputHandler.getYesNoInput(String.format("Did %s consumed from %s category?", participant.getName(), newCategory.getName()))) {
                participant.addConsumedCategory(newCategory.getName());
            }
        }

        // send the updated participant list to the server to update this current event.
        EventApiClient.setParticipants(participants, currentEventId);
    }

    /**
     * Renames a category by asking the user for a new name.
     */
    private void handleRenameCategory() {
        // 1. get a list of categories of this current event from user. including category id.
        List<CategoryDto> eventCategories = EventApiClient.fetchCategoriesEvent(currentEventId);
        // 2. display the list to the user and ask the user to choose the category to rename.
        List<CategoryDto> categoriesDisplay = new ArrayList<>(eventCategories);
        System.out.println("Enter the category number you want to rename");
        System.out.println(MenuPrinter.displayNumberedCategories(categoriesDisplay));
        int choice = UserInputHandler.getIntInput("Your choice: ");
        boolean validInput = false;

        while (!validInput) {
            if (choice == 0) {
                return;
            }
            if (choice >= 1 && choice <= categoriesDisplay.size()) {
                validInput = true;
            } else {
                System.out.println("Please enter a number between 1 and " + categoriesDisplay.size() + ", or 0 to skip.");
            }
        }
        int categoryIdToRename = categoriesDisplay.get(choice - 1).getId();
        // 3. send to the server the category id to rename and string with new name to the category
        String newName = UserInputHandler.getStringInput("Enter the a new name to " + categoriesDisplay.get(choice - 1).getName());

        EventApiClient.changeCategoryName(categoryIdToRename, newName, currentEventId);

    }

    private void validateDate(String newDate) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (date == null) {
            try {
                date = LocalDate.parse(newDate, formatter);
            } catch (DateTimeException e) {
                System.out.println("Invalid date. Please use format dd/MM/yyyy and enter a real date.");
            }
        }
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
