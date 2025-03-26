package logic;
import java.util.List;

public class Event {
    private String eventName;
    private double participantFee;
    private List<Category> categories;
    private List<Participant> participants;
    private boolean isFinalized;
    private boolean isDraft;

    // adds new participant to participants list
    public void addParticipant(Participant participant) {}

    // removes a participant from participants list
    public void removeParticipant(Participant participant) {}

    //
    public List<Debt> calculateBalances() {
        return null;
    }

    // adds participant fee for the event participation
    public void applyParticipantFees() {}

    public String getEventName() {
        return eventName;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
