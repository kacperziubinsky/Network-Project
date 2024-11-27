import java.io.*;
import java.net.*;

public class Client {
    private static String loggedInUser = null;

    public static void main(String args[]) throws Exception {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("localhost", 2137);
        DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String command;


        try {
            while (true) {
                System.out.println("Choose:");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Logout");
                System.out.println("4. Wyślij plik");
                System.out.println("5. Pobierz plik");
                System.out.println("6. Status");
                System.out.println("7. Dodaj post");
                System.out.println("8. Wyświetl posty");
                System.out.println("Type 'exit' to quit.");

                command = userInput.readLine();

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client...");
                    break;
                }

                switch (command) {
                    case "1": {
                        if (loggedInUser == null) {
                            System.out.println("Enter username and password (format: <username> <password>):");
                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 2) {
                                System.out.println("Invalid registration format. Please enter: <username> <password>");
                                continue;
                            }
                            String username = parts[0];
                            String password = parts[1];
                            serverOutput.writeBytes("register " + username + " " + password + '\n');

                            String response;

                            try {
                                response = serverInput.readLine();
                            } catch (IOException e) {
                                System.out.println("Error while reading server response: " + e.getMessage());
                                return;
                            }

                            if (response == null) {
                                System.out.println("Server did not respond in time.");
                                return;
                            }

                            if (response.equals("Username already exists.")) {
                                System.out.println("Username already exists.");
                            } else if (response.equals("Registration successful!")) {
                                System.out.println("Registration successful!");
                            }
                        } else {
                            System.out.println("You are already logged in. Please logout first.");
                        }
                        break;

                    } case "2": {
                        if (loggedInUser == null) {
                            System.out.println("Enter username and password (format: <username> <password>):");

                            String[] parts;
                            try {
                                parts = userInput.readLine().split(" ");
                            } catch (IOException e) {
                                System.out.println("Error reading input: " + e.getMessage());
                                return;
                            }

                            if (parts.length != 2) {
                                System.out.println("Invalid login format. Please enter: <username> <password>");
                                continue;
                            }

                            String username = parts[0];
                            String password = parts[1];

                            try {
                                serverOutput.writeBytes("login " + username + " " + password + '\n');
                            } catch (IOException e) {
                                System.out.println("Error while sending login data: " + e.getMessage());
                                return;
                            }

                            String response;
                            try {
                                response = serverInput.readLine();
                            } catch (IOException e) {
                                System.out.println("Error while reading server response: " + e.getMessage());
                                return;
                            }

                            if (response == null) {
                                System.out.println("Server did not respond in time.");
                                return;
                            }

                            switch (response) {
                                case "Login successful!":
                                    loggedInUser = username;
                                    System.out.println("You are now logged in as: " + loggedInUser);
                                    break;
                                case "Invalid credentials":
                                    System.out.println("Login failed: Invalid username or password.");
                                    break;
                                case "User already logged in":
                                    System.out.println("Login failed: User is already logged in.");
                                    break;
                                default:
                                    System.out.println("Login failed: " + response);
                                    break;
                            }
                        } else {
                            System.out.println("You are already logged in. Please logout first.");
                        }
                        break;
                    } case "3": {
                        if (loggedInUser != null) {
                            serverOutput.writeBytes("logout " + loggedInUser + '\n');
                        } else {
                            System.out.println("You are not logged in.");
                            continue;
                        }

                        String response;
                        try {
                            response = serverInput.readLine();
                        } catch (IOException e) {
                            System.out.println("Error while reading server response: " + e.getMessage());
                            return;
                        }

                        if (response == null) {
                            System.out.println("Server did not respond in time.");
                            return;
                        }

                        if (response.equals("Logout successful!")) {
                            loggedInUser = null;
                            System.out.println("You have been logged out.");
                        } else {
                            System.out.println("Logout failed: " + response);
                        }

                        break;

                    } case "4": {
                        if (loggedInUser != null) {
                            System.out.println("Wybierz plik do wysłania -> pełny format np. 'plik.txt' ");
                            FileTransfer.getALLFiles(new File("src/Clientdata"));
                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 1) {
                                System.out.println("Invalid plik format. Please enter: <plik.txt>");
                                continue;
                            } else {
                                serverOutput.writeBytes("send " + parts[0] + '\n');
                                System.out.println("Plik w trakcie przesyłania.");
                            }
                        } else {
                            System.out.println("You are not logged in.");
                            continue;
                        }
                        break;
                    } case "5": {
                        if (loggedInUser != null) {
                            System.out.println("Wybierz plik do pobrania -> pełny format np. 'plik.txt' ");
                            FileTransfer.getALLFiles(new File("src/Serwisdane"));
                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 1) {
                                System.out.println("Invalid plik format. Please enter: <plik.txt>");
                                continue;
                            } else {
                                serverOutput.writeBytes("rec " + parts[0] + '\n');
                                System.out.println("Plik w trakcie przesyłania.");
                            }
                        } else {
                            System.out.println("You are not logged in.");
                            continue;
                        }
                        break;
                    } case "6": {
                        if (loggedInUser != null) {
                            System.out.println("You are logged in as: " + loggedInUser);
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    } case "7": {
                        if (loggedInUser != null) {
                            System.out.println("Enter post content:");
                            String postContent = userInput.readLine();
                            String username = loggedInUser;
                            String response;
                            try {
                                response = serverInput.readLine();
                            } catch (IOException e) {
                                System.out.println("Error while reading server response: " + e.getMessage());
                                return;
                            }



                            if (response == null) {
                                System.out.println("Server did not respond in time.");
                                return;
                            }
                            serverOutput.writeBytes("add_post " + postContent + " " + username + '\n');
                        } else {
                            System.out.println("You need to be logged in to add a post.");
                        }
                        break;
                    } case "8": {
                        if (loggedInUser != null) {

                            serverOutput.writeBytes("view_posts" + '\n');

                            String response;
                            while (!(response = serverInput.readLine()).equals("END")) {
                                System.out.println("Post: " + response);
                            }
                        } else {
                            System.out.println("You need to be logged in to view posts.");
                        }
                        break;
                    }
                }
            }
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("Connection closed.");
                }
            } catch (IOException e) {
                System.out.println("Error while closing the socket: " + e.getMessage());
            }
        } finally {
            System.out.println("Program stopped");
        }
    }
}
