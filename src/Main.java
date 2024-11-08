import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    private static Map<String, String> users = new HashMap<>();  // Przechowywanie użytkowników i haseł
    private static Map<String, Boolean> loggedInUsers = new HashMap<>();  // Przechowywanie stanu zalogowania

    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2137);

        System.out.println("Server ruszyl..");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = input.readLine()) != null) {
                    String[] parts = message.split(" ");
                    String command = parts[0];

                    switch (command) {
                        case "register":
                            handleRegister(parts);
                            break;
                        case "login":
                            handleLogin(parts);
                            break;
                        case "logout":
                            handleLogout(parts);
                            break;
                        default:
                            output.writeBytes("Unknown command.\n");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRegister(String[] parts) throws IOException {
            if (parts.length != 3) {
                output.writeBytes("Invalid registration format. Please enter: <username> <password>\n");
                return;
            }
            String username = parts[1];
            String password = parts[2];

            if (users.containsKey(username)) {
                output.writeBytes("Username already exists.\n");
            } else {
                users.put(username, password);
                loggedInUsers.put(username, false);  // User is initially logged out
                output.writeBytes("Registration successful!\n");
            }
        }

        private void handleLogin(String[] parts) throws IOException {
            if (parts.length != 3) {
                output.writeBytes("Invalid login format. Please enter: <username> <password>\n");
                return;
            }
            String username = parts[1];
            String password = parts[2];

            if (!users.containsKey(username)) {
                output.writeBytes("Username does not exist.\n");
            } else if (!users.get(username).equals(password)) {
                output.writeBytes("Incorrect password.\n");
            } else if (loggedInUsers.get(username)) {
                output.writeBytes("You are already logged in.\n");
            } else {
                loggedInUsers.put(username, true);
                output.writeBytes("Login successful!\n");
            }
        }

        private void handleLogout(String[] parts) throws IOException {
            if (parts.length != 2) {
                output.writeBytes("Invalid logout format. Please enter: <username>\n");
                return;
            }
            String username = parts[1];

            if (!loggedInUsers.containsKey(username) || !loggedInUsers.get(username)) {
                output.writeBytes("You are not logged in.\n");
            } else {
                loggedInUsers.put(username, false);
                output.writeBytes("Logout successful!\n");
            }
        }

    }
}
