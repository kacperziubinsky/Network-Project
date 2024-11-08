import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Client {
    private static String loggedInUser = null;
    private static String currentUsername = null;

    public static void main(String args[]) throws Exception {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 2137);
        DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String command;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            while (true) {
                // Wyświetl menu
                System.out.println("Wybierz:");
                System.out.println("1. Zarejestruj");
                System.out.println("2. Zaloguj");
                System.out.println("3. Wyloguj");
                System.out.println("4. Status");
                System.out.println("5. Dodaj post");
                System.out.println("6. Wyświetl posty");
                System.out.println("Type 'exit' to quit.");

                command = userInput.readLine();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client...");
                    break;
                }

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
                } else if (command.equals("2")) {
                    System.out.println("Enter username and password (format: <username> <password>):");
                    String[] parts = userInput.readLine().split(" ");
                    if (parts.length != 2) {
                        System.out.println("Invalid login format. Please enter: <username> <password>");
                        continue;
                    }
                    String username = parts[0];
                    String password = parts[1];
                    currentUsername = username;
                    serverOutput.writeBytes("login " + username + " " + password + '\n');
                } else if (command.equals("3")) {
                    if (loggedInUser != null) {
                        serverOutput.writeBytes("logout " + loggedInUser + '\n');
                    } else {
                        System.out.println("You are not logged in.");
                        continue;
                    }
                } else if (command.equals("4")) {
                    if (loggedInUser != null) {
                        System.out.println("You are logged in as: " + loggedInUser);
                    } else {
                        System.out.println("You are not logged in.");
                    }
                    continue;
                } else if (command.equals("5")) {
                    if (loggedInUser != null) {
                        System.out.println("Enter post content:");
                        String postContent = userInput.readLine();
                        serverOutput.writeBytes("add_post " + postContent + '\n');
                    } else {
                        System.out.println("You need to be logged in to add a post.");
                    }
                    continue;
                } else if (command.equals("6")) {
                    if (loggedInUser != null) {
                        serverOutput.writeBytes("view_posts" + '\n');

                        // Pobieranie i wyświetlanie postów
                        String response;
                        while (!(response = serverInput.readLine()).equals("END")) {
                            System.out.println("Post: " + response);
                        }
                    } else {
                        System.out.println("You need to be logged in to view posts.");
                    }
                    continue;
                }

                Future<String> futureResponse = executor.submit(() -> {
                    try {
                        return serverInput.readLine();
                    } catch (IOException e) {
                        return null;
                    }
                });

                try {
                    String response = futureResponse.get(10, TimeUnit.SECONDS);

                    if (response == null) {
                        System.out.println("Server did not respond in time.");
                        continue;
                    }

                    System.out.println("Server Response: " + response);

                    if (command.equals("2")) {
                        if (response.equals("Login successful!")) {
                            loggedInUser = currentUsername;
                            System.out.println("You are now logged in as: " + loggedInUser);
                        } else {
                            loggedInUser = null;
                        }
                    }

                    if (command.equals("3")) {
                        if (response.equals("Logout successful!")) {
                            loggedInUser = null;
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
