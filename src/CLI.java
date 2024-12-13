import java.io.*;
import java.net.*;

public class CLI {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 2137); // 2137 to port menedżera
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Przykładowe zapytanie do loginu
            output.writeBytes("login 123 user password\n");
            String response = input.readLine();
            System.out.println("Response: " + response);
        }
    }
}
