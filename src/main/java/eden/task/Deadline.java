package eden.task;

/**
 * Represents a task that must be completed by a given time.
 */
public class Deadline extends Task {
    private String by;

    /**
     * Creates an unmarked deadline task.
     *
     * @param description task description.
     * @param by text describing when the task is due.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toDataString() {
        return "D | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription()) + " | " + escapeDataField(by);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
