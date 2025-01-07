import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Service implements Runnable {
    private int port;
    private volatile boolean running = true;

    public Service(int port) {
        this.port = port;
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

    public abstract void handleClient(Socket clientSocket);

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(getPort())) {
            System.out.println(getClass().getSimpleName() + " started on port: " + getPort());
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
