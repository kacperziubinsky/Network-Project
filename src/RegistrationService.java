import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class RegistrationService implements Runnable {
    private static final int SERVICE_PORT = 2200;

    @Override
    public void run() {
        try (ServerSocket serviceSocket = new ServerSocket(SERVICE_PORT)) {
            System.out.println("Registration Service started on port " + SERVICE_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serviceSocket.accept();
                handleRegistration(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegistration(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String registrationData = in.readLine();
            String[] parts = registrationData.split(":");
            String username = parts[0];
            String password = parts[1];

            DBHandler db = new DBHandler();
            if(db.createUser(username, password)){
                out.println("200" +'\n');
            } else {
                out.println("400" +'\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RegistrationService service = new RegistrationService();
        new Thread(service).start();
    }
}