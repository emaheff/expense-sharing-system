package ui;
import ui.client.MenuPrinter;
import ui.client.UserInterface;

public class ClientApp {
    public static void main(String[] args) {
        MenuPrinter.displayMainMenu();
        new UserInterface().start();
    }
}
