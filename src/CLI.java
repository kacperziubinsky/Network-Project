import java.io.*;
import java.net.*;

public class CLI {

    public static void main(String[] args) {
        // Tworzymy obiekt do odczytu danych z konsoli
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        // Informacja o dostępnych akcjach
        System.out.println("Login CLI - Please enter your login details:");

        try {
            // Wprowadzenie nazwy użytkownika
            System.out.print("Enter username: ");
            String username = userInput.readLine().trim();

            // Wprowadzenie hasła
            System.out.print("Enter password: ");
            String password = userInput.readLine().trim();

            // Łączymy się z serwerem logowania
            try (Socket clientSocket = new Socket("localhost", 2139);
                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                // Wysyłamy zapytanie logowania
                serverOutput.writeBytes("login " + "1" + " " + username + " " + password + "\n");

                // Odczytujemy odpowiedź od serwera
                String response = serverInput.readLine();
                if (response == null) {
                    System.out.println("Server did not respond.");
                    return;
                }

                // Sprawdzamy odpowiedź serwera
                if (response.contains("200")) {
                    System.out.println("Login successful! Welcome, " + username);
                } else if (response.contains("400")) {
                    System.out.println("Login failed: Invalid username or password.");
                }
            } catch (IOException e) {
                System.out.println("Error communicating with the server: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error reading input: " + e.getMessage());
        }
    }
}
