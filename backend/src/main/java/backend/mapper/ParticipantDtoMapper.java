package backend.mapper;

import backend.logic.Participant;
import backend.logic.Category;
import shared.ParticipantDto;
import shared.CategoryDto;

import java.util.*;
import java.util.stream.Collectors;

public class ParticipantDtoMapper {

    public static ParticipantDto fromParticipant(Participant participant, List<CategoryDto> categoryDtos) {
        ParticipantDto dto = new ParticipantDto(participant.getName(), participant.getPhoneNumber());


        List<CategoryDto> consumedDtos = participant.getConsumedCategories().stream()
                .map(c -> new CategoryDto(c.getName()))
                .collect(Collectors.toList());
        dto.setConsumedCategories(consumedDtos);


        Map<CategoryDto, Double> expenseMap = new HashMap<>();
        for (Map.Entry<Category, Double> entry : participant.getExpenses().entrySet()) {
            CategoryDto dtoKey = new CategoryDto(entry.getKey().getName());
            expenseMap.put(dtoKey, entry.getValue());
        }
        dto.setExpenses(expenseMap);

        dto.setTotalExpense(participant.getTotalExpense());
        dto.setTotalConsumed(participant.getTotalConsumed());

        return dto;
    }
}
