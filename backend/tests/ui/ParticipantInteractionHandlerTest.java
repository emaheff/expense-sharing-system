package ui;

import logic.Category;
import logic.Participant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParticipantInteractionHandler class tests")
class ParticipantInteractionHandlerTest {

    @Test
    @DisplayName("Check that getCategoryFromNumber returns the correct category and removes it from the list")
    void testGetParticipantFromNumber() {
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant("Bob"));
        participants.add(new Participant("Eve"));
        participants.add(new Participant("Alice"));

        Participant chosenParticipant = ParticipantInteractionHandler.getParticipantFromNumber(2, participants);

        assertEquals("Eve", chosenParticipant.getName());
        assertEquals(3, participants.size());
        assertTrue(participants.contains(chosenParticipant));
    }
}
