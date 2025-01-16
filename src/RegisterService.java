import java.io.*;
import java.net.*;

public class RegisterService extends Service {

    public RegisterService(int port){
        super(port);
    }

    public void handleClient(Socket clientSocket) {
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