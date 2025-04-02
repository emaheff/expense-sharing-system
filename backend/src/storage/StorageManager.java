package storage;

import logic.Event;

import java.io.FileWriter;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StorageManager {


    public void saveEventToFile(Event event) {
        // Step 1: Ensure the directory exists
        ensureEventsDirectoryExists();

        // Step 2: Determine a file path and resolve conflicts
        Path filePath = determineUniqueFilePath(event);
        if (filePath == null) return;

        // Step 3: Convert event object to JSON string
        String jsonContent = convertEventToJson(event);

        // Step 4: Write JSON string to file
        writeJsonToFile(filePath, jsonContent);
    }

    private void ensureEventsDirectoryExists() {
        Path eventsDirectory = Paths.get("docs", "events");
        try {
            Files.createDirectories(eventsDirectory);
        } catch (IOException e) {
            System.out.println("Failed to create events directory: " + e.getMessage());
        }
    }

    private Path determineUniqueFilePath(Event event) {
        Scanner scanner = new Scanner(System.in);
        Path eventsDirectory = Paths.get("docs", "events");
        String fileName = event.getEventName().trim() + ".json";
        Path filePath = eventsDirectory.resolve(fileName);

        while (Files.exists(filePath)) {
            System.out.println("A file named \"" + fileName + "\" already exists.");
            System.out.print("Do you want to overwrite it? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) {
                break;
            } else if (response.equals("no")) {
                System.out.print("Enter a new name for the event: ");
                String newName = scanner.nextLine().trim();
                fileName = newName + ".json";
                filePath = eventsDirectory.resolve(fileName);
            } else {
                System.out.println("Please answer 'yes' or 'no'.");
            }
        }

        return filePath;
    }

    private String convertEventToJson(Event event) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(event);
    }

    private void writeJsonToFile(Path filePath, String jsonContent) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(jsonContent);
            System.out.println("Event saved successfully to: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save event: " + e.getMessage());
        }
    }


    public Event loadEventFromFile() {
        // Step 1: list available files
        List<Path> files = listSavedEventFiles();
        if (files.isEmpty()) {
            return null;
        }

        // Step 2: let user select one
        Path selectedFile = promptUserToSelectFile(files);
        if (selectedFile == null) {
            return null;
        }

        // Step 3: read content
        String json = readFileContent(selectedFile);
        if (json == null) {
            return null;
        }

        // Step 4: parse JSON into Event object
        Event event = parseJsonToEvent(json);
        if (event != null) {
            System.out.println("Event loaded successfully: " + event.getEventName());
        }
        return event;
    }


    private List<Path> listSavedEventFiles() {
        List<Path> eventFiles = new ArrayList<>();
        Path eventsDirectory = Paths.get("docs", "events");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(eventsDirectory, "*.json")) {
            int index = 1;
            for (Path file : stream) {
                System.out.println(index + ". " + file.getFileName());
                eventFiles.add(file);
                index++;
            }
        } catch (IOException e) {
            System.out.println("Failed to list event files: " + e.getMessage());
        }

        if (eventFiles.isEmpty()) {
            System.out.println("No saved events found.");
        }

        return eventFiles;
    }

    private Path promptUserToSelectFile(List<Path> files) {
        if (files.isEmpty()) {
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (true) {
            System.out.print("Enter the number of the event to load: ");
            String input = scanner.nextLine().trim();

            try {
                choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= files.size()) {
                    return files.get(choice - 1);
                } else {
                    System.out.println("Please enter a number between 1 and " + files.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private String readFileContent(Path filePath) {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
            return null;
        }
    }

    private Event parseJsonToEvent(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Event.class);
        } catch (Exception e) {
            System.out.println("Failed to parse event from JSON: " + e.getMessage());
            return null;
        }
    }

    public List<String> listSavedEvents() {
        return null;
    }
}
