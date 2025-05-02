package backend.mapper;

import backend.logic.Event;
import backend.logic.Participant;
import backend.logic.Category;
import shared.DebtDto;
import shared.EventDto;
import shared.ParticipantDto;
import shared.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

public class EventDtoMapper {

    public static EventDto fromEvent(Event event) {
        List<CategoryDto> categoryDtos = event.getCategories().stream()
                .map(c -> new CategoryDto(c.getName()))
                .collect(Collectors.toList());

        List<ParticipantDto> participantDtos = event.getParticipants().stream()
                .map(p -> ParticipantDtoMapper.fromParticipant(p, categoryDtos))
                .collect(Collectors.toList());

        List<DebtDto> debtDtos = event.getDebts().stream()
                .map(d -> new DebtDto(d.getDebtor().getName(), d.getCreditor().getName(), d.getAmount()))
                .toList();

        EventDto eventDto = new EventDto(event.getEventName(), event.getDate(), event.getParticipationFee(), participantDtos, categoryDtos);
        eventDto.setDebts(debtDtos);
        return eventDto;
    }
}
