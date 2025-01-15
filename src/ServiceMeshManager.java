import java.util.concurrent.ConcurrentHashMap;

public class ServiceMeshManager {
    private final ConcurrentHashMap<String, Agent> serviceAgents = new ConcurrentHashMap<>();
    private final ApiGateway apiGateway;
    private final ConsoleInterface consoleInterface;

    public ServiceMeshManager() {
        this.apiGateway = new ApiGateway(new ManagerServiceAgent(this));
        this.consoleInterface = new ConsoleInterface();
    }

    public void initializeServices() {
        Agent loginAgent = new Agent(LoginService.class);
        Agent registerAgent = new Agent(RegisterService.class);
        //Agent postAgent = new Agent(PostService.class);

        serviceAgents.put("LoginService", loginAgent);
        serviceAgents.put("RegisterService", registerAgent);
        //serviceAgents.put("PostService", postAgent);

        // Start services
        loginAgent.startService(3001);
        registerAgent.startService(2137);
        //postAgent.startService(2111);
    }

    public void start() {
        System.out.println("Initializing services...");
        initializeServices();

        // Start API Gateway in a separate thread
        new Thread(() -> {
            System.out.println("Starting API Gateway...");
            apiGateway.start();
        }).start();

        // Give services time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Start console interface
        System.out.println("Starting console interface...");
        consoleInterface.start();
    }

    public Agent getAgentForService(String serviceName) {
        return serviceAgents.get(serviceName);
    }

    public void stop() {
        apiGateway.stop();
        serviceAgents.values().forEach(Agent::stopAllServices);
    }

    public static void main(String[] args) {
        ServiceMeshManager manager = new ServiceMeshManager();
        manager.start();
    }
}