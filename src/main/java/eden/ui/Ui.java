package eden.ui;

import java.util.List;
import java.util.Scanner;

import eden.task.Task;

/**
 * Handles console input and output for Eden.
 */
public class Ui {
    private static final String BANNER = " _____    _            \n"
            + "| ____|__| | ___ _ __  \n"
            + "|  _| / _` |/ _ \\ '_ \\ \n"
            + "| |__| (_| |  __/ | | |\n"
            + "|_____|\\__,_|\\___|_| |_|\n";
    private static final String DIVIDER =
            "____________________________________________________________\n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the command exactly as entered.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Eden's banner and greeting.
     */
    public void showWelcome() {
        String greeting = "Hello! I'm Eden.\n"
                + "What can I do for you?\n";
        System.out.print(DIVIDER);
        System.out.print(BANNER);
        System.out.print(greeting);
        System.out.print(DIVIDER);
    }

    /**
     * Displays Eden's farewell message.
     */
    public void showGoodbye() {
        System.out.print(DIVIDER);
        System.out.print("Bye. Hope to see you again soon!\n");
        System.out.print(DIVIDER);
    }

    /**
     * Displays all tasks using one-based numbering.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.print(DIVIDER);
        System.out.print("Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.print(DIVIDER);
    }

    /**
     * Confirms that a task was marked as completed.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.print(DIVIDER);
        System.out.print("Nice! I've marked this task as done:\n");
        System.out.print("  " + task + "\n");
        System.out.print(DIVIDER);
    }

    /**
     * Confirms that a task was marked as not completed.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.print(DIVIDER);
        System.out.print("OK, I've marked this task as not done yet:\n");
        System.out.print("  " + task + "\n");
        System.out.print(DIVIDER);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.print(DIVIDER);
        System.out.print("Got it. I've added this task:\n");
        System.out.print("  " + task + "\n");
        System.out.print("Now you have " + taskCount + " in the list.\n");
        System.out.print(DIVIDER);
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.print(DIVIDER);
        System.out.print("Noted. I've removed this task:\n");
        System.out.print("  " + task + "\n");
        System.out.print("Now you have " + taskCount + " in the list.\n");
        System.out.print(DIVIDER);
    }

    /**
     * Displays an error that Eden can explain to the user.
     *
     * @param message explanation of the error.
     */
    public void showError(String message) {
        System.out.print(DIVIDER);
        System.out.println(message);
        System.out.print(DIVIDER);
    }
}
