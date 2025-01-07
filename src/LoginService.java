import java.io.*;
import java.net.*;


public class LoginService extends Service {

    public LoginService(int port) {
        super(port);
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

            DBHandler db = new DBHandler();

            if(db.loginUser(username, password)){
                output.writeBytes("Login successful");
            } else{
                output.writeBytes("Error!");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
