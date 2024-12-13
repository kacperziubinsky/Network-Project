import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServiceManager {
    private final ConcurrentHashMap<String, ServiceAgent> agents = new ConcurrentHashMap<>();

    // Rejestracja agenta dla danego serwisu
    public void registerAgent(String serviceName, ServiceAgent agent) {
        agents.put(serviceName, agent);
    }

    // Uruchomienie menedżera i nasłuchiwanie na połączenia
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(2137)) {
            System.out.println("Service Manager is running on port 2137...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Przekazywanie zapytania do odpowiedniego agenta
                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obsługa zapytania od klienta i przekazywanie do agenta
    private void handleClientRequest(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

                String message = input.readLine();
                String[] parts = message.split(" ");
                String command = parts[0];

                ServiceAgent agent = null;

                // Decyzja, do którego agenta skierować zapytanie
                if ("login".equals(command)) {
                    agent = agents.get("LoginService");
                } else {
                    output.writeBytes("Service not available\n");
                }

                if (agent != null) {
                    agent.handleClientRequest(clientSocket);
                } else {
                    output.writeBytes("Service not available\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        ServiceManager manager = new ServiceManager();

        // Rejestracja agentów dla usług logowania na różnych portach
        manager.registerAgent("LoginService", new ServiceAgent(new LoginService(), 2139));
        manager.registerAgent("LoginService2", new ServiceAgent(new LoginService(), 2140));
        manager.registerAgent("LoginService3", new ServiceAgent(new LoginService(), 2141));

        // Uruchomienie menedżera usług
        manager.start();
    }
}
