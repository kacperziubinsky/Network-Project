import java.io.*;
import java.net.*;

public class RegisterService implements Runnable {
    private int port;

    public RegisterService(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("RegisterSevice started on port: " + port);
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

            DBHandler db = new DBHandler();
            if (db.createUser(username, password)) {
                output.writeBytes("Registration successful!\n");
            } else {
                output.writeBytes("Error\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}