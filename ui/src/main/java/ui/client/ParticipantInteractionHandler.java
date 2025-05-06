package ui.client;
import shared.CategoryDto;
import shared.ParticipantDto;

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
     * @param eventParticipants the event participants
     * @param header the message to display before the list
     * @return the selected participant or null if the user skips
     */
    private static ParticipantDto getParticipantFromUser(List<ParticipantDto> eventParticipants, String header) {
        List<ParticipantDto> participants = new ArrayList<>(eventParticipants);
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
    public static ParticipantDto getParticipantFromNumber(int userNumber, List<ParticipantDto> displayedParticipants) {
        return displayedParticipants.get(userNumber - 1);
    }

    public static List<ParticipantDto> getParticipantsFromUser() {
        List<ParticipantDto> eventParticipants = new ArrayList<>();
        boolean isMoreParticipant = true;

        System.out.println(" === Adds Participants to Event ===");
        while (isMoreParticipant) {
            String name = UserInputHandler.getStringInput("Enter Participant Name:");
            String phone = UserInputHandler.getStringInput("Enter Participant Phone Number:");
            ParticipantDto participant = new ParticipantDto(name, phone);
            eventParticipants.add(participant);
            if (!UserInputHandler.getYesNoInput("Do you want to add more participant?")) {
                isMoreParticipant = false;
            }
        }
        return eventParticipants;
    }

    public static void setConsumers(List<CategoryDto> categories, List<ParticipantDto> participants) {
        for (CategoryDto category: categories) {
            List<ParticipantDto> participantsToDisplay = new ArrayList<>(participants);
            while (!participantsToDisplay.isEmpty()) {
                int participantNumber =  UserInputHandler.getIntInput("Enter the participant number that consumed "
                        + category.getName() + "\nEnter 0 if no one in this list consumed this category\n" +
                        MenuPrinter.displayNumberedParticipants(participantsToDisplay));

                if (participantNumber == 0) {
                    break;
                } else {
                    ParticipantDto participant = getParticipantFromNumber(participantNumber, participantsToDisplay);
                    participant.addConsumedCategory(category.getName());
                    participantsToDisplay.remove(participant);
                }
            }
        }
    }

    public static void setParticipantsExpenses(List<CategoryDto> categories, List<ParticipantDto> participants) {
        for (CategoryDto category: categories) {
            List<ParticipantDto> participantsToDisplay = new ArrayList<>(participants);
            while (!participantsToDisplay.isEmpty()) {
                int participantNumber =  UserInputHandler.getIntInput("Enter the participant number that spent money on "
                        + category.getName() + "\nEnter 0 if no one in this list spent money on this category\n" +
                        MenuPrinter.displayNumberedParticipants(participantsToDisplay));

                if (participantNumber == 0) {
                    break;
                } else {
                    ParticipantDto participant = getParticipantFromNumber(participantNumber, participantsToDisplay);
                    double amount = UserInputHandler.getDoubleInput("Enter the amount of money that " + participant.getName() +
                            " spent on " + category.getName());
                    participant.addExpense(category.getName(), amount);
                    participantsToDisplay.remove(participant);
                }
            }
        }
    }
}
