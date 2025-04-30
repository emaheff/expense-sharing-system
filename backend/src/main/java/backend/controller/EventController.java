package backend.controller;

import backend.logic.Event;
import backend.mapper.EventMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import shared.ParticipantDto;
import shared.EventDto;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody EventDto eventDto) {

        Event event = EventMapper.toEvent(eventDto);

        event.finalizeCalculations();

        return ResponseEntity.ok("Event '" + event.getEventName() + "' processed successfully with " +
                event.getParticipants().size() + " participants and " +
                event.getCategories().size() + " categories.");
    }
}
