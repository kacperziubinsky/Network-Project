import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class LoginService implements Runnable {
    private static final int PORT = 2139;
    private static ConcurrentHashMap<String, Boolean> loggedInUsers = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

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
