package ui;

import java.util.Scanner;

/**
 * UserInputHandler provides utility methods to read and validate user input
 * from the console, including integers, doubles, strings, and yes/no choices.
 */
public class UserInputHandler {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user with a message and reads an integer value.
     * Repeats the prompt until a valid integer is entered.
     *
     * @param message the message shown to the user
     * @return the integer entered by the user
     */
    public static int getIntInput(String message) {
        while (true) {
            try {
                System.out.println(message);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /**
     * Prompts the user with a message and reads a double value.
     * Repeats the prompt until a valid double is entered.
     *
     * @param message the message shown to the user
     * @return the double entered by the user
     */
    public static double getDoubleInput(String message) {
        while (true) {
            try {
                System.out.println(message);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    /**
     * Prompts the user with a message and reads a line of text.
     *
     * @param message the message shown to the user
     * @return the text entered by the user
     */
    public static String getStringInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    /**
     * Prompts the user for a yes/no question and returns true if the answer is "Y" or "y".
     *
     * @param message the question shown to the user
     * @return true if the user answered "Y" (case-insensitive), false otherwise
     */
    public static boolean getYesNoInput(String message) {
        System.out.println(message + " (Y/N)");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Y");
    }

}