package ui.client;

import com.google.gson.*;
import shared.EventDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import shared.EventSummaryDto;
import shared.ParticipantDto;

public class EventApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/events";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .create();

    public static int sendEvent(EventDto event) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.connect();

            String json = gson.toJson(event);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String responseBody = in.readLine();
                    return Integer.parseInt(responseBody);
                }
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Failed to send event: " + e.getMessage());
        }

        return -1;
    }


    public static List<EventSummaryDto> fetchEventSummaries() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                EventSummaryDto[] response = gson.fromJson(in, EventSummaryDto[].class);
                return Arrays.asList(response);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch event summaries: " + e.getMessage());
            return List.of(); // empty list in case of error
        }
    }

    public static List<ParticipantDto> fetchParticipantsEvent(int eventId) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/participants");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                ParticipantDto[] response = gson.fromJson(in, ParticipantDto[].class);
                return Arrays.asList(response);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch participants " + e.getMessage());
            return List.of(); // empty list in case of error
        }
    }

    public static int setParticipants(List<ParticipantDto> participants, int eventId) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/participants");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = gson.toJson(participants);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String response = in.readLine();
                    return Integer.parseInt(response);
                }
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Failed to update participants: " + e.getMessage());
        }

        return -1;
    }


    public static EventDto fetchEventById(int id) {
        try {
            URL url = new URL(BASE_URL + "/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                return gson.fromJson(in, EventDto.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch event with ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    public static EventDto fetchEventResultsById(int eventId) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/results");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                StringBuilder responseText = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    responseText.append(line);
                }

                String fullJson = responseText.toString();
                System.out.println("DEBUG: server response = " + fullJson);
                return gson.fromJson(fullJson, EventDto.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch event results: " + e.getMessage());
            return null;
        }
    }

    public static int changeEventName(int eventId, String newName) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/name");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = new Gson().toJson(newName);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String responseBody = in.readLine();
                    return Integer.parseInt(responseBody);
                }
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Failed to change event name: " + e.getMessage());
        }
        return -1;
    }

    public static int changeEventDate(int eventId, String date) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/date");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = new Gson().toJson(date);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String responseBody = in.readLine();
                    return Integer.parseInt(responseBody);
                }
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Failed to change event name: " + e.getMessage());
        }
        return -1;
    }

    public static int changeParticipationFee(int eventId, double participationFee) {
        try {
            URL url = new URL(BASE_URL + "/" + eventId + "/fee");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = new Gson().toJson(participationFee);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String responseBody = in.readLine();
                    return Integer.parseInt(responseBody);
                }
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Failed to change event name: " + e.getMessage());
        }
        return -1;
    }

}
