import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Agent implements Runnable {
    private static final int MANAGER_PORT = 8200;
    private final String agentName;
    private final int agentPort;
    private final Map<String, Process> runningServices;
    private final ExecutorService executorService;
    private final Map<String, ServiceInfo> serviceRegistry;

    // Service information class
    public static class ServiceInfo {
        String className;
        int defaultPort;

        public ServiceInfo(String className, int defaultPort) {
            this.className = className;
            this.defaultPort = defaultPort;
        }
    }

    public Agent(String agentName, int agentPort) {
        this.agentName = agentName;
        this.agentPort = agentPort;
        this.runningServices = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.serviceRegistry = new ConcurrentHashMap<>();

        // Populate service registry
        serviceRegistry.put("RegistrationService",
                new ServiceInfo("RegistrationService", 2200));
        serviceRegistry.put("LoginService",
                new ServiceInfo("LoginService", 2300));
        serviceRegistry.put("PostService",
                new ServiceInfo("PostService", 2400));
        serviceRegistry.put("FileService",
                new ServiceInfo("FileService", 2137));
    }

    @Override
    public void run() {
        registerWithManager();

        try (ServerSocket agentSocket = new ServerSocket(agentPort)) {
            System.out.println(agentName + " started on port " + agentPort);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = agentSocket.accept();
                executorService.submit(() -> handleAgentCommand(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerWithManager() {
        try (Socket managerSocket = new Socket("localhost", MANAGER_PORT);
             PrintWriter out = new PrintWriter(managerSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(managerSocket.getInputStream()))) {

            // Send agent registration details with available services
            String availableServices = String.join(",", serviceRegistry.keySet());
            out.println("REGISTER_AGENT:" + agentName + ":" + agentPort + ":" + availableServices);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAgentCommand(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String command = in.readLine();
            String[] parts = command.split(":");

            switch (parts[0]) {
                case "START_SERVICE":
                    startService(parts[1]);
                    out.println("SERVICE_STARTED:" + parts[1]);
                    break;
                case "STOP_SERVICE":
                    stopService(parts[1]);
                    out.println("SERVICE_STOPPED:" + parts[1]);
                    break;
                case "CHECK_SERVICE":
                    checkServiceStatus(parts[1], out);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startService(String serviceName) {
        // Check if service is already running
        if (runningServices.containsKey(serviceName)) {
            System.out.println(serviceName + " is already running.");
            return;
        }

        // Retrieve service info from registry
        ServiceInfo serviceInfo = serviceRegistry.get(serviceName);
        if (serviceInfo == null) {
            System.out.println("Unknown service: " + serviceName);
            return;
        }

        try {
            // Prepare the command to start the service
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    serviceInfo.className
            );

            // Start the process
            Process serviceProcess = processBuilder.start();
            runningServices.put(serviceName, serviceProcess);

            System.out.println("Started service: " + serviceName);
        } catch (IOException e) {
            System.err.println("Failed to start service " + serviceName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopService(String serviceName) {
        Process process = runningServices.get(serviceName);
        if (process != null) {
            process.destroy();
            runningServices.remove(serviceName);
            System.out.println("Stopped service: " + serviceName);
        } else {
            System.out.println("Service not running: " + serviceName);
        }
    }

    private void checkServiceStatus(String serviceName, PrintWriter out) {
        boolean isRunning = runningServices.containsKey(serviceName);
        out.println("SERVICE_STATUS:" + serviceName + ":" + (isRunning ? "RUNNING" : "STOPPED"));
    }

    public static void main(String[] args) {
        Agent agent = new Agent("MainAgent", 2100);
        new Thread(agent).start();
    }
}