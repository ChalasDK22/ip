/**
 * Represents a task that takes place between two times.
 */
public class Event extends Task {
    private String from;
    private String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toDataString() {
        return "E | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription()) + " | " + escapeDataField(this.from)
                + " | " + escapeDataField(this.to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
