import java.util.HashMap;
import java.util.Map;

public class ServiceMeshManager {
    private Map<String, Agent> agents;
    private int currentPort;

    public ServiceMeshManager(int startingPort) {
        agents = new HashMap<>();
        this.currentPort = startingPort; // Początkowy port do przydzielania
    }

    public void addAgent(String id, String version) {
        if (!agents.containsKey(id)) {
            agents.put(id, new Agent(id, version));
            System.out.println("Agent added: " + id);

            // Automatycznie uruchom domyślne usługi dla agenta
            startDefaultServices(id);
        } else {
            System.out.println("Agent already exists: " + id);
        }
    }

    private void startDefaultServices(String agentId) {
        // Uruchom domyślną usługę LoginService
        LoginService loginService = new LoginService(allocatePort());
        startServiceOnAgent(agentId, "LoginService", loginService);
    }

    public void startServiceOnAgent(String agentId, String serviceName, LoginService service) {
        if (agents.containsKey(agentId)) {
            Agent agent = agents.get(agentId);

            // Uruchom usługę na przydzielonym porcie
            Thread serviceThread = new Thread(service);
            serviceThread.start();

            // Zarejestruj usługę w agencie
            agent.startService(serviceName, service.getPort());
            System.out.println("Service " + serviceName + " started on agent " + agentId + " at port " + service.getPort());
        } else {
            System.out.println("Agent not found: " + agentId);
        }
    }

    public Integer getServicePort(String agentId, String serviceName) {
        if (agents.containsKey(agentId)) {
            Agent agent = agents.get(agentId);
            Integer port = agent.getServicePort(serviceName);
            if (port != null) {
                return port;
            } else {
                System.out.println("Service not found: " + serviceName);
            }
        } else {
            System.out.println("Agent not found: " + agentId);
        }
        return null;
    }

    private int allocatePort() {
        return currentPort++; // Przydziel i zwiększ aktualny port
    }

    public static void main(String[] args) {
        ServiceMeshManager manager = new ServiceMeshManager(3000);

        // Dodaj agenta (usługi zostaną uruchomione automatycznie)
        manager.addAgent("agent1", "v1");

        // Pobierz port uruchomionej usługi
        Integer port = manager.getServicePort("agent1", "LoginService");
        System.out.println("Port of LoginService: " + (port != null ? port : "Service not running"));
    }
}
