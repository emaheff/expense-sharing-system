package backend.mapper;

import backend.logic.Event;
import backend.logic.Participant;
import backend.logic.Category;
import shared.CategoryDto;
import shared.EventDto;
import shared.ParticipantDto;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static Event toEvent(EventDto dto) {

        Event event = new Event(dto.getName(), dto.getParticipationFee(), dto.getDate());

        List<Category> categories = new ArrayList<>();
        for (CategoryDto categoryDto: dto.getCategories()) {
            Category category = new Category(categoryDto.getName());
            categories.add(category);
        }
        event.setCategories(categories);

        List<Participant> participants = new ArrayList<>();
        for (ParticipantDto participantDto: dto.getParticipants()) {
            Participant participant = new Participant(participantDto.getName(), participantDto.getPhone());
            for (String categoryDto: participantDto.getConsumedCategories()) {
                for (Category category: categories) {
                    if (categoryDto.equals(category.getName())) {
                        participant.addConsumedCategory(category);
                    }
                }
            }
            for (String categoryDto: participantDto.getExpenses().keySet()) {
                for (Category category: categories) {
                    if (categoryDto.equals(category.getName())) {
                        participant.addExpense(category, participantDto.getExpenses().get(categoryDto));
                    }
                }
            }
            participants.add(participant);
        }
        event.setParticipants(participants);
        return event;
    }
}
