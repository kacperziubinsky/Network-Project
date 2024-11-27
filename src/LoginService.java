import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService implements Runnable {
    private static final int PORT = 2132;
    private static ConcurrentHashMap<String, Boolean> loggedInUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("LoginService running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void handleClient(Socket clientSocket){
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = input.readLine();
            String[] parts = message.split(" ");

            if (parts.length != 3) {
                output.writeBytes("Invalid login format\n");
                return;
            }

            String username = parts[1];
            String password = parts[2];

            if (!users.containsKey(username)) {
                output.writeBytes("Username does not exist\n");
            } else if (!users.get(username).equals(password)) {
                output.writeBytes("Incorrect password\n");
            } else if (loggedInUsers.getOrDefault(username, false)) {
                output.writeBytes("Already logged in\n");
            } else {
                loggedInUsers.put(username, true);
                output.writeBytes("Login successful\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
