package eden.command;

import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Ends the current Eden session.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
