package storage;

import com.google.gson.*;
import logic.Category;
import logic.Participant;

import java.lang.reflect.Type;
import java.util.*;

public class CategoryToParticipantListAdapter implements JsonSerializer<Map<Category, List<Participant>>>,
        JsonDeserializer<Map<Category, List<Participant>>> {

    @Override
    public JsonElement serialize(Map<Category, List<Participant>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<Category, List<Participant>> entry : src.entrySet()) {
            String categoryName = entry.getKey().getName();  // assume unique name
            JsonElement participantsJson = context.serialize(entry.getValue());
            jsonObject.add(categoryName, participantsJson);
        }
        return jsonObject;
    }

    @Override
    public Map<Category, List<Participant>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Map<Category, List<Participant>> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String categoryName = entry.getKey();
            Category category = new Category(categoryName);  // construct Category from name
            List<Participant> participants = context.deserialize(entry.getValue(), List.class);
            map.put(category, participants);
        }
        return map;
    }
}
