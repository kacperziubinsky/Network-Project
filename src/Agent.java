import java.util.ArrayList;
import java.util.List;

public class Agent {
    private Class<? extends Service> serviceType;
    private List<Service> serviceList;


    public Agent(Class<? extends Service> serviceType) {
        this.serviceType = serviceType;
        this.serviceList = new ArrayList<>();
    }

    public void startService(int port) {
        try {
            Service service = serviceType.getConstructor(int.class).newInstance(port);
            Thread serviceThread = new Thread(service);
            serviceThread.start();
            serviceList.add(service);
            System.out.println("Started " + serviceType.getSimpleName() + " on port " + port);
        } catch (Exception e) {
            System.err.println("Failed to start service: " + e.getMessage());
        }
    }

    public void stopService(int port) {
        Service serviceToStop = null;
        for (Service service : serviceList) {
            if (service.getPort() == port) {
                service.stopService();
                serviceToStop = service;
                System.out.println("Stopped " + serviceType.getSimpleName() + " on port " + port);
                break;
            }
        }
        if (serviceToStop != null) {
            serviceList.remove(serviceToStop);
        } else {
            System.out.println("No service found on port " + port);
        }
    }

    public void stopAllServices() {
        for (Service service : serviceList) {
            service.stopService();
            System.out.println("Stopped " + serviceType.getSimpleName() + " on port " + service.getPort());
        }
        serviceList.clear();
    }

    public List<Integer> getRunningServicesPorts() {
        List<Integer> ports = new ArrayList<>();
        for (Service service : serviceList) {
            ports.add(service.getPort());
        }
        return ports;
    }

    public int getPort(){
        Service last = serviceList.getLast();
        return last.getPort();
    }
}
