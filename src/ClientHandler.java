import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Thread {
    private final PostHandler postHandler;
    private Socket clientSocket;
    private BufferedReader input;
    private DataOutputStream output;

    public ClientHandler(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new DataOutputStream(socket.getOutputStream());
        this.postHandler = new PostHandler(output);
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
                    case "send":
                        try {
                            handleSend(parts,"src/Serwisdane/","src/Clientdata/");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "rec":
                        try {
                            handleSend(parts,"src/Clientdata/","src/Serwisdane/");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "logout":
                        handleLogout(parts);
                        break;
                    case "view_posts":
                        postHandler.handleViewPosts();
                        break;
                    case "add_post":
                        postHandler.handleAddPost(parts[1], parts[2] );
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
            Server.loggedInUsers.put(username, false);
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
            output.writeBytes("Login successful!\n");
        }
    }

    private void handleLogout(String[] parts) throws IOException {
        if (parts.length != 2) {
            output.writeBytes("Invalid logout format. Please enter: <username>\n");
            return;
        }
        String username = parts[1];

        if (!Server.loggedInUsers.containsKey(username) || !Server.loggedInUsers.get(username)) {
            output.writeBytes("You are not logged in.\n");
        } else {
            Server.loggedInUsers.put(username, false);
            output.writeBytes("Logout successful!\n");
        }
    }
    private void handleSend(String[] parts,String dir1, String dir2) throws Exception {
        if (parts.length < 2) {
            output.writeBytes("Invalid send command format. Please enter: send <filename>\n");
            return;
        }
        String filename = parts[1];
        FileTransfer.sendFile(dir1, filename, dir2);
    }


}