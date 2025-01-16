import java.io.*;
import java.net.*;
import java.util.*;

public class ServiceMeshManager {
    private static Map<String, List<Integer>> serviceRegistry = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Agent loginAgent = new Agent(LoginService.class);
        Agent registerAgent = new Agent(RegisterService.class);
        Agent postAgent = new Agent(PostService.class);
        Agent apiAgent = new Agent(ApiGateway.class);
        Agent fileAgent = new Agent(FileService.class);

        loginAgent.startService(3001);
        loginAgent.startService(3002);
        apiAgent.startService(3003);
        registerAgent.startService(2139);
        registerAgent.startService(2132);
        registerAgent.startService(2138);
        postAgent.startService(2111);
        fileAgent.startService(1405);

        serviceRegistry.put("login", loginAgent.getRunningServicesPorts());
        serviceRegistry.put("register", registerAgent.getRunningServicesPorts());
        serviceRegistry.put("post", postAgent.getRunningServicesPorts());
        serviceRegistry.put("file", fileAgent.getRunningServicesPorts());

        startPortDiscoveryService();
    }

    private static void startPortDiscoveryService() {
        try (ServerSocket serverSocket = new ServerSocket(2137)) {
            System.out.println("Port discovery service rozpoczęty na porcie 2137");

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
            System.err.println("Usługa wykrywania portów nie powiodła się: " + e.getMessage());
        }
    }

    private static int getServicePort(String serviceName) {
        List<Integer> ports = serviceRegistry.get(serviceName);
        if (ports == null || ports.isEmpty()) {
            return -1;
        }
        Random rand = new Random();
        return ports.get(rand.nextInt(ports.size()));
    }
}