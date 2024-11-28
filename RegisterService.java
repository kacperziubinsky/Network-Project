import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterService implements Runnable{
    private static final int PORT = 2138;
    private static ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Register running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = input.readLine();
            String[] parts = message.split(" ");
            if (parts.length != 3 || !parts[0].equals("register")) {
                output.writeBytes("Invalid command\n");
                return;
            }

            String username = parts[1];
            String password = parts[2];

            if (users.putIfAbsent(username, password) == null) {
                output.writeBytes("Registration successful!\n");
            } else {
                output.writeBytes("Username already exists.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
