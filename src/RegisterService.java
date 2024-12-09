import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterService implements Runnable{
    private static final int PORT = 2138;

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
            if (parts.length != 4 || !parts[0].equals("register")) {
                output.writeBytes("Invalid command\n");
                return;
            }
            String ID = parts[1];
            String username = parts[2];
            String password = parts[3];

            DBHandler db = new DBHandler();
            if(db.createUser(username, password)){
                output.writeBytes("ID " + ID + " " + "200" +'\n');
            } else {
                output.writeBytes("ID" + ID + " " + "400" +'\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
