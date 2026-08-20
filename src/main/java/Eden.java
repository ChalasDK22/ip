import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Greets the user as Eden and then displays a farewell message.
 */
public class Eden {
    public static void main(String[] args) {
        String banner = " _____    _            \n"
                + "| ____|__| | ___ _ __  \n"
                + "|  _| / _` |/ _ \\ '_ \\ \n"
                + "| |__| (_| |  __/ | | |\n"
                + "|_____|\\__,_|\\___|_| |_|\n";
        String line = "____________________________________________________________\n";

        Scanner scanner = new Scanner(System.in);

        List<Task> tasks = new ArrayList<>();

        String greetings = "Hello! I'm Eden.\n"
                + "What can I do for you?\n";
        String bye = "Bye. Hope to see you again soon!\n";
        System.out.print(line);
        System.out.print(banner);
        System.out.print(greetings);
        System.out.print(line);

        while (true) {
            String command = scanner.nextLine();

            try {
                if (command.equals("bye")) {
                    System.out.print(line);
                    System.out.print(bye);
                    System.out.print(line);
                    break;
                } else if (command.equals("list")) {
                    System.out.print(line);
                    System.out.print("Here are the tasks in your list:\n");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                    System.out.print(line);
                } else if (command.startsWith("mark ")) {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    Task task = tasks.get(taskNumber - 1);
                    task.mark();
                    System.out.print(line);
                    System.out.print("Nice! I've marked this task as done:\n");
                    System.out.print("  " + task + "\n");
                    System.out.print(line);
                } else if (command.startsWith("unmark ")) {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    Task task = tasks.get(taskNumber - 1);
                    task.unmark();
                    System.out.print(line);
                    System.out.print("OK, I've marked this task as not done yet:\n");
                    System.out.print("  " + task + "\n");
                    System.out.print(line);
                } else if (command.startsWith("todo")) {
                    String description = command.substring("todo".length()).trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a todo cannot be empty.");
                    }
                    Task task = new Todo(command.substring("todo".length()).trim());
                    tasks.add(task);
                    System.out.print(line);
                    System.out.print("Got it. I've added this task:\n");
                    System.out.print("  " + task + "\n");
                    System.out.print("Now you have " + tasks.size() + " in the list.\n");
                    System.out.print(line);
                } else if (command.startsWith("deadline")) {
                    String details = command.substring("deadline".length()).trim();
                    String[] parts = details.split("\\s+/by\\s+", 2);
                    String description = parts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of a deadline cannot be empty.");
                    }
                    String by = parts[1].trim();
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    System.out.print(line);
                    System.out.print("Got it. I've added this task:\n");
                    System.out.print("  " + task + "\n");
                    System.out.print("Now you have " + tasks.size() + " in the list.\n");
                    System.out.print(line);
                } else if (command.startsWith("event ")) {
                    String details = command.substring("event".length()).trim();
                    String[] fromParts = details.split("\\s+/from\\s+", 2);
                    String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
                    String description = fromParts[0].trim();
                    if (description.isEmpty()) {
                        throw new EdenException(
                            "OOPS!!! The description of an event cannot be empty.");
                    }
                    String from = toParts[0].trim();
                    String to = toParts[1].trim();
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    System.out.print(line);
                    System.out.print("Got it. I've added this task:\n");
                    System.out.print("  " + task + "\n");
                    System.out.print("Now you have " + tasks.size() + " in the list.\n");
                    System.out.print(line);
                } else {
                    throw new EdenException(
                            "OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (EdenException exception) {
                System.out.print(line);
                System.out.println(exception.getMessage());
                System.out.print(line);
            }
        }
    }
}
