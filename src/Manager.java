import java.util.*;

public class Manager {
    private Map<String, Agent> agents;

    public Manager() {
        agents = new HashMap<>();
    }

    // Rejestruje nowego agenta dla usługi
    public void registerService(String serviceName, String serviceAddress) {
        Agent agent = new Agent(serviceName, serviceAddress);
        agents.put(serviceName, agent);
        System.out.println("Service registered: " + serviceName + " at " + serviceAddress);
    }

    // Uruchamia usługę na żądanie
    public void startService(String serviceName) {
        Agent agent = agents.get(serviceName);
        if (agent != null && !agent.isRunning()) {
            agent.startService();
        } else {
            System.out.println("Service is already running or not registered.");
        }
    }

    // Zatrzymuje usługę na żądanie
    public void stopService(String serviceName) {
        Agent agent = agents.get(serviceName);
        if (agent != null && agent.isRunning()) {
            agent.stopService();
        } else {
            System.out.println("Service is not running or not registered.");
        }
    }

    // Sprawdza status wszystkich usług
    public void checkServicesStatus() {
        System.out.println("Checking status of services:");
        for (Map.Entry<String, Agent> entry : agents.entrySet()) {
            String status = entry.getValue().isRunning() ? "Running" : "Stopped";
            System.out.println(entry.getKey() + " is " + status);
        }
    }
}
