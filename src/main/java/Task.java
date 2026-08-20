/**
 * Represents a task stored by Eden.
 */

public class Task {
    private String description;
    private boolean isMarked;

    public Task(String description) {
        this.description = description;
        this.isMarked = false;
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Marks this task as completed.
     */
    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Returns the completion status as an icon.
     */

    @Override
    public String toString() {
        if (isMarked) {
            return "[X] " + this.description;
        }
        return "[ ] " + this.description;
    }
}