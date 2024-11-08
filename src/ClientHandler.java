import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private DataOutputStream output;
    private String username = null;

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
                        handleLogout();
                        break;
                    case "add_post":
                        handleAddPost(message);
                        break;
                    case "view_posts":
                        handleViewPosts();
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

        if (Server.users.containsKey(username)) {
            output.writeBytes("Username already exists.\n");
        } else {
            Server.users.put(username, password);
            Server.loggedInUsers.put(username, false);  // User is initially logged out
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

        if (!Server.users.containsKey(username)) {
            output.writeBytes("Username does not exist.\n");
        } else if (!Server.users.get(username).equals(password)) {
            output.writeBytes("Incorrect password.\n");
        } else if (Server.loggedInUsers.get(username)) {
            output.writeBytes("You are already logged in.\n");
        } else {
            Server.loggedInUsers.put(username, true);
            this.username = username;
            output.writeBytes("Login successful!\n");
        }
    }

    private void handleLogout() throws IOException {
        if (username == null || !Server.loggedInUsers.containsKey(username) || !Server.loggedInUsers.get(username)) {
            output.writeBytes("You are not logged in.\n");
        } else {
            Server.loggedInUsers.put(username, false);
            output.writeBytes("Logout successful!\n");
            username = null;
        }
    }

    private void handleAddPost(String message) throws IOException {
        if (username != null && message.split(" ").length >= 2) {
            String postContent = message.substring(message.indexOf(' ') + 1);
            Server.posts.add(username + ": " + postContent);
            output.writeBytes("Post added.\n");
        } else {
            output.writeBytes("You need to be logged in to add a post.\n");
        }
    }

    private void handleViewPosts() throws IOException {
        if (!Server.posts.isEmpty()) {
            for (String post : Server.posts) {
                output.writeBytes(post + "\n");
            }
            output.writeBytes("END\n"); // Wysyłanie znacznika końca
        } else {
            output.writeBytes("No posts to display.\n");
            output.writeBytes("END\n"); // Wysyłanie znacznika końca
        }
    }
}
