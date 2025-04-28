package backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import shared.EventDto;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @PostMapping
    public ResponseEntity<String> createEvent(@RequestBody EventDto eventDto) {
        System.out.println("Received event:");
        System.out.println("Name: " + eventDto.getName());
        System.out.println("Date: " + eventDto.getDate());
        System.out.println("Participation Fee: " + eventDto.getParticipationFee());

        //
        return ResponseEntity.ok("Event created successfully");
    }
}
