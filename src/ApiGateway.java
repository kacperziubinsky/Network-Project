import java.io.*;
import java.net.*;
import java.util.HashMap;


public class ApiGateway extends Service {
    private HashMap<String, String> serviceMap = new HashMap<>();
    private static final String SERVICE_MESH_HOST = "localhost";
    private static final int SERVICE_MESH_PORT = 2137;

    public ApiGateway(int port) {
        super(port);
        serviceMap.put("login", "LoginService");
        serviceMap.put("post", "PostService");
        serviceMap.put("register", "RegisterService");
    }

    public void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = input.readLine();
            String[] parts = message.split(" ");

            String serviceType = parts[0];
            if (!serviceMap.containsKey(serviceType)) {
                output.writeBytes("Unknown service: " + serviceType + "\n");
                return;
            }

            int servicePort = getPort(serviceType);
            if (servicePort == -1) {
                output.writeBytes("Service unavailable\n");
                return;
            }

            String response = forwardRequest(message, servicePort);
            output.writeBytes(response + "\n");

        } catch (IOException e) {
            System.err.println("Błąd podczas obsługi żądania klienta: " + e.getMessage());
        }
    }

    private String forwardRequest(String message, int port) throws IOException {
        try (Socket serviceSocket = new Socket("localhost", port);
             DataOutputStream toService = new DataOutputStream(serviceSocket.getOutputStream());
             BufferedReader fromService = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()))) {

            toService.writeBytes(message + "\n");
            return fromService.readLine();
        }
    }

    public int getPort(String serviceName) throws IOException {
        try (Socket socket = new Socket(SERVICE_MESH_HOST, SERVICE_MESH_PORT);
             DataOutputStream toServiceMesh = new DataOutputStream(socket.getOutputStream());
             BufferedReader fromServiceMesh = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            toServiceMesh.writeBytes(serviceName + "\n");
            String response = fromServiceMesh.readLine();
            return Integer.parseInt(response);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Błąd podczas pobierania portu: " + e.getMessage());
            return -1;
        }
    }
}