package eden;

import java.nio.file.Path;

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
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Records whether Eden loaded its data successfully and can process commands.
     */
    private final boolean isReady;

    /**
     * Creates Eden and loads tasks from the given data file.
     *
     * @param filePath path to the task data file.
     */
    public Eden(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks = new TaskList();
        boolean isLoadedSuccessfully = false;

        try {
            loadedTasks = new TaskList(storage.load());
            isLoadedSuccessfully = true;
        } catch (EdenException exception) {
            ui.showError(exception.getMessage());
        }

        tasks = loadedTasks;
        isReady = isLoadedSuccessfully;
    }

    /**
     * Greets the user and processes commands until the user exits.
     */
    public void run() {
        if (!isReady) {
            return;
        }

        ui.showWelcome();

        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();

            try {
                CommandType commandType = CommandType.from(fullCommand);
                if (commandType == CommandType.BYE) {
                    Command command = new ExitCommand();
                    command.execute(tasks, ui, storage);
                    isExit = command.isExit();
                } else if (commandType == CommandType.LIST) {
                    Command command = new ListCommand();
                    command.execute(tasks, ui, storage);
                    isExit = command.isExit();
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(5));
                    Task task = tasks.mark(taskNumber);
                    storage.save(tasks.asList());
                    ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(7));
                    Task task = tasks.unmark(taskNumber);
                    storage.save(tasks.asList());
                    ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.TODO) {
                    String description = fullCommand.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                                "OOPS!!! The description of a todo cannot be empty.");
                    }
                    Task task = new Todo(fullCommand.substring("todo".length()).trim());
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());
                } else if (commandType == CommandType.DEADLINE) {
                    String details = fullCommand.substring("deadline".length()).trim();
                    String[] parts = details.split("\\s+/by\\s+", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                                "OOPS!!! The description of a deadline cannot be empty.");
                    }
                    String by = parts[1].trim();
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());
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
                    tasks.add(task);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(task, tasks.size());
                } else if (commandType == CommandType.DELETE) {
                    int taskNumber = Integer.parseInt(fullCommand.substring(7));
                    Task task = tasks.delete(taskNumber);
                    storage.save(tasks.asList());
                    ui.showTaskDeleted(task, tasks.size());
                } else {
                    throw new EdenException(
                            "OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (EdenException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Starts Eden using the default task data file.
     *
     * @param args command-line arguments, which Eden does not use.
     */
    public static void main(String[] args) {
        new Eden(Path.of("data", "eden.txt")).run();
    }
}
