//import java.io.*;
//import java.net.*;
//import java.util.*;
//
//public class APIGateway {
//    private static final int PORT = 2137;
//    private ServiceMeshManager serviceMeshManager;
//
//    public APIGateway(ServiceMeshManager serviceMeshManager) {
//        this.serviceMeshManager = serviceMeshManager;
//    }
//
//    public static void main(String[] args) {
//        // Utwórz menedżera i agenty
//        ServiceMeshManager serviceMeshManager = new ServiceMeshManager();
//
//        serviceMeshManager.addAgent("agent1", "LoginService");
//        serviceMeshManager.addAgent("agent2", "RegisterService");
//
//        // Uruchom usługi
//        LoginService loginService = new LoginService(serviceMeshManager);
//        serviceMeshManager.startServiceOnAgent("agent1", "LoginService", loginService);
//
//        RegisterService registerService = new RegisterService(serviceMeshManager);
//        serviceMeshManager.startServiceOnAgent("agent2", "RegisterService", registerService);
//
//        // Utwórz API Gateway
//        APIGateway apiGateway = new APIGateway(serviceMeshManager);
//        apiGateway.start();
//    }
//
//    // Rozpocznij nasłuchiwanie na porcie API Gateway
//    public void start() {
//        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            System.out.println("API Gateway listening on port " + PORT);
//
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                new Thread(() -> handleClient(clientSocket)).start();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleClient(Socket clientSocket) {
//        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
//
//            String message = input.readLine();
//            String[] parts = message.split(" ");
//            String command = parts[0];
//
//            // Zależnie od komendy, Gateway wybiera odpowiedni agent i usługę
//            switch (command) {
//                case "login":
//                    forwardToService(parts, "LoginService", output);
//                    break;
//                case "register":
//                    forwardToService(parts, "RegisterService", output);
//                    break;
//                default:
//                    output.writeBytes("Unknown command\n");
//                    break;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void forwardToService(String[] parts, String serviceName, DataOutputStream clientOutput) throws IOException {
//        // Znajdź IP i port dla usługi w danym agencie
//        String agentId = serviceName.equals("LoginService") ? "agent1" : "agent2";
//        Agent agent = serviceMeshManager.getAgent(agentId);
//        Integer port = agent.getServicePort(serviceName);
//
//        if (port != null) {
//            try (Socket serviceSocket = new Socket(agent.getId(), port);
//                 DataOutputStream serviceOutput = new DataOutputStream(serviceSocket.getOutputStream());
//                 BufferedReader serviceInput = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()))) {
//
//                serviceOutput.writeBytes(String.join(" ", parts) + "\n");
//                String response = serviceInput.readLine();
//                clientOutput.writeBytes(response + "\n");
//            }
//        } else {
//            clientOutput.writeBytes("Service not available\n");
//        }
//    }
//}
