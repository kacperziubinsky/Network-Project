import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3003;
    private final Scanner scanner;
    private String loggedInUser;

    public Client() {
        this.scanner = new Scanner(System.in);
        this.loggedInUser = null;
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
                    displayStatus();
                    break;
                case 5:
                    System.out.println("Kończenie programu...");
                    return;
                default:
                    System.out.println("Zły wybór. Proszę spróbować ponownie.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Projekt Service Mesh Console ===");
        System.out.println("1. Logowanie");
        System.out.println("2. Rejestracja");
        System.out.println("3. Posty");
        System.out.println("4. Status");
        System.out.println("5. Wyjście");
        System.out.print("Wybierz opcje (1-4): ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleLogin() {
        System.out.print("Podaj login: ");
        String username = scanner.nextLine();
        System.out.print("Podaj hasło: ");
        String password = scanner.nextLine();

        String response = sendRequest("login " + username + " " + password);
        if (response.equalsIgnoreCase("Login successful")) {
            loggedInUser = username;
            System.out.println("\nOdpowiedź: " + response);
        } else {
            System.out.println("\nNieudane logowanie: " + response);
        }
    }

    private void handleRegister() {
        if (isUserLoggedIn()) {
            System.out.println("\nBłąd: Jesteś już zalogowany. Wyloguj się, aby móc się zarejestrować.");
            return;
        }

        System.out.print("Podaj login do rejestracji: ");
        String username = scanner.nextLine();
        System.out.print("Podaj hasło do rejestracji: ");
        String password = scanner.nextLine();

        String response = sendRequest("register " + username + " " + password);
        System.out.println("\nOdpowiedź: " + response);
    }

    private void handlePosts() {
        if (!isUserLoggedIn()) {
            System.out.println("\nMusisz być zalogowany, aby wybrać inne opcje.");
            return;
        }

        while (true) {
            System.out.println("\n=== Menu Postów ===");
            System.out.println("1. Zobacz wszystkie posty");
            System.out.println("2. Stwórz nowy post");
            System.out.println("3. Powrót do menu");
            System.out.print("Wybierz opcje (1-3): ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    String viewResponse = sendRequest("post view_posts");
                    displayPosts(viewResponse);
                    break;
                case 2:
                    System.out.print("Napisz post: ");
                    String content = scanner.nextLine();
                    try {
                        content = URLEncoder.encode(content.toString(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    String createResponse = sendRequest("post add_post " + loggedInUser + " " + content);
                    System.out.println("\nResponse: " + createResponse);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Zły wybór. Proszę spróbować ponownie.");
            }
        }
    }

    private void handleFiles() {
        if (!isUserLoggedIn()) {
            System.out.println("\nMusisz być zalogowany, aby wybrać inne opcje.");
            return;
        }

        while (true) {
            System.out.println("\n=== Menu Plików ===");
            System.out.println("1. Wyślij plik na serwer");
            System.out.println("2. Pobierz plik z serwera");
            System.out.println("3. Powrót do menu");
            System.out.print("Wybierz opcje (1-3): ");

            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    System.out.print("Podaj nazwę pliku do wysłania: ");
                    String fileToSend = scanner.nextLine();
                    sendFileToServer(loggedInUser, fileToSend);
                    break;
                case 2:
                    System.out.print("Podaj nazwę pliku do pobrania: ");
                    String fileToReceive = scanner.nextLine();
                    receiveFileFromServer(loggedInUser, fileToReceive);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Zły wybór. Proszę spróbować ponownie.");
            }
        }
    }

    private void sendFileToServer(String username, String filename) {
        try {
            String response = sendRequest("send " + username + " " + filename);
            System.out.println("\nSerwer odpowiedział: " + response);
        } catch (Exception e) {
            System.out.println("Błąd podczas wysyłania pliku: " + e.getMessage());
        }
    }

    private void receiveFileFromServer(String username, String filename) {
        try {
            String response = sendRequest("rec " + username + " " + filename);
            System.out.println("\nSerwer odpowiedział: " + response);
        } catch (Exception e) {
            System.out.println("Błąd podczas pobierania pliku: " + e.getMessage());
        }
    }

    private void displayStatus() {
        System.out.println("\n=== Status Użytkownika ===");
        if (isUserLoggedIn()) {
            System.out.println("Nazwa użytkownika: " + loggedInUser);
            System.out.println("Status: Zalogowany");
        } else {
            System.out.println("Nie jesteś zalogowany.");
        }
    }

    private boolean isUserLoggedIn() {
        return loggedInUser != null;
    }

    private void displayPosts(String postsResponse) {
        if (postsResponse == null || postsResponse.isEmpty()) {
            System.out.println("\nBrak postów do wyświetlania.");
            return;
        }
        try {
            postsResponse = URLDecoder.decode(postsResponse, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Błąd dekodowania odpowiedzi od serwera: " + e.getMessage());
            return;
        }

        String[] posts = postsResponse.split("## ");
        System.out.println("\nPosty:");
        for (String post : posts) {
            if (!post.trim().isEmpty()) {
                System.out.println("- " + post.trim());
            }
        }
    }


    private String sendRequest(String request) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            return "Error: Brak połączenia z serwisem. Upewnij się, że serwer jest włączony.";
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
