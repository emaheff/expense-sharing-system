package shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantDto {
    private String name;
    private String phone;
    private List<CategoryDto> consumedCategories;
    private Map<CategoryDto, Double> expenses;

    public ParticipantDto() {}  // required for deserialization

    public ParticipantDto(String name, String phone) {
        this.name = name;
        this.phone = phone;
        consumedCategories = new ArrayList<>();
        expenses = new HashMap<>();
    }

    public void setConsumedCategories(List<CategoryDto> consumedCategories) {
        this.consumedCategories = consumedCategories;
    }

    public void setExpenses(Map<CategoryDto, Double> expenses) {
        this.expenses = expenses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryDto> getConsumedCategories() {
        return consumedCategories;
    }

    public Map<CategoryDto, Double> getExpenses() {
        return expenses;
    }

    public void addConsumedCategory(CategoryDto category) {
        consumedCategories.add(category);
    }

    public void addExpense(CategoryDto category, double amount) {
        expenses.put(category, amount);
    }
}
