package backend.logic;
import java.time.LocalDate;
import java.util.*;

/**
 * Represents a financial event that involves participants, categories,
 * expenses, consumptions, and calculated debts.
 *
 * An event tracks how much each participant spent and consumed, and allows
 * for calculating balances and debts between participants.
 */
public class Event {
    private int id;
    private String eventName;
    private LocalDate date;
    private double participationFee;

    private List<Participant> participants;
    private List<Category> categories;
    private List<Debt> debts;

    private Map<Category, Double> totalExpensePerCategory;
    private Map<Category, Double> adjustedTotalExpensePerCategory;
    private Map<Category, List<Participant>> consumedPerCategory;
    private Map<Category, Map<Participant, Double>> expensePerCategory;


    /**
     * Constructs a new Event with the given name, participation fee, and date.
     *
     * @param name             the name of the event
     * @param participationFee the fee each participant pays
     * @param date             the date of the event
     */
    public Event(String name, double participationFee, LocalDate date) {
        this.eventName = name;
        this.participationFee = participationFee;
        this.date = date;

        this.categories = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.debts = new ArrayList<>();

        this.totalExpensePerCategory = new HashMap<>();
        this.consumedPerCategory = new HashMap<>();
        this.expensePerCategory = new HashMap<>();
        this.adjustedTotalExpensePerCategory = new HashMap<>();
    }

    /**
     * Adds a participant to the event.
     *
     * @param participant the participant to add
     */
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    /**
     * Adds a category to the event.
     *
     * @param category the category to add
     */
    public void addCategory(Category category) {
        categories.add(category);
    }

    // ---------------------- Getters and Setters ----------------------

    public String getEventName() {
        return eventName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setDebts(List<Debt> debts) {
        this.debts = debts;
    }

    public List<Debt> getDebts() {
        return debts;
    }

    public double getParticipationFee() {
        return participationFee;
    }

    public void setEventName(String name) {
        this.eventName = name;
    }

    public void setParticipationFee(double amount) {
        this.participationFee = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Category, List<Participant>> getConsumedPerCategory() {
        return consumedPerCategory;
    }

    public Map<Category, Map<Participant, Double>> getExpensePerCategory() {
        return expensePerCategory;
    }

    public Map<Category, Double> getTotalExpensePerCategory() {
        return totalExpensePerCategory;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setAdjustedTotalExpensePerCategory(Map<Category, Double> adjustedTotalExpensePerCategory) {
        this.adjustedTotalExpensePerCategory = adjustedTotalExpensePerCategory;
    }

    public Map<Category, Double> getAdjustedTotalExpensePerCategory() {
        return adjustedTotalExpensePerCategory;
    }

    // ---------------------- Calculation Methods ----------------------

    /**
     * Populates the expensePerCategory map with participant expenses per category.
     */
    public void fillExpensePerCategory() {
        expensePerCategory.clear();

        // iterate throw all categories in this event
        for (Category category : categories) {
            Map<Participant, Double> participantExpenses = new HashMap<>();

            // iterate throw all participants in this event
            for (Participant participant : participants) {

                // getExpenses returns Map<Category, Double> - it's all the category that the participant spent money on
                // and the amount of money that he spent on the category
                Double amount = participant.getExpenses().get(category);
                if (amount != null && amount > 0) {
                    // put the amount of money that participant spent money on the current category int participantExpenses map
                    participantExpenses.put(participant, amount);
                }
            }
            expensePerCategory.put(category, participantExpenses);
        }
    }

    /**
     * Populates the consumedPerCategory map with participants who consumed each category.
     */
    public void fillConsumedPerCategory() {
        consumedPerCategory.clear();

        // iterate throw all categories in this event
        for (Category category : categories) {
            List<Participant> consumers = new ArrayList<>();

            // iterate throw all participants in this event
            for (Participant participant : participants) {
                if (participant.getConsumedCategories().contains(category)) {
                    consumers.add(participant);
                }
            }
            consumedPerCategory.put(category, consumers);
        }
    }

    /**
     * Populates the totalExpensePerCategory map with the total amount spent for each category.
     */
    public void fillTotalExpensePerCategory() {
        totalExpensePerCategory.clear();
        for (Map.Entry<Category, Map<Participant, Double>> entry : expensePerCategory.entrySet()) {
            Category category = entry.getKey();
            Map<Participant, Double> participantExpenses = entry.getValue();

            double total = 0.0;
            for (double amount : participantExpenses.values()) {
                total += amount;
            }

            totalExpensePerCategory.put(category, total);
        }
    }

    /**
     * Triggers all calculations needed to finalize the event data:
     * fills category maps and calculates participant balances.
     */
    public void finalizeCalculations() {
        fillConsumedPerCategory();
        fillExpensePerCategory();
        fillTotalExpensePerCategory();
        CalculationEngine engine = new CalculationEngine();
        engine.calculateBalances(this);
    }

    // ---------------------- equals, hashCode, toString ----------------------

    /**
     * @return a short summary of the event including its name and number of participants
     */
    @Override
    public String toString() {
        return "Event{name='%s', participants=%d}".formatted(eventName, participants.size());
    }

    /**
     * Checks equality based on event ID.
     *
     * @param o the object to compare with
     * @return true if the events have the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event other = (Event) o;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.eventName, other.eventName);
    }

    /**
     * @return the hash code of the event, based on its ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
