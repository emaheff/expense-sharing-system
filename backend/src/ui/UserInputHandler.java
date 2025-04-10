package ui;

import java.util.Scanner;

public class UserInputHandler {

    private static final Scanner scanner = new Scanner(System.in);

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

    public static String getStringInput(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    public static boolean getYesNoInput(String message) {
        System.out.println(message + " (Y/N)");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("Y");
    }

}
