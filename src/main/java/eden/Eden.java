package eden;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import eden.command.Command;
import eden.command.ExitCommand;
import eden.command.FindCommand;
import eden.command.ListCommand;
import eden.exception.EdenException;
import eden.parser.CommandType;
import eden.storage.Storage;
import eden.task.Deadline;
import eden.task.Event;
import eden.task.Task;
import eden.task.TaskList;
import eden.task.Todo;
import eden.ui.Ui;

/**
 * Runs the Eden task manager and coordinates its user interface and storage.
 */
public class Eden {
    private static final String DEADLINE_DATE_ERROR =
            "OOPS!!! Please enter the deadline date as yyyy-MM-dd "
            + "(e.g., 2019-12-02).";
    private static final String EVENT_FORMAT_ERROR =
            "OOPS!!! Please enter an event as: "
            + "event DESCRIPTION /from START /to END.";
    private static final String TASK_NUMBER_ERROR =
            "OOPS!!! Please enter a valid task number.";
    private static final String UNKNOWN_COMMAND_ERROR =
            "OOPS!!! I'm sorry, but I don't know what that means :-(";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Records whether Eden loaded its data successfully and can process commands.
     */
    private final boolean isReady;
    private final String loadingError;

    private boolean isExit;

    /**
     * Creates Eden and attempts to load tasks from the given data file.
     * If loading fails, Eden reports the loading error and will not process commands.
     *
     * @param filePath path to the task data file.
     */
    public Eden(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks = new TaskList();
        boolean isLoadedSuccessfully = false;
        String loadError = null;

        try {
            loadedTasks = new TaskList(storage.load());
            isLoadedSuccessfully = true;
        } catch (EdenException exception) {
            loadError = exception.getMessage();
        }

        tasks = loadedTasks;
        isReady = isLoadedSuccessfully;
        loadingError = loadError;
    }

    /**
     * Greets the user and processes commands until the user exits, provided that
     * the task data loaded successfully. Otherwise, this method reports the loading
     * error and returns.
     */
    public void run() {
        if (!isReady) {
            ui.showResponse(loadingError);
            return;
        }

        ui.showWelcome();
        while (!isExit) {
            ui.showResponse(getResponse(ui.readCommand()));
        }
    }

    /**
     * Returns Eden's startup message for a graphical or other non-console interface.
     *
     * @return greeting, or the loading error if stored data could not be loaded.
     */
    public String getWelcomeMessage() {
        return isReady ? ui.getWelcomeMessage() : loadingError;
    }

    /**
     * Processes one user command and returns the response without console decoration.
     *
     * @param fullCommand full command entered by the user.
     * @return response suitable for a console or graphical interface.
     */
    public String getResponse(String fullCommand) {
        if (!isReady) {
            return loadingError;
        }

        try {
            return processCommand(fullCommand.trim());
        } catch (EdenException exception) {
            return exception.getMessage();
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return TASK_NUMBER_ERROR;
        }
    }

    /**
     * Returns whether the most recently processed command requested an exit.
     *
     * @return true after a successful bye command.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Routes a normalized command to the matching operation.
     */
    private String processCommand(String fullCommand) throws EdenException {
        CommandType commandType = CommandType.from(fullCommand);
        switch (commandType) {
            case BYE:
                return executeCommand(new ExitCommand());
            case LIST:
                return executeCommand(new ListCommand());
            case FIND:
                String keyword = fullCommand.substring("find".length()).trim();
                return executeCommand(new FindCommand(keyword));
            case MARK:
                return markTask(fullCommand);
            case UNMARK:
                return unmarkTask(fullCommand);
            case TODO:
                return addTodo(fullCommand);
            case DEADLINE:
                return addDeadline(fullCommand);
            case EVENT:
                return addEvent(fullCommand);
            case DELETE:
                return deleteTask(fullCommand);
            default:
                throw new EdenException(UNKNOWN_COMMAND_ERROR);
        }
    }

    /**
     * Executes a command object and remembers whether it exits Eden.
     */
    private String executeCommand(Command command) throws EdenException {
        String response = command.execute(tasks, ui, storage);
        isExit = command.isExit();
        return response;
    }

    /**
     * Marks the selected task and persists the change.
     */
    private String markTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "mark");
        Task task = tasks.mark(taskNumber);
        storage.save(tasks.asList());
        return ui.formatTaskMarked(task);
    }

    /**
     * Unmarks the selected task and persists the change.
     */
    private String unmarkTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "unmark");
        Task task = tasks.unmark(taskNumber);
        storage.save(tasks.asList());
        return ui.formatTaskUnmarked(task);
    }

    /**
     * Creates a todo from its command text.
     */
    private String addTodo(String fullCommand) throws EdenException {
        String description = fullCommand.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of a todo cannot be empty.");
        }
        return addTask(new Todo(description));
    }

    /**
     * Creates a deadline from its description and ISO date.
     */
    private String addDeadline(String fullCommand) throws EdenException {
        String details = fullCommand.substring("deadline".length()).trim();
        String[] parts = details.split("(?:^|\\s+)/by\\s+", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new EdenException(DEADLINE_DATE_ERROR);
        }

        LocalDate dueDate = parseDeadlineDate(parts[1].trim());
        return addTask(new Deadline(description, dueDate));
    }

    /**
     * Creates an event from its description, start, and end text.
     */
    private String addEvent(String fullCommand) throws EdenException {
        String details = fullCommand.substring("event".length()).trim();
        String[] fromParts = details.split("\\s+/from\\s+", 2);
        if (fromParts.length < 2) {
            throw new EdenException(EVENT_FORMAT_ERROR);
        }

        String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of an event cannot be empty.");
        }
        if (toParts.length < 2 || toParts[0].isBlank() || toParts[1].isBlank()) {
            throw new EdenException(EVENT_FORMAT_ERROR);
        }

        return addTask(new Event(description, toParts[0].trim(), toParts[1].trim()));
    }

    /**
     * Adds and persists one newly parsed task.
     */
    private String addTask(Task task) throws EdenException {
        tasks.add(task);
        storage.save(tasks.asList());
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Deletes the selected task and persists the change.
     */
    private String deleteTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "delete");
        Task task = tasks.delete(taskNumber);
        storage.save(tasks.asList());
        return ui.formatTaskDeleted(task, tasks.size());
    }

    /**
     * Parses the one-based task number after a command word.
     */
    private int parseTaskNumber(String fullCommand, String commandWord) throws EdenException {
        String numberText = fullCommand.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new EdenException(TASK_NUMBER_ERROR);
        }
        return Integer.parseInt(numberText);
    }

    /**
     * Parses a deadline date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText deadline date entered by the user.
     * @return parsed date.
     * @throws EdenException if the text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String dateText) throws EdenException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw new EdenException(DEADLINE_DATE_ERROR, exception);
        }
    }

    /**
     * Starts Eden using {@code data/eden.txt}, resolved relative to the process's
     * working directory.
     *
     * @param args command-line arguments, which Eden does not use.
     */
    public static void main(String[] args) {
        new Eden(Path.of("data", "eden.txt")).run();
    }
}
