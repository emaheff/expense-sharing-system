import ui.UserInterface;
import logic.EventManager;

public class Main {
    public static void main(String[] args) {
        UserInterface ui = new UserInterface(new EventManager());
        ui.start();
    }
}
