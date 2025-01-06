import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 3000);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

//            // Symulujemy zapytanie o rejestrację
//            output.writeBytes("register\n");
//            String response = input.readLine();
//            System.out.println(response);

            // Symulujemy zapytanie o logowanie
            output.writeBytes("login admin admin\n");
            String response = input.readLine();
            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
