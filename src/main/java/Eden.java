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

            System.out.print(line);
            System.out.print(command + " huh?\n");
            System.out.print(line);
        }
    }
}
