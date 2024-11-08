import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    protected static Map<String, String> users = new HashMap<>();  // Przechowywanie użytkowników i haseł
    protected static Map<String, Boolean> loggedInUsers = new HashMap<>();  // Przechowywanie stanu zalogowania
    protected static List<String> posts = new ArrayList<>(); // Przechowywanie postów

    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2137);

        System.out.println("Server ruszyl..");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }
}
