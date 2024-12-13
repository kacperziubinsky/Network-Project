import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServiceAgent {
    private final Runnable service;
    private final int port;

    public ServiceAgent(Runnable service, int port) {
        this.service = service;
        this.port = port;
    }

    // Uruchomienie mikroserwisu na określonym porcie
    public void startService() {
        new Thread(service).start();
    }

    // Przekazanie zapytania do mikroserwisu
    public void handleClientRequest(Socket clientSocket) {
        new Thread(() -> {
            if (service instanceof LoginService) {
                ((LoginService) service).handleClient(clientSocket);
            }
        }).start();
    }

    // Nasłuchiwanie połączeń na danym porcie
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
