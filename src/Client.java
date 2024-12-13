import java.io.*;
import java.net.*;

public class Client {
    private static String loggedInUser = null;

    public static void main(String args[]) throws Exception {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        String command;

        try {
            while (true) {
                System.out.println("Choose:");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Logout");
                System.out.println("4. Wyślij plik (niedziala pan (chwilowo))");
                System.out.println("5. Pobierz plik (niedziala pan (chwilowo))");
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

                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                                serverOutput.writeBytes("register " + username + " " + password + '\n');

                                String response = serverInput.readLine();
                                if (response == null) {
                                    System.out.println("Server did not respond in time.");
                                    continue;
                                }

                                if (response.equals("Username already exists.")) {
                                    System.out.println("Username already exists.");
                                } else if (response.equals("Registration successful!")) {
                                    System.out.println("Registration successful!");
                                }
                            } catch (IOException e) {
                                System.out.println("Error while communicating with the server: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You are already logged in. Please logout first.");
                        }
                        break;
                    }
                    case "2": {
                        if (loggedInUser == null) {
                            System.out.println("Enter username and password (format: <username> <password>):");

                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 2) {
                                System.out.println("Invalid login format. Please enter: <username> <password>");
                                continue;
                            }

                            String username = parts[0];
                            String password = parts[1];

                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                                serverOutput.writeBytes("login " + username + " " + password + '\n');

                                String response = serverInput.readLine();
                                if (response == null) {
                                    System.out.println("Server did not respond in time.");
                                    continue;
                                }
                                System.out.println(response);
                                switch (response) {
                                    case "Login successful":
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
                            } catch (IOException e) {
                                System.out.println("Error while communicating with the server: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You are already logged in. Please logout first.");
                        }
                        break;
                    }
                    case "3": {
                        if (loggedInUser != null) {
                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                                serverOutput.writeBytes("logout " + loggedInUser + '\n');

                                String response = serverInput.readLine();
                                if (response == null) {
                                    System.out.println("Server did not respond in time.");
                                    continue;
                                }

                                if (response.equals("Logout successful!")) {
                                    loggedInUser = null;
                                    System.out.println("You have been logged out.");
                                } else {
                                    System.out.println("Logout failed: " + response);
                                }
                            } catch (IOException e) {
                                System.out.println("Error while communicating with the server: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    }
                    case "4": {
                        if (loggedInUser != null) {
                            System.out.println("Choose a file to send (full format e.g. 'file.txt'):");
                            FileTransfer.getALLFiles(new File("src/Clientdata/" + loggedInUser));
                            String filename = userInput.readLine().trim();

                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                                serverOutput.writeBytes("send " + loggedInUser + " " + filename + "\n");
                                String response = serverInput.readLine();
                                System.out.println(response);
                            } catch (IOException e) {
                                System.out.println("Error while sending file: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    }
                    case "5": {
                        if (loggedInUser != null) {
                            System.out.println("Choose a file to download (full format e.g. 'file.txt'):");
                            FileTransfer.getALLFiles(new File("src/ServerData/" + loggedInUser));
                            String filename = userInput.readLine().trim();

                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                                serverOutput.writeBytes("rec " + loggedInUser + " " + filename + "\n");
                                String response = serverInput.readLine();
                                System.out.println(response);
                            } catch (IOException e) {
                                System.out.println("Error while downloading file: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    }
                    case "6": {
                        if (loggedInUser != null) {
                            System.out.println("You are logged in as: " + loggedInUser);
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    }
                    case "7": {
                        if (loggedInUser != null) {
                            System.out.println("Enter post content:");
                            String postContent = userInput.readLine();

                            try (Socket clientSocket = new Socket("localhost", 2137);
                                 DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))  {

                                String response;
                                String encodedPostContent = URLEncoder.encode(postContent, "UTF-8");
                                serverOutput.writeBytes("post " + loggedInUser + " " + encodedPostContent + '\n');
                                response = serverInput.readLine();
                                System.out.println(response);
                            } catch (IOException e) {
                                System.out.println("Error while adding post: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You need to be logged in to add a post.");
                        }
                        break;
                    }
                    case "8":
                        try (Socket clientSocket = new Socket("localhost", 2137);
                             DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream());
                             BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                            serverOutput.writeBytes("view_posts\n");

                            String response;
                            while ((response = serverInput.readLine()) != null) {
                                String[] posts = response.split("##");
                                for (String post : posts) {
                                    System.out.println(post);
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Error while viewing posts: " + e.getMessage());
                        }
                        break;
                    }
                }
        } finally {
            System.out.println("Program stopped");
        }
    }
}
