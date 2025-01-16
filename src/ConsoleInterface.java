import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ConsoleInterface {
    private static final String API_GATEWAY_ADDRESS = "localhost";
    private static final int API_GATEWAY_PORT = 8080;
    private final Scanner scanner;

    public ConsoleInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    handlePosts();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Service Mesh Console ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Posts");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        String response = sendRequest("login " + username + " " + password);
        System.out.println("\nResponse: " + response);
    }

    private void handleRegister() {
        System.out.print("Enter desired username: ");
        String username = scanner.nextLine();
        System.out.print("Enter desired password: ");
        String password = scanner.nextLine();

        String response = sendRequest("register " + username + " " + password);
        System.out.println("\nResponse: " + response);
    }

    private void handlePosts() {
        while (true) {
            System.out.println("\n=== Posts Menu ===");
            System.out.println("1. View all posts");
            System.out.println("2. Create new post");
            System.out.println("3. Back to main menu");
            System.out.print("Enter your choice (1-3): ");

            int choice = getUserChoice();
            
            switch (choice) {
                case 1:
                    String viewResponse = sendRequest("post VIEW_POSTS");
                    System.out.println("\nPosts:");
                    System.out.println(viewResponse);
                    break;
                case 2:
                    System.out.print("Enter your username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter your post content: ");
                    String content = scanner.nextLine();
                    String createResponse = sendRequest("post " + username + ":" + content);
                    System.out.println("\nResponse: " + createResponse);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private String sendRequest(String request) {
        try (Socket socket = new Socket(API_GATEWAY_ADDRESS, API_GATEWAY_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            return "Error: Could not connect to service. Make sure the API Gateway is running.";
        }
    }
}
