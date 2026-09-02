package eden.command;

import eden.exception.EdenException;
import eden.storage.Storage;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword text to find within task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EdenException {
        if (keyword.isEmpty()) {
            throw new EdenException("OOPS!!! The keyword for find cannot be empty.");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
