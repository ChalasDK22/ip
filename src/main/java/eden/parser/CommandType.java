package eden.parser;

/**
 * Represents the commands that Eden can process.
 */
public enum CommandType {
    /** Ends the current Eden session. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as not completed. */
    UNMARK,
    /** Adds a task without a date or time. */
    TODO,
    /** Adds a task with a due date or time. */
    DEADLINE,
    /** Adds a task with a start and end time. */
    EVENT,
    /** Removes a task. */
    DELETE,
    /** Represents input that does not name a supported command. */
    UNKNOWN;

    /**
     * Identifies the command type named by the first word of the input,
     * ignoring surrounding whitespace and letter case.
     *
     * @param input full user command.
     * @return matching command type, or {@link #UNKNOWN} if no command matches.
     */
    public static CommandType from(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return UNKNOWN;
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
