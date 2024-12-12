import java.io.*;
import java.net.*;

public class Agent {
    private String serviceName;
    private String serviceAddress;
    private boolean isRunning;
    private Thread serviceThread;

    public Agent(String serviceName, String serviceAddress) {
        this.serviceName = serviceName;
        this.serviceAddress = serviceAddress;
        this.isRunning = false;
    }

    // Uruchamia usługę
    public void startService() {
        if (!isRunning) {
            System.out.println("Starting service: " + serviceName);
            isRunning = true;
            serviceThread = new Thread(() -> runService());
            serviceThread.start();
        }
    }

    // Zatrzymuje usługę
    public void stopService() {
        if (isRunning) {
            System.out.println("Stopping service: " + serviceName);
            isRunning = false;
            serviceThread.interrupt(); // Przerywa wątek usługi
        }
    }

    // Uruchamia wątek usługi (np. LoginService)
    private void runService() {
        try {
            // Zakładamy, że każda usługa działa w osobnym porcie
            int port = Integer.parseInt(serviceAddress.split(":")[1]);
            if (serviceName.equals("LoginService")) {
                LoginService loginService = new LoginService(port);
                loginService.run();
            }
            // Można dodać inne usługi w przyszłości
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sprawdza, czy usługa jest uruchomiona
    public boolean isRunning() {
        return isRunning;
    }
}
