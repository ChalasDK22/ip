package eden.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns and manages Eden's tasks using the one-based task numbers shown to users.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the loaded tasks.
     *
     * @param tasks tasks with which to initialize the list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Deletes the task with the given one-based task number.
     *
     * @param taskNumber number shown to the user
     * @return the deleted task
     * @throws IndexOutOfBoundsException if the task number is outside the list
     */
    public Task delete(int taskNumber) {
        return this.tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks the task with the given one-based task number as completed.
     *
     * @param taskNumber number shown to the user
     * @return the marked task
     * @throws IndexOutOfBoundsException if the task number is outside the list
     */
    public Task mark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.mark();
        return task;
    }

    /**
     * Marks the task with the given one-based task number as not completed.
     *
     * @param taskNumber number shown to the user
     * @return the unmarked task
     * @throws IndexOutOfBoundsException if the task number is outside the list
     */
    public Task unmark(int taskNumber) {
        Task task = getTask(taskNumber);
        task.unmark();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns a structurally read-only view for displaying or saving the tasks.
     *
     * @return unmodifiable view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Gets the task with the given one-based task number.
     */
    private Task getTask(int taskNumber) {
        return this.tasks.get(toIndex(taskNumber));
    }

    /**
     * Converts a one-based user-facing task number to a zero-based list index.
     */
    private int toIndex(int taskNumber) {
        return taskNumber - 1;
    }
}
