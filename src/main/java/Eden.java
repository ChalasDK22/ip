import java.nio.file.Path;
import java.util.List;

/**
 * Runs the Eden task manager and coordinates its user interface and storage.
 */
public class Eden {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "eden.txt"));
        List<Task> tasks;

        try {
            tasks = storage.load();
        } catch (EdenException exception) {
            ui.showError(exception.getMessage());
            return;
        }

        ui.showWelcome();

        while (true) {
            String command = ui.readCommand();

            try {
                CommandType commandType = CommandType.from(command);
                if (commandType == CommandType.BYE) {
                    ui.showGoodbye();
                    break;
                } else if (commandType == CommandType.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    Task task = tasks.get(taskNumber - 1);
                    task.mark();
                    storage.save(tasks);
                    ui.showTaskMarked(task);
                } else if (commandType == CommandType.UNMARK) {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    storage.save(tasks);
                    ui.showTaskUnmarked(task);
                } else if (commandType == CommandType.TODO) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a todo cannot be empty.");
                    }
                    Task task = new Todo(command.substring("todo".length()).trim());
                    tasks.add(task);
                    storage.save(tasks);
                    ui.showTaskAdded(task, tasks.size());
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
                    tasks.add(task);
                    storage.save(tasks);
                    ui.showTaskAdded(task, tasks.size());
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
                    tasks.add(task);
                    storage.save(tasks);
                    ui.showTaskAdded(task, tasks.size());
                } else if (commandType == CommandType.DELETE) {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    Task task = tasks.remove(taskNumber - 1);
                    storage.save(tasks);
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
}
