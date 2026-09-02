package eden;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import eden.command.Command;
import eden.command.ExitCommand;
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

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Records whether Eden loaded its data successfully and can process commands.
     */
    private final boolean isReady;

    /**
     * Creates Eden and attempts to load tasks from the given data file.
     * If loading fails, Eden displays the loading error and will not process commands.
     *
     * @param filePath path to the task data file
     */
    public Eden(Path filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loadedTasks = new TaskList();
        boolean loadedSuccessfully = false;

        try {
            loadedTasks = new TaskList(this.storage.load());
            loadedSuccessfully = true;
        } catch (EdenException exception) {
            this.ui.showError(exception.getMessage());
        }

        this.tasks = loadedTasks;
        this.isReady = loadedSuccessfully;
    }

    /**
     * Greets the user and processes commands until the user exits, provided that
     * the task data loaded successfully. Otherwise, this method returns immediately.
     */
    public void run() {
        if (!this.isReady) {
            return;
        }

        this.ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = this.ui.readCommand();

            try {
                CommandType commandType = CommandType.from(fullCommand);
                if (commandType == CommandType.BYE) {
                    Command command = new ExitCommand();
                    command.execute(this.tasks, this.ui, this.storage);
                    isExit = command.isExit();
                } else if (commandType == CommandType.LIST) {
                    Command command = new ListCommand();
                    command.execute(this.tasks, this.ui, this.storage);
                    isExit = command.isExit();
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(5));
                    Task task = this.tasks.mark(taskNumber);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(7));
                    Task task = this.tasks.unmark(taskNumber);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.TODO) {
                    String description = fullCommand.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a todo cannot be empty.");
                    }
                    Task task = new Todo(fullCommand.substring("todo".length()).trim());
                    this.tasks.add(task);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskAdded(task, this.tasks.size());
                } else if (commandType == CommandType.DEADLINE) {
                    String details = fullCommand.substring("deadline".length()).trim();
                    String[] parts = details.split("(?:^|\\s+)/by\\s+", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a deadline cannot be empty.");
                    }
                    if (parts.length < 2 || parts[1].isBlank()) {
                        throw new EdenException(DEADLINE_DATE_ERROR);
                    }
                    LocalDate by = parseDeadlineDate(parts[1].trim());
                    Task task = new Deadline(description, by);
                    this.tasks.add(task);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskAdded(task, this.tasks.size());
                } else if (commandType == CommandType.EVENT) {
                    String details = fullCommand.substring("event".length()).trim();
                    String[] fromParts = details.split("\\s+/from\\s+", 2);
                    String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of an event cannot be empty.");
                    }
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    Task task = new Event(description, from, to);
                    this.tasks.add(task);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskAdded(task, this.tasks.size());
                } else if (commandType == CommandType.DELETE) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(7));
                    Task task = this.tasks.delete(taskNumber);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskDeleted(task, this.tasks.size());
                } else {
                    throw new EdenException(
                            "OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (EdenException exception) {
                this.ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Parses a deadline date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText deadline date entered by the user
     * @return parsed date
     * @throws EdenException if the text is not a valid ISO date
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
     * @param args command-line arguments, which Eden does not use
     */
    public static void main(String[] args) {
        new Eden(Path.of("data", "eden.txt")).run();
    }
}
