import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Service implements Runnable {
    private int port;
    private volatile boolean running;
    private int requestCount;

    public Service(int port) {
        this.port = port;
        this.running = true;
        this.requestCount = 0;
    }

    public int getPort() {
        return port;
    }

    public void stopService() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void incrementRequestCount() {
        requestCount++;
    }

    public synchronized int getRequestCount() {
        return requestCount;
    }
    public abstract void handleClient(Socket clientSocket);

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(getPort())) {
            System.out.println(getClass().getSimpleName() + " rozpoczęto na porcie: " + getPort());
            while (running) {
                if (serverSocket.isClosed()) break;
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
