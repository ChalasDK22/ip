package eden.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests TaskList's one-based task operations and collection encapsulation.
 */
public class TaskListTest {
    /**
     * Verifies that one-based mark and unmark operations affect only the selected task.
     */
    @Test
    public void markAndUnmark_secondTask_changesOnlyRequestedTask() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Deadline("second task", "Friday");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task markedTask = tasks.mark(2);

        assertAll(
                () -> assertSame(secondTask, markedTask),
                () -> assertFalse(firstTask.isMarked()),
                () -> assertTrue(secondTask.isMarked()));

        Task unmarkedTask = tasks.unmark(2);

        assertAll(
                () -> assertSame(secondTask, unmarkedTask),
                () -> assertFalse(firstTask.isMarked()),
                () -> assertFalse(secondTask.isMarked()));
    }

    /**
     * Verifies that deletion returns the selected task and preserves remaining order.
     */
    @Test
    public void delete_secondTask_removesItAndPreservesOrder() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        Task thirdTask = new Todo("third task");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = tasks.delete(2);

        assertAll(
                () -> assertSame(secondTask, deletedTask),
                () -> assertEquals(2, tasks.size()),
                () -> assertIterableEquals(List.of(firstTask, thirdTask), tasks.asList()));
    }

    /**
     * Verifies that invalid task numbers fail without mutating the list.
     */
    @Test
    public void invalidTaskNumbers_throwWithoutChangingTasks() {
        Task onlyTask = new Todo("only task");
        TaskList tasks = new TaskList(List.of(onlyTask));

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(0)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(2)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(0)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(2)));
        assertAll(
                () -> assertEquals(1, tasks.size()),
                () -> assertFalse(onlyTask.isMarked()));
    }

    /**
     * Verifies that construction and read-only views do not expose structural mutation.
     */
    @Test
    public void constructorAndAsList_doNotExposeMutableStructure() {
        Task firstTask = new Todo("first task");
        List<Task> source = new ArrayList<>();
        source.add(firstTask);
        TaskList tasks = new TaskList(source);

        source.clear();
        Task secondTask = new Todo("second task");
        tasks.add(secondTask);

        assertAll(
                () -> assertEquals(2, tasks.size()),
                () -> assertIterableEquals(List.of(firstTask, secondTask), tasks.asList()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> tasks.asList().add(new Todo("leaked task"))));
        assertEquals(2, tasks.size());
    }
}
