package ui.client;

import shared.EventDto;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

import com.google.gson.Gson;

public class EventApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/events";
    private static final Gson gson = new Gson();

    public void createEvent(EventDto eventDto) {
        try {
            // connection creation
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // convert object to Json
            String json = gson.toJson(eventDto);

            // sands the body request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // getting response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Event created successfully.");
            } else {
                System.out.println("Failed to create event. Response code: " + responseCode);
            }

            connection.disconnect();

        } catch (IOException e) {
            System.out.println("Error while creating event: " + e.getMessage());
        }
    }
}
