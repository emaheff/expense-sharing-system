package storage;

import com.google.gson.*;
import logic.Category;
import logic.Participant;

import java.lang.reflect.Type;
import java.util.*;

public class CategoryToParticipantDoubleMapAdapter implements JsonSerializer<Map<Category, Map<Participant, Double>>>,
        JsonDeserializer<Map<Category, Map<Participant, Double>>> {

    @Override
    public JsonElement serialize(Map<Category, Map<Participant, Double>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        for (Map.Entry<Category, Map<Participant, Double>> outerEntry : src.entrySet()) {
            String categoryName = outerEntry.getKey().getName();
            JsonObject participantMapJson = new JsonObject();

            for (Map.Entry<Participant, Double> innerEntry : outerEntry.getValue().entrySet()) {
                participantMapJson.addProperty(innerEntry.getKey().getName(), innerEntry.getValue());
            }

            result.add(categoryName, participantMapJson);
        }

        return result;
    }

    @Override
    public Map<Category, Map<Participant, Double>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Map<Category, Map<Participant, Double>> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> outerEntry : jsonObject.entrySet()) {
            Category category = new Category(outerEntry.getKey());
            Map<Participant, Double> participantMap = new HashMap<>();

            JsonObject participantJson = outerEntry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> innerEntry : participantJson.entrySet()) {
                Participant participant = new Participant(innerEntry.getKey());
                Double value = innerEntry.getValue().getAsDouble();
                participantMap.put(participant, value);
            }

            map.put(category, participantMap);
        }

        return map;
    }
}
