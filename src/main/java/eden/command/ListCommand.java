package eden.command;

import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
