package ui.client;

import shared.ParticipantDto;

import java.util.List;

public class MenuPrinter {

    /**
     * Displays the main menu options for the user.
     */
    public static void displayMainMenu() {
        System.out.println("\n=== Expense Sharing System ===");
        System.out.println("Please choose an option:");
        System.out.println("1. Create new event");
        System.out.println("2. Load existing event");
        System.out.println("3. Show event results");
        System.out.println("4. Edit current event");
        System.out.println("5. Delete current event");
        System.out.println("6. Export excel file of current event");
        System.out.println("7. Exit");
    }

    /**
     * Displays the participant management menu.
     */
    public static void displayManageParticipantMenu() {
        System.out.println("""
                === Manage Participants ===
                1. Add new participant
                2. Remove participant
                3. Edit existing participant
                4. Back to previous menu
                """);
    }

    /**
     * Displays the menu for managing categories in an event.
     */
    public static void displayManageCategoryMenu() {
        System.out.println("""
                === Manage Categories ===
                1. Add new category
                2. Rename existing category
                3. Remove category
                4. Back to previous menu
                """);
    }

    /**
     * Displays the event editing menu options.
     */
    public static void displayEditMenu() {
        System.out.println("""
                === Edit Event ===
                1. Rename event
                2. Edit event date
                3. Change participation fee
                4. Manage categories
                5. Manage participants
                6. Back to main menu
                """);
    }

    /**
     * Displays the menu for editing a specific participant.
     *
     * @param participant the participant to edit
     */
    public static void displayParticipantEditMenu(ParticipantDto participant) {
        System.out.println("=== Edit Participant: " + participant.getName() + " ===");
        System.out.println("""
                1. Rename participant
                2. Edit phone number
                3. Edit email address
                4. Edit expenses per category
                5. Edit consumed categories
                6. Back to previous menu
                """);
    }

    /**
     * Returns a string listing participants with numbered entries.
     *
     * @param participants the list of participants
     * @return formatted string of numbered participant names
     */
    public static String displayNumberedParticipants(List<ParticipantDto> participants) {
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= participants.size(); i++) {
            result.append(i).append(". ").append(participants.get(i -1).getName()).append("\n");
        }
        return result.toString();
    }

}
