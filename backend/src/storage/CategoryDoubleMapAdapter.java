package storage;

import com.google.gson.*;
import logic.Category;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CategoryDoubleMapAdapter implements JsonSerializer<Map<Category, Double>>, JsonDeserializer<Map<Category, Double>> {

    @Override
    public JsonElement serialize(Map<Category, Double> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<Category, Double> entry : src.entrySet()) {
            jsonObject.addProperty(entry.getKey().getName(), entry.getValue());
        }
        return jsonObject;
    }

    @Override
    public Map<Category, Double> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<Category, Double> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String categoryName = entry.getKey();
            Double value = entry.getValue().getAsDouble();

            Category category = new Category(categoryName);
            map.put(category, value);
        }
        return map;
    }
}
