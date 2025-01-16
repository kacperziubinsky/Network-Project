import java.io.*;
import java.net.*;
import java.util.*;

public class ServiceMeshManager {
    private static Map<String, List<Integer>> serviceRegistry = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        // Initialize services
        Agent loginAgent = new Agent(LoginService.class);
        Agent registerAgent = new Agent(RegisterService.class);
        Agent postAgent = new Agent(PostService.class);
        Agent apiAgent = new Agent(ApiGateway.class);

        // Start services and register them
        loginAgent.startService(3001);
        loginAgent.startService(3002);
        apiAgent.startService(3003);
        registerAgent.startService(2139);
        registerAgent.startService(2138);
        postAgent.startService(2111);

        // Initialize service registry
        serviceRegistry.put("login", loginAgent.getRunningServicesPorts());
        serviceRegistry.put("register", registerAgent.getRunningServicesPorts());
        serviceRegistry.put("post", postAgent.getRunningServicesPorts());

        // Start port discovery service
        startPortDiscoveryService();
    }

    private static void startPortDiscoveryService() {
        try (ServerSocket serverSocket = new ServerSocket(2137)) {
            System.out.println("Port discovery service started on port 2137");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream())) {

                    String serviceName = inFromClient.readLine();

                    System.out.println(serviceName);
                    int port = getServicePort(serviceName);
                    outToClient.writeBytes(port + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Port discovery service failed: " + e.getMessage());
        }
    }

    private static int getServicePort(String serviceName) {
        List<Integer> ports = serviceRegistry.get(serviceName);
        if (ports == null || ports.isEmpty()) {
            return -1;
        }
        // Simple round-robin load balancing
        Random rand = new Random();
        return ports.get(rand.nextInt(ports.size()));
    }
}