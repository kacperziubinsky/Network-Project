import java.util.HashMap;
import java.util.Map;

public class Agent {
    private String id;
    private String version;
    private Map<String, Integer> services;

    public Agent(String id, String version) {
        this.id = id;
        this.version = version;
        this.services = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    // Metoda do uruchamiania usługi i przypisywania portu
    public void startService(String serviceName, int port) {
        services.put(serviceName, port);
        System.out.println("Service " + serviceName + " registered on agent " + id + " with port " + port);
    }

    // Pobierz port dla danej usługi
    public Integer getServicePort(String serviceName) {
        return services.get(serviceName);
    }

    // Możesz dodać inne metody do zarządzania agentem, np. zatrzymywanie usług itd.
}
