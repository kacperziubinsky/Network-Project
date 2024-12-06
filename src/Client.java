import java.io.*;
import java.net.*;
import java.util.UUID;

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

                                String ID = UUID.randomUUID().toString();
                                serverOutput.writeBytes("register " + ID + " " + username + " " + password + '\n');

                                String response = serverInput.readLine();
                                if (response == null) {
                                    System.out.println("Server did not respond in time.");
                                    continue;
                                }

                                String[] responseParts = response.split(" ");
                                if (responseParts.length < 3) {
                                    System.out.println("Unexpected server response: " + response);
                                    continue;
                                }

                                String responseID = responseParts[1];
                                String statusCode = responseParts[2];
                                System.out.println("ID: " + responseID + " " + "Status: " + statusCode);

                                if (!responseID.equals(ID)) {
                                    System.out.println("Error: Response ID does not match request ID. Possible server issue.");
                                    System.out.println("Expected ID: " + ID + ", Received ID: " + responseID);
                                    continue;
                                }
                                switch (statusCode) {
                                    case "200":
                                        System.out.println("Registration successful!");
                                        break;
                                    case "400":
                                        System.out.println("Username already exists.");
                                        break;
                                    default:
                                        System.out.println("Unexpected status code: " + statusCode);
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

                                String ID = UUID.randomUUID().toString();
                                serverOutput.writeBytes("login " + ID + " " + username + " " + password + '\n');

                                String response = serverInput.readLine();
                                if (response == null) {
                                    System.out.println("Server did not respond in time.");
                                    continue;
                                }
                                String[] responseParts = response.split(" ");
                                if (responseParts.length < 3) {
                                    System.out.println("Unexpected server response: " + response);
                                    continue;
                                }

                                String responseID = responseParts[1];
                                String statusCode = responseParts[2];
                                System.out.println("ID: " + responseID + " " + "Status: " + statusCode);

                                if (!responseID.equals(ID)) {
                                    System.out.println("Error: Response ID does not match request ID. Possible server issue.");
                                    System.out.println("Expected ID: " + ID + ", Received ID: " + responseID);
                                    continue;
                                }
                                switch (statusCode) {
                                    case "200":
                                        System.out.println("Login successful");
                                        loggedInUser = username;
                                        System.out.println("You are now logged in as: " + loggedInUser);
                                        break;
                                    case "400":
                                        System.out.println("Invalid credentials");
                                        System.out.println("Login failed: Invalid username or password.");
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

                                String ID = UUID.randomUUID().toString();
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
                            System.out.println("Choose a file to send -> full format e.g. 'file.txt' ");
                            FileTransfer.getALLFiles(new File("src/Clientdata"));
                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 1) {
                                System.out.println("Invalid file format. Please enter: <file.txt>");
                                continue;
                            } else {
                                try (Socket clientSocket = new Socket("localhost", 2137);
                                     DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream())) {
                                    String ID = UUID.randomUUID().toString();
                                    serverOutput.writeBytes("send " + ID + " " + parts[0] + '\n');
                                    System.out.println("File is being sent.");
                                } catch (IOException e) {
                                    System.out.println("Error while sending the file: " + e.getMessage());
                                }
                            }
                        } else {
                            System.out.println("You are not logged in.");
                        }
                        break;
                    }
                    case "5": {
                        if (loggedInUser != null) {
                            System.out.println("Choose a file to download -> full format e.g. 'file.txt' ");
                            FileTransfer.getALLFiles(new File("src/ServerData"));
                            String[] parts = userInput.readLine().split(" ");
                            if (parts.length != 1) {
                                System.out.println("Invalid file format. Please enter: <file.txt>");
                                continue;
                            } else {
                                try (Socket clientSocket = new Socket("localhost", 2137);
                                     DataOutputStream serverOutput = new DataOutputStream(clientSocket.getOutputStream())) {
                                    String ID = UUID.randomUUID().toString();
                                    serverOutput.writeBytes("rec " + ID + " " + parts[0] + '\n');
                                    System.out.println("File is being downloaded.");
                                } catch (IOException e) {
                                    System.out.println("Error while downloading the file: " + e.getMessage());
                                }
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
                                String ID = UUID.randomUUID().toString();
                                serverOutput.writeBytes("post " + ID + " " + loggedInUser + " " + encodedPostContent + '\n');
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

                            String ID = UUID.randomUUID().toString();
                            serverOutput.writeBytes("view_posts" + ID + " ");

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
