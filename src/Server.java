import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    protected static Map<String, String> users = new HashMap<>();
    protected static Map<String, Boolean> loggedInUsers = new HashMap<>();

    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket = new ServerSocket(2137);

        System.out.println("Server ruszyl..");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new ClientHandler(clientSocket).start();
        }
    }


}
