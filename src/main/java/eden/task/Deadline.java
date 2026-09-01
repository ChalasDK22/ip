package eden.task;

/**
 * Represents a task that must be completed by a given time.
 */
public class Deadline extends Task {
    private String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toDataString() {
        return "D | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription()) + " | " + escapeDataField(this.by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
