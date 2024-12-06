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
            if (parts.length != 4 || !parts[0].equals("login")) {
                output.writeBytes("Invalid login format\n");
                return;
            }
            String ID = parts[1];
            String username = parts[2];
            String password = parts[3];

            DBHandler db = new DBHandler();
            if(db.loginUser(username, password)){
                output.writeBytes("ID " + ID + " " + "200" +'\n');
            } else{
                output.writeBytes("ID " + ID + " " + "400" +'\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
