import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class PostService implements Runnable {
    private static final int SERVICE_PORT = 2400;
    private final List<String> posts;

    public PostService() {
        this.posts = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        try (ServerSocket serviceSocket = new ServerSocket(SERVICE_PORT)) {
            System.out.println("Post Service started on port " + SERVICE_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serviceSocket.accept();
                handlePostRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePostRequest(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String postData = in.readLine();

            if (postData.equals("VIEW_POSTS")) {
                // Send all posts
                for (String post : posts) {
                    out.println(post);
                }
                out.println("END_OF_POSTS");
            } else {
                // Regular post creation
                String[] parts = postData.split(":", 2);
                String username = parts[0];
                String postContent = parts[1];

                posts.add(username + ": " + postContent);
                out.println("POST_SUCCESS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PostService service = new PostService();
        new Thread(service).start();
    }
}