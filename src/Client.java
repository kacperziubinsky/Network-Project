import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client {
    private static String loggedInUser = null; // Zmienna do przechowywania zalogowanego użytkownika
    private static String currentUsername = null; // Zmienna do przechowywania aktualnego użytkownika podczas logowania

    public static void main(String args[]) throws Exception {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 2137);  // Połączenie z serwerem na localhost
        DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String command;

        // Utwórz ExecutorService, który pozwala ustawić timeout na odczyt odpowiedzi z serwera
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            while (true) {
                // Wyświetl menu
                System.out.println("Wybierz:");
                System.out.println("1. Zarejestruj");
                System.out.println("2. Zaloguj");
                System.out.println("3. Wyloguj");
                System.out.println("4. Wyślij plik");
                System.out.println("5. Pobierz plik");
                System.out.println("6. Status");
                System.out.println("Type 'exit' to quit.");

                // Odczytaj komendę od użytkownika
                command = userInput.readLine();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client...");
                    break;
                }

                // Obsługuje komendę rejestracji
                if (command.equals("1")) {
                    System.out.println("Enter username and password (format: <username> <password>):");
                    String[] parts = userInput.readLine().split(" ");
                    if (parts.length != 2) {
                        System.out.println("Invalid registration format. Please enter: <username> <password>");
                        continue;
                    }
                    String username = parts[0];
                    String password = parts[1];
                    serverOutput.writeBytes("register " + username + " " + password + '\n');
                }
                // Obsługuje komendę logowania
                else if (command.equals("2")) {
                    System.out.println("Enter username and password (format: <username> <password>):");
                    String[] parts = userInput.readLine().split(" ");
                    if (parts.length != 2) {
                        System.out.println("Invalid login format. Please enter: <username> <password>");
                        continue;
                    }
                    String username = parts[0];
                    String password = parts[1];
                    currentUsername = username; // Store the username for later use
                    serverOutput.writeBytes("login " + username + " " + password + '\n');
                }
                // Obsługuje komendę wylogowania
                else if (command.equals("3")) {
                    if (loggedInUser != null) {
                        serverOutput.writeBytes("logout " + loggedInUser + '\n');
                    } else {
                        System.out.println("You are not logged in.");
                        continue;
                    }
                }
                else if (command.equals("4")) {
                    if (loggedInUser != null) {
                        System.out.println("Wybierz plik do wysłania -> pełny format np. 'plik.txt' ");
                        FileTransfer.getALLFiles(new File("src/ClientData"));
                        String[] parts = userInput.readLine().split(" ");
                        if (parts.length != 1) {
                            System.out.println("Invalid plik format. Please enter: <plik.txt>");
                            continue;
                        }
                        else
                        {
                            serverOutput.writeBytes("send " + parts[0] + '\n');
                            System.out.println("Plik w trakcie przesyłania.");
                        }
                    }
                    else {
                        System.out.println("You are not logged in.");
                        continue;
                    }
                }
                else if (command.equals("5")) {
                    if (loggedInUser != null) {
                        System.out.println("Wybierz plik do pobrania -> pełny format np. 'plik.txt' ");
                        FileTransfer.getALLFiles(new File("src/Data"));
                        String[] parts = userInput.readLine().split(" ");
                        if (parts.length != 1) {
                            System.out.println("Invalid plik format. Please enter: <plik.txt>");
                            continue;
                        }
                        else
                        {
                            serverOutput.writeBytes("recieve" + parts[0] + '\n');
                            System.out.println("Plik w trakcie pobierania.");
                        }
                    }
                    else {
                        System.out.println("You are not logged in.");
                        continue;
                    }
                }
                // Obsługuje komendę statusu
                else if (command.equals("6")) {
                    if (loggedInUser != null) {
                        System.out.println("You are logged in as: " + loggedInUser);
                    } else {
                        System.out.println("You are not logged in.");
                    }
                    continue;
                }


                // Użycie executor do odbierania odpowiedzi z serwera z timeoutem
                Future<String> futureResponse = executor.submit(() -> {
                    try {
                        return serverInput.readLine(); // Czekaj na odpowiedź od serwera
                    } catch (IOException e) {
                        return null;
                    }
                });

                try {
                    // Oczekiwanie na odpowiedź z serwera z maksymalnym czasem oczekiwania
                    String response = futureResponse.get(10, TimeUnit.SECONDS); // Zwiększamy timeout do 10 sekund

                    if (response == null) {
                        System.out.println("Server did not respond in time.");
                        continue;
                    }

                    System.out.println("Server Response: " + response);

                    // Jeśli użytkownik się zalogował, zapisujemy jego nazwę
                    if (command.equals("2")) {  // Logowanie
                        if (response.equals("Login successful!")) {
                            loggedInUser = currentUsername;  // Przechowujemy nazwę użytkownika
                            System.out.println("You are now logged in as: " + loggedInUser);
                        } else {
                            loggedInUser = null;  // Jeśli logowanie nieudane, resetujemy zmienną
                        }
                    }

                    // Jeśli użytkownik się wylogował, resetujemy zmienną loggedInUser
                    if (command.equals("3")) {  // Wylogowanie
                        if (response.equals("Logout successful!")) {
                            loggedInUser = null;  // Resetujemy zalogowanego użytkownika
                            System.out.println("You have been logged out.");
                        } else {
                            System.out.println("Logout failed: " + response);
                        }
                    }
                } catch (TimeoutException e) {
                    System.out.println("Server response timed out.");
                } catch (Exception e) {
                    System.out.println("Error while receiving response: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error during communication with the server: " + e.getMessage());
        } finally {
            // Zamknięcie połączenia z serwerem
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("Connection closed.");
                }
            } catch (IOException e) {
                System.out.println("Error while closing the socket: " + e.getMessage());
            }
        }
    }
}
