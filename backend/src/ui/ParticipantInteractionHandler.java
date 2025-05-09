package ui;

import logic.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles user interactions related to participants including adding, editing,
 * removing participants, and managing their expenses and consumption categories.
 */
public class ParticipantInteractionHandler {

    /**
     * Prompts the user to select a participant from a list.
     *
     * @param event the event containing the participants
     * @param header the message to display before the list
     * @return the selected participant or null if the user skips
     */
    private static Participant getParticipantFromUser(Event event, String header) {
        List<Participant> participants = new ArrayList<>(event.getParticipants());
        System.out.println(header);
        System.out.println(MenuPrinter.displayNumberedParticipants(participants));

        int choice = UserInputHandler.getIntInput("Your choice: ");
        boolean validInput = false;

        while (!validInput) {
            if (choice == 0) {
                return null;
            }
            if (choice >= 1 && choice <= participants.size()) {
                validInput = true;
            } else {
                System.out.println("Please enter a number between 1 and " + participants.size() + ", or 0 to skip.");
            }
        }

        return getParticipantFromNumber(choice, participants);
    }

    /**
     * Returns a participant by 1-based index from the given list.
     */
    public static Participant getParticipantFromNumber(int userNumber, List<Participant> displayedParticipants) {
        return displayedParticipants.get(userNumber - 1);
    }

    /**
     * Displays and handles the main participant management menu.
     */
    public static void handleManageParticipants(Event event) {
        MenuPrinter.displayManageParticipantMenu();
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleManagerParticipantChoice(choice, event);
    }

    private static void handleManagerParticipantChoice(int choice, Event event) {
        switch (choice) {
            case 1 -> addParticipant(event);
            case 2 -> handleRemoveParticipant(event);
            case 3 -> handleEditParticipant(event);
            case 4 -> {}
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleRemoveParticipant(Event event) {
        Participant participantToRemove = getParticipantFromUser(event, "Enter Participant Number you wants to remove:" +
                " if there are none enter 0");
        if (participantToRemove == null) {
            System.out.println("There is no participant!");
            return;
        }
        event.getParticipants().remove(participantToRemove);
    }

    private static void handleEditParticipant(Event event) {
        Participant participantToEdit = getParticipantFromUser(event, "Enter Participant Number you wants to edit:" +
                " if there are none enter 0");
        if (participantToEdit == null) {
            System.out.println("There is no participant!");
            return;
        }
        MenuPrinter.displayParticipantEditMenu(participantToEdit);
        int choice = UserInputHandler.getIntInput("Your choice: ");
        handleEditParticipantChoice(choice, participantToEdit, event);
    }

    private static void handleEditParticipantChoice(int choice, Participant participant, Event event) {
        switch (choice) {
            case 1 -> handleRenameParticipant(event);
            case 2 -> handlePhoneNumberEdit(event);
            case 3 -> handleEmailAddressEdit(event);
            case 4 -> handleEditExpensesPerCategory(participant, event);
            case 5 -> handleEditConsumedCategories(participant, event);
            case 6 -> handleManageParticipants(event);
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleRenameParticipant(Event event) {
        Participant participantToRename = getParticipantFromUser(event, "Enter Participant Number you wants to rename:" +
                " if there are none enter 0");
        if (participantToRename == null) {
            System.out.println("There is no participant!");
            return;
        }
        String newName = UserInputHandler.getStringInput(String.format("Enter new name for %s", participantToRename.getName()));
        participantToRename.setName(newName);
    }

    private static void handleEditExpensesPerCategory(Participant participant, Event event) {
        List<Category> expenseCategories = new ArrayList<>(event.getCategories());
        Category expenseCategoryToEdit = CategoryInteractionHandler.getCategoryFromUser("Enter category Number you wants to edit its expense:" +
                " if there are none enter 0", expenseCategories);
        assert expenseCategoryToEdit != null;
        double expense = UserInputHandler.getDoubleInput(String.format("Enter the amount of money that %s spent on %s:", participant.getName(), expenseCategoryToEdit.getName()));
        participant.getExpenses().put(expenseCategoryToEdit, expense);
        MenuPrinter.displayParticipantEditMenu(participant);
    }

    private static void handleEditConsumedCategories(Participant participant, Event event) {
        List<Category> consumedCategory = new ArrayList<>(event.getCategories());
        Category consumedCategoryToEdit = CategoryInteractionHandler.getCategoryFromUser("Enter category Number you wants to edit its consumed:" +
                " if there are none enter 0", consumedCategory);
        System.out.println("Enter 1 if you want to add this category as category that the participant consumed\n" +
                "Enter 2 if you want to remove this category from the categories that the participant consumed");
        int choice = UserInputHandler.getIntInput("Your choice: ");
        boolean validInput = false;

        while (!validInput) {
            if (choice == 1) {
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
            else if (choice == 2) {
                participant.getConsumedCategories().remove(consumedCategoryToEdit);
                break;
            }
        }
    }

    public static void addParticipants(Event newEvent) {
        boolean isMoreParticipant = true;
        System.out.println(" === Adds Participants to Event ===");
        while (isMoreParticipant) {
            addParticipant(newEvent);
            if (!UserInputHandler.getYesNoInput("Do you want to add more participant?"))
                isMoreParticipant = false;
        }
    }

    private static void addParticipant(Event event) {
        String name = UserInputHandler.getStringInput("Enter Participant Name:");
        Participant participant = new Participant(name);

        String phone = UserInputHandler.getStringInput("Enter Participant Phone Number:");
        participant.setPhoneNumber(phone);
        String email = UserInputHandler.getStringInput("Enter Participant email");
        participant.setEmail(email);
        // ask the user to enter the categories that need to add to the participant expenses
        enterExpensesCategories(participant, event.getCategories());
        enterConsumedCategories(participant, event.getCategories());

        // adds the participant to the event
        event.addParticipant(participant);
    }

    private static void enterExpensesCategories(Participant participant, List<Category> categories) {
        boolean isMoreCategory = true;
        // copy the categories list, so I can change it without effect the original list
        List<Category> displayCategories = new ArrayList<>(categories);
        System.out.println("Enter Categories that " + participant.getName() +" Expense Money on");

        while (isMoreCategory) {
            // ask from the user to choose category that the participant spent on
            System.out.println("Enter Category Number The Participant Spent Money On From These Categories:" +
                    " if there are none enter 0");
            System.out.println(MenuPrinter.displayNumberedCategories(displayCategories));

            // get user answer and create new category
            int choice = UserInputHandler.getIntInput("Your choice: ");
            if (choice == 0)
                break;
            Category expenseCategory =  CategoryInteractionHandler.getCategoryFromNumber(choice, displayCategories);

            // get the amount that the participant spent on the category
            double amount = UserInputHandler.getDoubleInput("Enter the amount of money you spent on this category.");

            // adds the expense to the participant expenses list
            participant.addExpense(expenseCategory, amount);



            // ask the user if there are more categories that the participant spent money on
            if (!UserInputHandler.getYesNoInput("Is there are more categories that the participant spent money on?"))
                isMoreCategory = false;

        }

    }

    private static void enterConsumedCategories(Participant participant, List<Category> categories) {
        boolean isMoreCategory = true;
        // copy the categories list, so I can change it without effect the original list
        List<Category> displayCategories = new ArrayList<>(categories);
        System.out.println("Enter Categories that " + participant.getName() +" Consumed");

        while (isMoreCategory || !displayCategories.isEmpty()) {
            System.out.println("Enter Category Number The Participant Consumed From These Categories:" +
                    " if there are none enter 0");
            System.out.println(MenuPrinter.displayNumberedCategories(displayCategories));

            int choice = UserInputHandler.getIntInput("Your choice: ");
            boolean validInput = false;

            while (!validInput) {
                if (choice == 0) {
                    return;
                }
                if (choice >= 1 && choice <= displayCategories.size()) {
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 1 and " + displayCategories.size() + ", or 0 to skip.");
                }
            }

            Category consumedCategory = CategoryInteractionHandler.getCategoryFromNumber(choice, displayCategories);
            participant.addConsumedCategory(consumedCategory);

            if (!UserInputHandler.getYesNoInput("Are there more categories that the participant consumed?")) {
                isMoreCategory = false;
            }
        }

    }

    private static void handlePhoneNumberEdit(Event event) {
        Participant participantToEdit = getParticipantFromUser(event, "Enter Participant Number you wants to edit it's phone number:" +
                " if there are none enter 0");
        if (participantToEdit == null) {
            System.out.println("There is no participant!");
            return;
        }
        String newPhoneNumber = UserInputHandler.getStringInput(String.format("Enter new phone number for %s", participantToEdit.getName()));
        participantToEdit.setPhoneNumber(newPhoneNumber);
    }

    private static void handleEmailAddressEdit(Event event) {
        Participant participantToEdit = getParticipantFromUser(event, "Enter Participant Number you wants to edit it's email address:" +
                " if there are none enter 0");
        if (participantToEdit == null) {
            System.out.println("There is no participant!");
            return;
        }
        String newEmail = UserInputHandler.getStringInput(String.format("Enter new email address for %s", participantToEdit.getName()));
        participantToEdit.setEmail(newEmail);
    }
}