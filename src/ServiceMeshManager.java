import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceMeshManager {
    private static Map<String, List<Integer>> serviceRegistry = new HashMap<>();
    private static Agent loginAgent = new Agent(LoginService.class);
    private static Agent registerAgent = new Agent(RegisterService.class);
    private static Agent postAgent = new Agent(PostService.class);
    private static Agent fileAgent = new Agent(FileService.class);
    private static Agent ApiGateway = new Agent(ApiGateway.class);

    private static final String LOG_FILE = "ManagerLogger.log";

    private static int minimumRequestsThreshold = 50;
    private static int monitoringTimePeriodMinutes = 2;

    public static void main(String[] args) throws InterruptedException {
        initializeLogger();

        ApiGateway.startService(3003);
        loginAgent.startService(2231);
        loginAgent.startService(2341);

        serviceRegistry.put("login", loginAgent.getRunningServicesPorts());
        serviceRegistry.put("register", registerAgent.getRunningServicesPorts());
        serviceRegistry.put("post", postAgent.getRunningServicesPorts());
        serviceRegistry.put("file", fileAgent.getRunningServicesPorts());

        startPortDiscoveryService();
        monitorAgents();
    }

    private static void startPortDiscoveryService() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(2137)) {
                log("Port discovery service rozpoczęty na porcie 2137");

                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream())) {

                        String clientIP = clientSocket.getInetAddress().getHostAddress();
                        String serviceName = inFromClient.readLine();
                        log("Klient z IP " + clientIP + " zażądał usługi: " + serviceName);

                        int port = getServicePortOrStart(serviceName);

                        outToClient.writeBytes(port + "\n");
                    }
                }
            } catch (IOException e) {
                log("Usługa wykrywania portów nie powiodła się: " + e.getMessage());
            }
        }).start();
    }

    private static int getServicePortOrStart(String serviceName) {
        List<Integer> ports = serviceRegistry.get(serviceName);

        if (ports == null || ports.isEmpty()) {
            log("Usługa " + serviceName + " nie działa. Uruchamianie nowej instancji...");
            Agent agent = getAgentByName(serviceName);

            if (agent != null) {
                int newPort = findAvailablePort();
                agent.startService(newPort);

                serviceRegistry.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(newPort);
                return newPort;
            } else {
                log("Agent dla usługi " + serviceName + " nie został znaleziony.");
                return -1;
            }
        }

        Random rand = new Random();
        return ports.get(rand.nextInt(ports.size()));
    }

    private static void monitorAgents() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                StringBuilder logBuilder = new StringBuilder();

                for (Map.Entry<String, List<Integer>> entry : serviceRegistry.entrySet()) {
                    String serviceName = entry.getKey();
                    List<Integer> ports = entry.getValue();

                    Agent agent = getAgentByName(serviceName);
                    if (agent != null) {
                        int runningServices = agent.getRunningServiceCount();
                        int totalRequests = agent.getTotalRequestsHandled();

                        String logEntry = String.format("[%s] :: [Uruchomione instancje: %d] [Obsługiwane żądania: %d]",
                                getCurrentTimestamp(), runningServices, totalRequests);
                        logBuilder.append("Usługa: ").append(serviceName).append(" ").append(logEntry).append("\n");

                        int requestsInTimeFrame = agent.getRequestsInTimeFrame(monitoringTimePeriodMinutes);
                        if (runningServices > 1 && requestsInTimeFrame < minimumRequestsThreshold) {
                            int portToStop = ports.remove(ports.size() - 1);
                            agent.stopService(portToStop);
                            log("Zatrzymano instancję usługi " + serviceName + " na porcie " + portToStop);
                        }
                    }
                }

                log(logBuilder.toString().trim());
            }
        }, 0, 5000);
    }

    private static Agent getAgentByName(String serviceName) {
        switch (serviceName) {
            case "login": return loginAgent;
            case "register": return registerAgent;
            case "post": return postAgent;
            case "file": return fileAgent;
            default: return null;
        }
    }

    private static int findAvailablePort() {
        Random rand = new Random();
        return rand.nextInt(1000) + 2000;
    }

    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(getCurrentTimestamp() + " :: " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Błąd zapisu do logu: " + e.getMessage());
        }
    }

    private static void initializeLogger() {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    System.out.println("Utworzono nowy plik logów: " + LOG_FILE);
                }
            } catch (IOException e) {
                System.err.println("Nie można utworzyć pliku logów: " + e.getMessage());
            }
        }
    }
}
