package storage;

import com.google.gson.JsonSyntaxException;
import logic.*;

import java.io.File;
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

import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class StorageManager {

    private static final Path EVENTS_DIRECTORY = Paths.get("docs", "events");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<Map<Category, Double>>() {}.getType(),
                    new CategoryDoubleMapAdapter()
            )
            .registerTypeAdapter(
                    new TypeToken<Map<Category, List<Participant>>>() {}.getType(),
                    new storage.CategoryToParticipantListAdapter()
            )

            .registerTypeAdapter(
                    new TypeToken<Map<Category, Map<Participant, Double>>>() {}.getType(),
                    new storage.CategoryToParticipantDoubleMapAdapter()
            )
            .setPrettyPrinting()
            .create();

    public boolean saveEventToFile(Event event) {
        ensureEventsDirectoryExists();

        Path filePath = getEventFilePath(event.getEventName());

        return writeJsonToFile(event, filePath);
    }

    private void ensureEventsDirectoryExists() {
        Path eventsDirectory = Paths.get("docs", "events");
        try {
            Files.createDirectories(eventsDirectory);
        } catch (IOException e) {
            System.out.println("Failed to create events directory: " + e.getMessage());
        }
    }

    public boolean doesEventFileExist(String eventName) {
        Path filePath = getEventFilePath(eventName);
        return Files.exists(filePath);
    }

    private Path getEventFilePath(String eventName) {
        return EVENTS_DIRECTORY.resolve(eventName + ".json");
    }

    private File getEventsDirectoryFile() {
        return EVENTS_DIRECTORY.toFile();
    }

    private boolean writeJsonToFile(Event event, Path filePath) {
        try {
            String json = gson.toJson(event);
            Files.write(filePath, json.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public List<String> getSavedEventNames() {
        List<String> eventNames = new ArrayList<>();
        File dir = getEventsDirectoryFile();

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.endsWith(".json")) {
                        eventNames.add(name.replace(".json", ""));
                    }
                }
            }
        }
        return eventNames;
    }

    public Event loadEventByName(String eventName) {
        try {
            Path filePath = getEventFilePath(eventName);
            String json = Files.readString(filePath);
            Gson gson = this.gson;
            return gson.fromJson(json, Event.class);
        } catch (IOException | JsonSyntaxException e) {
            return null;
        }
    }

}
