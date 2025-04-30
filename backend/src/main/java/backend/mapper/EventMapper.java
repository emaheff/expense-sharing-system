package backend.mapper;

import backend.logic.Event;
import backend.logic.Participant;
import backend.logic.Category;
import shared.EventDto;

import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    public static Event toEvent(EventDto dto) {

        Event event = new Event(dto.getName(), dto.getParticipationFee(), dto.getDate());

        List<Category> categories = dto.getCategories().stream()
                .map(CategoryMapper::toCategory)
                .collect(Collectors.toList());


        List<Participant> participants = dto.getParticipants().stream()
                .map(p -> ParticipantMapper.toParticipant(p, categories))
                .toList();


        categories.forEach(event::addCategory);
        participants.forEach(event::addParticipant);

        return event;
    }
}
