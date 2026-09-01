package eden.command;

import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Ends the current Eden session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current Eden session.
     */
    public ExitCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
