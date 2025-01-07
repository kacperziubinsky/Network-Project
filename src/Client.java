import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 2111);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {


            output.writeBytes("view_posts\n");
            String response = input.readLine();
            System.out.println(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
