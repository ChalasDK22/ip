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

        List<String> commands = new ArrayList<>();
        
        String greetings = "Hello! I'm Eden.\n"
                + "What can I do for you?\n";
        String bye = "Bye. Hope to see you again soon!\n";
        System.out.print(line);
        System.out.print(banner);
        System.out.print(greetings);
        System.out.print(line);

        while (true) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.print(line);
                System.out.print(bye);
                System.out.print(line);
                break;
            }

            else if (command.equals("list")) {
                System.out.print(line);
                for (int i = 0; i < commands.size(); i++) {
                    System.out.println((i + 1) + ". " + commands.get(i));
                }
                System.out.print(line);
            }
            else {
                commands.add(command);
                System.out.print(line);
                System.out.print("added: " + command + " \n");
                System.out.print(line);
            }
        }
    }
}
