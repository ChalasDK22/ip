package eden.command;

import eden.exception.EdenException;
import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Represents an action that Eden can execute.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Executes this command using Eden's application collaborators.
     *
     * @param tasks task list on which the command operates.
     * @param ui user interface used to display the result.
     * @param storage storage used to persist task changes.
     * @throws EdenException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EdenException;

    /**
     * Returns whether Eden should stop after executing this command.
     *
     * @return true only for a command that exits Eden.
     */
    public boolean isExit() {
        return false;
    }
}
