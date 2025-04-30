package backend.mapper;

import backend.logic.Participant;
import backend.logic.Category;
import shared.ParticipantDto;
import shared.CategoryDto;

import java.util.List;
import java.util.Map;

public class ParticipantMapper {

    public static Participant toParticipant(ParticipantDto dto, List<Category> allCategories) {
        Participant participant = new Participant(dto.getName());

        for (CategoryDto consumed : dto.getConsumedCategories()) {
            String categoryName = consumed.getName();

            allCategories.stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .ifPresent(participant::addConsumedCategory);
        }

        for (Map.Entry<CategoryDto, Double> entry : dto.getExpenses().entrySet()) {
            String categoryName = entry.getKey().getName();
            Double amount = entry.getValue();

            allCategories.stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .ifPresent(c -> participant.addExpense(c, amount));
        }

        return participant;
    }
}
