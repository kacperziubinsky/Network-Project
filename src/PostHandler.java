import java.io.*;
import java.util.HashMap;

public class PostHandler {
    protected HashMap<String, String> posts = new HashMap<>();
    private DataOutputStream output;
    private static final int PORT = 2139;

    public PostHandler(DataOutputStream output) {
        this.output = output;
    }

    public void handleAddPost(String username, String message) throws IOException {
        if (username != null && message.split(" ").length >= 2) {
            String postContent = message.substring(message.indexOf(' ') + 1);
            posts.put(username, postContent);
            output.writeBytes("Post added.\n");
        } else if (username == null) {
            output.writeBytes("You need to be logged in to add a post.\n");
        } else {
            output.writeBytes("Invalid post format. Please provide content.\n");
        }
    }

    public void handleViewPosts() throws IOException {
        if (posts.isEmpty()) {
            output.writeBytes("No posts available.\n");
        } else {
            StringBuilder response = new StringBuilder("Posts:\n");
            for (String user : posts.keySet()) {
                response.append(user).append(": ").append(posts.get(user)).append("\n");
            }
            output.writeBytes(response.toString());
        }
    }
}
