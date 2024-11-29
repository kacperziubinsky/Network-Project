import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class PostService implements Runnable {
    private static final int PORT = 2134;
    private static final int MAX_POSTS = 10;
    private static final int MAX_POST_LENGTH = 100;
    private static ConcurrentHashMap<String, String> posts = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("PostService running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handlePosts(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handlePosts(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
            String message = input.readLine();
            String[] parts = message.split(" ");

            System.out.println(parts[0]);
            switch(parts[0]) {
                case "post":
                    if (posts.size() < MAX_POSTS) {
                        if (parts[2] == null || parts[2].length() > MAX_POST_LENGTH) {
                            output.writeBytes("Invalid post\n");
                            return;
                        }
                        posts.put(parts[1], parts[2]);
                        output.writeBytes("Post successful\n");
                    } else {
                        output.writeBytes("Post limit reached\n");
                    }
                    return;
                case "view_posts":
                    output.writeBytes("wypisuje se posty:");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}