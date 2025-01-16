import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PostService extends Service {

    public PostService(int port){
        super(port);
    }

    public void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
            String message = input.readLine();
            String[] parts = message.split(" ");
            DBHandler dbHandler = new DBHandler();
            switch(parts[0]) {
                case "post":
                    String encodedData = parts[2];
                    String decodedData = URLDecoder.decode(encodedData, "UTF-8");
                    String user = parts[1];
                    if (dbHandler.addPost(user, decodedData)) {
                        output.writeBytes("Post successful");
                        System.out.println("Post successful");
                    } else
                        output.writeBytes("Post failed");
                    break;
                case "view_posts":
                    List<String[]> posty = dbHandler.getPosts();
                    StringBuilder allPosts = new StringBuilder();
                    for (String[] post : posty) {
                        allPosts.append("ID: ").append(post[0]).append(" Autor: ").append(post[1]).append(" Zawartosc postu: ").append(post[2]).append("## ");
                    }
                    System.out.println(allPosts.toString());
                    output.writeBytes(allPosts.toString());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}