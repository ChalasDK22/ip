package eden.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toDataString() {
        return "T | " + (isMarked() ? "1" : "0") + " | "
                + escapeDataField(getDescription());
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
