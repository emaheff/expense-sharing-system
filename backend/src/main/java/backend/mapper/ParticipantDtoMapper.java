package backend.mapper;

import backend.logic.Participant;
import backend.logic.Category;
import shared.ParticipantDto;
import shared.CategoryDto;

import java.util.*;

public class ParticipantDtoMapper {

    public static ParticipantDto fromParticipant(Participant participant, List<CategoryDto> categoryDtos) {
        ParticipantDto dto = new ParticipantDto(participant.getName(), participant.getPhoneNumber());
        dto.setId(participant.getId());


        List<String> consumedDtos = new ArrayList<>();
        for (Category category : participant.getConsumedCategories()) {
            consumedDtos.add(category.getName());
        }
        dto.setConsumedCategories(consumedDtos);

        Map<String, Double> expenseMap = new HashMap<>();
        for (Map.Entry<Category, Double> entry : participant.getExpenses().entrySet()) {
            String dtoKey = entry.getKey().getName();
            expenseMap.put(dtoKey, entry.getValue());
        }
        dto.setExpenses(expenseMap);

        dto.setTotalExpense(participant.getTotalExpense());
        dto.setTotalConsumed(participant.getTotalConsumed());

        return dto;
    }
}
