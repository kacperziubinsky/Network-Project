import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Service implements Runnable {
    private int port;
    private volatile boolean running;
    private int requestCount;
    private Queue<Long> requestTimestamps;

    public Service(int port) {
        this.port = port;
        this.running = true;
        this.requestCount = 0;
        this.requestTimestamps = new LinkedList<>();
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
        requestTimestamps.add(System.currentTimeMillis());
        cleanUpOldRequests();
    }

    public synchronized int getRequestCount() {
        return requestCount;
    }

    public synchronized int getRequestsInLastTwoMinutes(int minutes) {
        cleanUpOldRequests();
        return requestTimestamps.size();
    }

    private synchronized void cleanUpOldRequests() {
        long now = System.currentTimeMillis();
        long twoMinutesAgo = now - 2 * 60 * 1000;
        while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < twoMinutesAgo) {
            requestTimestamps.poll();
        }
    }

    public abstract void handleClient(Socket clientSocket);

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(getPort())) {
            System.out.println(getClass().getSimpleName() + " rozpoczęto na porcie: " + getPort());
            while (running) {
                if (serverSocket.isClosed()) break;
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    incrementRequestCount();
                    handleClient(clientSocket);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
