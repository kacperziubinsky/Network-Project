import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServiceManager implements Runnable {
    private static final int MANAGER_PORT = 8200;
    private final ExecutorService executorService;
    private final Map<String, AgentInfo> registeredAgents;
    private final Map<String, ServiceInstance> runningServices;
    private final Map<String, List<String>> serviceToAgentsMap;

    public ServiceManager() {
        this.executorService = Executors.newCachedThreadPool();
        this.registeredAgents = new ConcurrentHashMap<>();
        this.runningServices = new ConcurrentHashMap<>();
        this.serviceToAgentsMap = new ConcurrentHashMap<>();
    }

    public static class AgentInfo {
        String agentName;
        String nodeAddress;
        int agentPort;
        List<String> availableServices;

        public AgentInfo(String agentName, String nodeAddress, int agentPort, List<String> availableServices) {
            this.agentName = agentName;
            this.nodeAddress = nodeAddress;
            this.agentPort = agentPort;
            this.availableServices = availableServices;
        }
    }

    public static class ServiceInstance {
        String serviceName;
        String agentName;
        String nodeAddress;
        int port;
        String status;

        public ServiceInstance(String serviceName, String agentName, String nodeAddress, int port) {
            this.serviceName = serviceName;
            this.agentName = agentName;
            this.nodeAddress = nodeAddress;
            this.port = port;
            this.status = "RUNNING";
        }
    }

    @Override
    public void run() {
        try (ServerSocket managerSocket = new ServerSocket(MANAGER_PORT)) {
            System.out.println("Service Manager started on port " + MANAGER_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = managerSocket.accept();
                executorService.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message = in.readLine();
            String[] parts = message.split(":");

            switch (parts[0]) {
                case "REGISTER_AGENT":
                    registerAgent(parts[1], parts[2], Integer.parseInt(parts[3]), Arrays.asList(parts[4].split(",")));
                    out.println("AGENT_REGISTERED:" + parts[1]);
                    break;
                case "REQUEST_SERVICE":
                    startService(parts[1]);
                    out.println("SERVICE_STARTED:" + parts[1]);
                    break;
                case "STOP_SERVICE":
                    stopService(parts[1]);
                    out.println("SERVICE_STOPPED:" + parts[1]);
                    break;
                case "GET_SERVICE_STATUS":
                    checkServiceStatus(parts[1], out);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAgent(String agentName, String nodeAddress, int agentPort, List<String> availableServices) {
        AgentInfo agentInfo = new AgentInfo(agentName, nodeAddress, agentPort, availableServices);
        registeredAgents.put(agentName, agentInfo);

        // Update service to agents mapping
        for (String service : availableServices) {
            serviceToAgentsMap.computeIfAbsent(service, k -> new ArrayList<>()).add(agentName);
        }

        System.out.println("Registered Agent: " + agentName + " with services: " + availableServices);
    }

    private void startService(String serviceName) {
        // Find an agent that can run this service
        List<String> eligibleAgents = serviceToAgentsMap.get(serviceName);
        if (eligibleAgents == null || eligibleAgents.isEmpty()) {
            System.out.println("No agents available to start service: " + serviceName);
            return;
        }

        // Choose the first available agent (could implement load balancing later)
        String selectedAgentName = eligibleAgents.get(0);
        AgentInfo agentInfo = registeredAgents.get(selectedAgentName);

        try (Socket agentSocket = new Socket(agentInfo.nodeAddress, agentInfo.agentPort);
             PrintWriter out = new PrintWriter(agentSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()))) {

            // Send start service command
            out.println("START_SERVICE:" + serviceName);
            String response = in.readLine();

            if (response.startsWith("SERVICE_STARTED")) {
                // Create service instance tracking
                ServiceInstance serviceInstance = new ServiceInstance(
                        serviceName,
                        selectedAgentName,
                        agentInfo.nodeAddress,
                        0  // You might want to retrieve the actual port dynamically
                );
                runningServices.put(serviceName, serviceInstance);
                System.out.println("Service " + serviceName + " started on " + selectedAgentName);
            }
        } catch (IOException e) {
            System.err.println("Failed to start service " + serviceName + ": " + e.getMessage());
        }
    }

    private void stopService(String serviceName) {
        ServiceInstance serviceInstance = runningServices.get(serviceName);
        if (serviceInstance == null) {
            System.out.println("Service not running: " + serviceName);
            return;
        }

        try (Socket agentSocket = new Socket(serviceInstance.nodeAddress,
                registeredAgents.get(serviceInstance.agentName).agentPort);
             PrintWriter out = new PrintWriter(agentSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()))) {

            // Send stop service command
            out.println("STOP_SERVICE:" + serviceName);
            String response = in.readLine();

            if (response.startsWith("SERVICE_STOPPED")) {
                runningServices.remove(serviceName);
                System.out.println("Service " + serviceName + " stopped");
            }
        } catch (IOException e) {
            System.err.println("Failed to stop service " + serviceName + ": " + e.getMessage());
        }
    }

    private void checkServiceStatus(String serviceName, PrintWriter out) {
        ServiceInstance serviceInstance = runningServices.get(serviceName);
        if (serviceInstance != null) {
            out.println("SERVICE_STATUS:" + serviceName + ":RUNNING:" +
                    serviceInstance.agentName);
        } else {
            out.println("SERVICE_STATUS:" + serviceName + ":STOPPED");
        }
    }

    public static void main(String[] args) {
        ServiceManager manager = new ServiceManager();
        Thread managerThread = new Thread(manager);
        managerThread.start();
    }
}