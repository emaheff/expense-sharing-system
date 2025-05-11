package backend.controller;

import backend.logic.Event;
import backend.mapper.EventDtoMapper;
import backend.mapper.EventMapper;
import backend.storage.EventDao;
import backend.storage.*;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import shared.CategoryDto;
import shared.EventSummaryDto;
import shared.ParticipantDto;
import shared.EventDto;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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

    @PutMapping("/{id}/name")
    public ResponseEntity<String> updateEventName(@PathVariable int id, @RequestBody String newNameJson) {
        try {
            String newName = newNameJson.replace("\"", "");

            Event event = EventDao.loadEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }

            event.setEventName(newName);
            boolean success = EventDao.insertOrUpdateEvent(event);
            if (success) {
                return ResponseEntity.ok(String.valueOf(event.getId()));
            } else {
                return ResponseEntity.status(500).body("Failed to update event Name");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/date")
    public ResponseEntity<String> updateEventDate(@PathVariable int id, @RequestBody String newDateJson) {
        try {
            String newDate = newDateJson.replace("\"", "");

            Event event = EventDao.loadEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
            LocalDate date = LocalDate.parse(newDate, formatter);
            event.setDate(date);
            boolean success = EventDao.insertOrUpdateEvent(event);
            if (success) {
                return ResponseEntity.ok(String.valueOf(event.getId()));
            } else {
                return ResponseEntity.status(500).body("Failed to update event Date");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/fee")
    public ResponseEntity<String> updateEventParticipationFee(@PathVariable int id, @RequestBody String fee) {
        try {
            String newDate = fee.replace("\"", "");

            Event event = EventDao.loadEventById(id);
            if (event == null) {
                return ResponseEntity.notFound().build();
            }

            double participationFee = Double.parseDouble(fee);
            event.setParticipationFee(participationFee);
            boolean success = EventDao.insertOrUpdateEvent(event);
            if (success) {
                return ResponseEntity.ok(String.valueOf(event.getId()));
            } else {
                return ResponseEntity.status(500).body("Failed to update event Date");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDto>> getParticipants(@PathVariable int id) {
        List<ParticipantDto> participantsDto = EventDao.getParticipants(id);
        return ResponseEntity.ok(participantsDto);
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(@PathVariable int id) {
        List<CategoryDto> categoriesDto = EventDao.getCategories(id);
        return ResponseEntity.ok(categoriesDto);
    }

    @PutMapping("/{id}/participants")
    public ResponseEntity<String> updateParticipants(@PathVariable int id, @RequestBody List<ParticipantDto> participantsDto) {
        try {
            EventDao.updateParticipantsForEvent(id, participantsDto);
            return ResponseEntity.ok(String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update participants for event " + id);
        }
    }

    @PutMapping("/categoryRename")
    public ResponseEntity<String> renameCategory(@RequestBody Map<String, Object> request) {
        try {
            int categoryId = ((Number) request.get("categoryId")).intValue();
            String newName = (String) request.get("newName");
            int eventId = ((Number) request.get("eventId")).intValue();

            CategoryDao.renameCategoryForEvent(categoryId, newName, eventId);
            return ResponseEntity.ok("Category renamed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to rename category: " + e.getMessage());
        }
    }



}
