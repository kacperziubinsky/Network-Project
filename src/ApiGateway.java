import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApiGateway {
    private static final int API_GATEWAY_PORT = 8080;
    private final ServiceAgent managerAgent;
    private volatile boolean running = true;
    private final ConcurrentHashMap<String, String> serviceRoutes;

    public ApiGateway(ServiceAgent managerAgent) {
        this.managerAgent = managerAgent;
        this.serviceRoutes = new ConcurrentHashMap<>();
        initializeRoutes();
    }

    private void initializeRoutes() {
        serviceRoutes.put("login", "LoginService");
        serviceRoutes.put("register", "RegisterService");
        serviceRoutes.put("post", "PostService");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(API_GATEWAY_PORT)) {
            System.out.println("API Gateway started on port " + API_GATEWAY_PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String request = input.readLine();
            String[] requestParts = request.split(" ", 2);
            String serviceType = requestParts[0].toLowerCase();
            String payload = requestParts.length > 1 ? requestParts[1] : "";

            String serviceName = serviceRoutes.get(serviceType);
            if (serviceName == null) {
                output.writeBytes("Invalid service requested\n");
                return;
            }

            // Get service port from manager through agent
            int servicePort = managerAgent.getServicePort(serviceName);
            if (servicePort == -1) {
                output.writeBytes("Service unavailable\n");
                return;
            }

            // Forward request to actual service
            String response = forwardRequest(servicePort, serviceType + " " + payload);
            output.writeBytes(response + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String forwardRequest(int servicePort, String request) {
        try (Socket serviceSocket = new Socket("localhost", servicePort);
             DataOutputStream serviceOutput = new DataOutputStream(serviceSocket.getOutputStream());
             BufferedReader serviceInput = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()))) {

            serviceOutput.writeBytes(request + "\n");
            return serviceInput.readLine();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error forwarding request";
        }
    }

    public void stop() {
        running = false;
    }
}
