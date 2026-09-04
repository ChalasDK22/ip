package eden;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the request-response boundary shared by Eden's console and graphical interfaces.
 */
public class EdenTest {
    @TempDir
    private Path tempDirectory;

    /**
     * Verifies that commands return undecorated responses and update task state.
     */
    @Test
    public void getResponse_taskCommands_returnsResponsesAndUpdatesState() {
        Eden eden = new Eden(tempDirectory.resolve("data").resolve("eden.txt"));

        assertEquals("Hello! I'm Eden.\nWhat can I do for you?", eden.getWelcomeMessage());
        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 in the list.", eden.getResponse("todo read book"));
        assertEquals("Got it. I've added this task:\n"
                + "  [D][ ] return book (by: Dec 02 2019)\n"
                + "Now you have 2 in the list.",
                eden.getResponse("deadline return book /by 2019-12-02"));
        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] project meeting (from: 2pm to: 4pm)\n"
                + "Now you have 3 in the list.",
                eden.getResponse("event project meeting /from 2pm /to 4pm"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 02 2019)\n"
                + "3.[E][ ] project meeting (from: 2pm to: 4pm)", eden.getResponse("list"));
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Dec 02 2019)", eden.getResponse("find book"));
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                eden.getResponse("mark 1"));
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book",
                eden.getResponse("unmark 1"));
        assertEquals("Noted. I've removed this task:\n"
                + "  [D][ ] return book (by: Dec 02 2019)\n"
                + "Now you have 2 in the list.", eden.getResponse("delete 2"));
    }

    /**
     * Verifies that graphical-interface responses retain user-friendly validation.
     */
    @Test
    public void getResponse_invalidCommands_returnsErrorsWithoutThrowing() {
        Eden eden = new Eden(tempDirectory.resolve("eden.txt"));

        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(",
                eden.getResponse("something else"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.",
                eden.getResponse("todo"));
        assertEquals("OOPS!!! Please enter the deadline date as yyyy-MM-dd "
                + "(e.g., 2019-12-02).", eden.getResponse("deadline task /by tomorrow"));
        assertEquals("OOPS!!! Please enter an event as: "
                + "event DESCRIPTION /from START /to END.",
                eden.getResponse("event meeting /from 2pm"));
        assertEquals("OOPS!!! Please enter a valid task number.", eden.getResponse("mark nope"));
        assertEquals("OOPS!!! Please enter a valid task number.", eden.getResponse("delete 1"));
    }

    /**
     * Verifies that one instance's changes can be loaded by a new GUI backend instance.
     */
    @Test
    public void getResponse_addedTask_persistsForNewInstance() {
        Path dataFile = tempDirectory.resolve("nested").resolve("eden.txt");
        Eden firstInstance = new Eden(dataFile);
        firstInstance.getResponse("todo persisted task");

        Eden secondInstance = new Eden(dataFile);

        assertEquals("Here are the tasks in your list:\n1.[T][ ] persisted task",
                secondInstance.getResponse("list"));
    }

    /**
     * Verifies that bye returns its message and exposes the controller's close signal.
     */
    @Test
    public void getResponse_bye_setsExitFlag() {
        Eden eden = new Eden(tempDirectory.resolve("eden.txt"));

        assertFalse(eden.isExit());
        assertEquals("Bye. Hope to see you again soon!", eden.getResponse("bye"));
        assertTrue(eden.isExit());
    }

    /**
     * Verifies that malformed stored data is reported through the startup API.
     */
    @Test
    public void getWelcomeMessage_malformedStorage_returnsLoadingError() throws IOException {
        Path dataFile = tempDirectory.resolve("eden.txt");
        Files.writeString(dataFile, "X | 0 | invalid", StandardCharsets.UTF_8);

        Eden eden = new Eden(dataFile);
        String expectedError = "OOPS!!! The task data in " + dataFile
                + " is invalid at line 1. Expected fields separated by ' | '.";

        assertEquals(expectedError, eden.getWelcomeMessage());
        assertEquals(expectedError, eden.getResponse("list"));
    }
}
