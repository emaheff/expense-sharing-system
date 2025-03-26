package ui;

import logic.Event;
import logic.EventManager;
import logic.Debt;
import java.util.List;

public class UserInterface {

    private EventManager eventManager;

    public void start() {
        // Entry point of the program's UI
    }

    public void displayMainMenu() {
        // Show main options to the user
    }

    public void displayEventSummary(Event event) {
        // Show details of the current event
    }

    public void displayDebts(List<Debt> debts) {
        // Display who owes whom and how much
    }

    public void collectEventData() {
        // Prompt user for event details and create new event
    }

    public void collectParticipantData() {
        // Prompt user to add participants and their info
    }

    public void handleEditFlow() {
        // Allow user to edit existing event or participant
    }
}
