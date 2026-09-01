package eden.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eden.exception.EdenException;
import eden.task.Deadline;
import eden.task.Event;
import eden.task.Task;
import eden.task.Todo;

/**
 * Tests loading and saving tasks without touching Eden's real data file.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveAndLoad_allTaskTypesWithEscapedText_restoresTasks()
            throws EdenException, IOException {
        Path dataFile = this.tempDirectory.resolve("nested folder").resolve("eden.txt");
        Storage storage = new Storage(dataFile);

        Todo todo = new Todo("read | book \\ notes");
        todo.mark();
        Deadline deadline = new Deadline("return book", "Sunday | 5pm");
        Event event = new Event("project \\ meeting", "2pm", "4pm | online");
        event.mark();

        List<String> expectedLines = List.of(
                "T | 1 | read \\| book \\\\ notes",
                "D | 0 | return book | Sunday \\| 5pm",
                "E | 1 | project \\\\ meeting | 2pm | 4pm \\| online");

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertAll(
                () -> assertEquals(expectedLines,
                        Files.readAllLines(dataFile, StandardCharsets.UTF_8)),
                () -> assertEquals(3, loadedTasks.size()),
                () -> assertInstanceOf(Todo.class, loadedTasks.get(0)),
                () -> assertInstanceOf(Deadline.class, loadedTasks.get(1)),
                () -> assertInstanceOf(Event.class, loadedTasks.get(2)),
                () -> assertEquals(expectedLines,
                        loadedTasks.stream().map(Task::toDataString).toList()),
                () -> assertEquals(List.of(
                        "read | book \\ notes", "return book", "project \\ meeting"),
                        loadedTasks.stream().map(Task::getDescription).toList()),
                () -> assertTrue(loadedTasks.get(0).isMarked()),
                () -> assertFalse(loadedTasks.get(1).isMarked()),
                () -> assertTrue(loadedTasks.get(2).isMarked()));
    }

    @Test
    public void load_missingFile_returnsEmptyWithoutCreatingFile() throws EdenException {
        Path dataFile = this.tempDirectory.resolve("missing folder").resolve("eden.txt");
        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertAll(
                () -> assertTrue(loadedTasks.isEmpty()),
                () -> assertFalse(Files.exists(dataFile)),
                () -> assertFalse(Files.exists(dataFile.getParent())));
    }

    @Test
    public void save_existingData_replacesOldContents() throws EdenException, IOException {
        Path dataFile = this.tempDirectory.resolve("eden.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(
                new Todo("obsolete task"),
                new Event("obsolete event", "2pm", "3pm")));

        Deadline replacement = new Deadline("submit report", "Friday");
        replacement.mark();
        storage.save(List.of(replacement));

        List<Task> loadedTasks = storage.load();
        assertAll(
                () -> assertEquals(List.of("D | 1 | submit report | Friday"),
                        Files.readAllLines(dataFile, StandardCharsets.UTF_8)),
                () -> assertEquals(1, loadedTasks.size()),
                () -> assertInstanceOf(Deadline.class, loadedTasks.get(0)),
                () -> assertEquals("D | 1 | submit report | Friday",
                        loadedTasks.get(0).toDataString()),
                () -> assertTrue(loadedTasks.get(0).isMarked()));
    }

    @Test
    public void load_malformedData_throwsLineNumberedError() {
        assertAll(
                () -> assertInvalidData("unknown-type.txt",
                        List.of("X | 0 | task"), 1),
                () -> assertInvalidData("invalid-status.txt",
                        List.of("T | 0 | valid", "", "T | 2 | invalid"), 3),
                () -> assertInvalidData("missing-field.txt",
                        List.of("D | 0 | return book"), 1),
                () -> assertInvalidData("extra-field.txt",
                        List.of("T | 0 | task | extra"), 1),
                () -> assertInvalidData("blank-description.txt",
                        List.of("T | 0 |   "), 1),
                () -> assertInvalidData("invalid-escape.txt",
                        List.of("T | 0 | bad\\q"), 1),
                () -> assertInvalidData("dangling-escape.txt",
                        List.of("T | 0 | bad\\"), 1));
    }

    @Test
    public void save_unusableParentPath_wrapsFileSystemError() throws IOException {
        Path blockingFile = this.tempDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "blocking file", StandardCharsets.UTF_8);
        Path dataFile = blockingFile.resolve("eden.txt");
        Storage storage = new Storage(dataFile);

        EdenException exception = assertThrows(EdenException.class,
                () -> storage.save(List.of(new Todo("read book"))));

        assertAll(
                () -> assertEquals("OOPS!!! I couldn't save the task data to "
                        + dataFile + ".", exception.getMessage()),
                () -> assertInstanceOf(IOException.class, exception.getCause()));
    }

    /**
     * Writes malformed task data and checks the public loading error contract.
     */
    private void assertInvalidData(String fileName, List<String> lines, int expectedLine)
            throws IOException {
        Path dataFile = this.tempDirectory.resolve(fileName);
        Files.write(dataFile, lines, StandardCharsets.UTF_8);

        EdenException exception = assertThrows(EdenException.class,
                () -> new Storage(dataFile).load());

        assertEquals("OOPS!!! The task data in " + dataFile
                + " is invalid at line " + expectedLine
                + ". Expected fields separated by ' | '.", exception.getMessage());
    }
}
