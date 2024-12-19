import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LoginService implements Runnable {
    private static final int SERVICE_PORT = 2300;




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

            DBHandler db = new DBHandler();


            if(db.loginUser(username, password)){
                out.println("200" +'\n');
            } else{
                out.println("400" +'\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginService service = new LoginService();
        new Thread(service).start();
    }
}