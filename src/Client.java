import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final Scanner scanner = new Scanner(System.in);
    private static String currentUsername = null;

    public static void main(String[] args) {
        while (true) {
            displayMenu();
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Social Media Client ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Post Message");
        System.out.println("4. Upload File");
        System.out.println("5. View Posts");
        System.out.println("6. Logout");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                if (currentUsername != null) {
                    postMessage();
                } else {
                    System.out.println("Please login first.");
                }
                break;
            case 4:
                if (currentUsername != null) {
                    uploadFile();
                } else {
                    System.out.println("Please login first.");
                }
                break;
            case 5:
                if (currentUsername != null) {
                    viewPosts();
                } else {
                    System.out.println("Please login first.");
                }
                break;
            case 6:
                logout();
                break;
            case 7:
                System.exit(0);
            default:
                System.out.println("Invalid option");
        }
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 2200);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(username + ":" + password);
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 2300);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(username + ":" + password);
            String response = in.readLine();
            if (response.equals("LOGIN_SUCCESS")) {
                currentUsername = username;
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed.");
            }
        } catch (IOException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void logout() {
        if (currentUsername != null) {
            System.out.println("Logged out of account: " + currentUsername);
            currentUsername = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    private static void postMessage() {
        System.out.print("Enter your message: ");
        String message = scanner.nextLine();

        try (Socket socket = new Socket("localhost", 2400);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(currentUsername + ":" + message);
            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.out.println("Posting message failed: " + e.getMessage());
        }
    }

    private static void viewPosts() {
        try (Socket socket = new Socket("localhost", 2400);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("VIEW_POSTS");
            String post;
            System.out.println("--- Posts ---");
            while ((post = in.readLine()) != null && !post.equals("END_OF_POSTS")) {
                System.out.println(post);
            }
        } catch (IOException e) {
            System.out.println("Retrieving posts failed: " + e.getMessage());
        }
    }

    private static void uploadFile() {
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        try (Socket socket = new Socket("localhost", 2137);
             BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
             FileInputStream fileInput = new FileInputStream(file)) {

            String filename = file.getName();
            String metadata = currentUsername + " " + filename + "\n\n";

            out.write(metadata.getBytes());

            byte[] data = new byte[512];
            int k;
            while ((k = fileInput.read(data)) != -1) {
                out.write(data, 0, k);
            }
            out.flush();
            System.out.println("File uploaded successfully");
        } catch (IOException e) {
            System.out.println("File upload failed: " + e.getMessage());
        }
    }
}