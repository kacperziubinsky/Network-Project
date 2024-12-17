import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LoginService implements Runnable {
    private static final int SERVICE_PORT = 2300;
    private final Map<String, String> userCredentials;
    private final Set<String> loggedInUsers;

    public LoginService(Map<String, String> userCredentials) {
        this.userCredentials = userCredentials;
        this.loggedInUsers = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void run() {
        try (ServerSocket serviceSocket = new ServerSocket(SERVICE_PORT)) {
            System.out.println("Login Service started on port " + SERVICE_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serviceSocket.accept();
                handleLogin(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String loginData = in.readLine();
            String[] parts = loginData.split(":");
            String username = parts[0];
            String password = parts[1];

            if (userCredentials.containsKey(username) && 
                userCredentials.get(username).equals(password)) {
                loggedInUsers.add(username);
                out.println("LOGIN_SUCCESS");
            } else {
                out.println("LOGIN_FAILED");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, String> dummyCredentials = new ConcurrentHashMap<>();
        dummyCredentials.put("testuser", "password");
        
        LoginService service = new LoginService(dummyCredentials);
        new Thread(service).start();
    }
}