package backend.controller;

import backend.logic.Event;
import backend.mapper.EventDtoMapper;
import backend.mapper.EventMapper;
import backend.storage.EventDao;
import backend.storage.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import shared.EventSummaryDto;
import shared.ParticipantDto;
import shared.EventDto;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody EventDto eventDto) {

        Event event = EventMapper.toEvent(eventDto);

        event.finalizeCalculations();
        boolean dbSuccess = EventDao.insertOrUpdateEvent(event);
        if (dbSuccess) {
            ParticipantDao.saveEventParticipants(event);
            CategoryDao.saveEventCategories(event);
            DebtDao.saveEventDebts(event);
            ExpenseDao.saveEventExpenses(event);
            ExpenseDao.saveEventConsumptions(event);

            return ResponseEntity.ok("" + event.getId());
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EventSummaryDto>> getAllEvents() {
        List<EventDao.EventSummary> events = EventDao.getAllEvents();
        List<EventSummaryDto> summaries = events.stream()
                .map(e -> new EventSummaryDto(e.getId(), e.getName(), e.getDate()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable int id) {
        Event event = EventDao.loadEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        EventDto dto = EventDtoMapper.fromEvent(event);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<EventDto> getEventResults(@PathVariable int id) {
        Event event = EventDao.loadEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        event.finalizeCalculations();

        EventDto dto = EventDtoMapper.fromEvent(event);
        return ResponseEntity.ok(dto);
    }



}
