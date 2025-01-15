import java.util.*;
import java.util.concurrent.*;

public class Agent {
    private final Class<? extends Service> serviceClass;
    private final Map<Integer, Service> runningServices;
    private final ExecutorService executorService;

    public Agent(Class<? extends Service> serviceClass) {
        this.serviceClass = serviceClass;
        this.runningServices = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void startService(int port) {
        try {
            Service service = serviceClass.getDeclaredConstructor(int.class).newInstance(port);
            runningServices.put(port, service);
            executorService.submit(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopService(int port) {
        Service service = runningServices.get(port);
        if (service != null) {
            service.stopService();
            runningServices.remove(port);
        }
    }

    public void stopAllServices() {
        for (Integer port : new ArrayList<>(runningServices.keySet())) {
            stopService(port);
        }
        executorService.shutdown();
    }

    public List<Integer> getRunningServicesPorts() {
        return new ArrayList<>(runningServices.keySet());
    }

    public int getPort() {
        if (!runningServices.isEmpty()) {
            return runningServices.keySet().iterator().next();
        }
        return -1;
    }
}