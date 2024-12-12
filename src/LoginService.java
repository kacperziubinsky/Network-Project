import java.io.*;
import java.net.*;

public class LoginService implements Runnable {
    private int port;

    public LoginService(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("LoginService running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obsługuje zapytania od klientów
    public void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = input.readLine();
            String[] parts = message.split(" ");
            if (parts.length != 4 || !parts[0].equals("login")) {
                output.writeBytes("Invalid login format\n");
                return;
            }

            String ID = parts[1];
            String username = parts[2];
            String password = parts[3];

            DBHandler db = new DBHandler();
            if (db.loginUser(username, password)) {
                output.writeBytes("ID " + ID + " 200\n");  // 200 oznacza sukces
            } else {
                output.writeBytes("ID " + ID + " 400\n");  // 400 oznacza błąd
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
