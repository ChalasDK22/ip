import java.nio.file.Path;

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
     * Greets the user and processes commands until the user exits.
     */
    public void run() {
        if (!this.isReady) {
            return;
        }

        this.ui.showWelcome();

        while (true) {
            String command = this.ui.readCommand();

            try {
                CommandType commandType = CommandType.from(command);
                if (commandType == CommandType.BYE) {
                    this.ui.showGoodbye();
                    break;
                } else if (commandType == CommandType.LIST) {
                    this.ui.showTaskList(this.tasks.asList());
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    Task task = this.tasks.mark(taskNumber);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    Task task = this.tasks.unmark(taskNumber);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.TODO) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a todo cannot be empty.");
                    }
                    Task task = new Todo(command.substring("todo".length()).trim());
                    this.tasks.add(task);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskAdded(task, this.tasks.size());
                } else if (commandType == CommandType.DEADLINE) {
                    String details = command.substring("deadline".length()).trim();
                    String[] parts = details.split("\\s+/by\\s+", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a deadline cannot be empty.");
                    }
                    String by = parts[1].trim();
                    Task task = new Deadline(description, by);
                    this.tasks.add(task);
                    this.storage.save(this.tasks.asList());
                    this.ui.showTaskAdded(task, this.tasks.size());
                } else if (commandType == CommandType.EVENT) {
                    String details = command.substring("event".length()).trim();
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
                    int taskNumber = Integer.parseInt(command.substring(7));
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
     * Starts Eden using the default task data file.
     *
     * @param args command-line arguments, which Eden does not use
     */
    public static void main(String[] args) {
        new Eden(Path.of("data", "eden.txt")).run();
    }
}
