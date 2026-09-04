package eden.command;

import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the current task list.
     */
    public ListCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.formatTaskList(tasks.asList());
    }
}
