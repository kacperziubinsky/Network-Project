import java.io.*;
import java.net.*;

public class APIGateway {
    private static final int PORT = 2137;


    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("API Gateway listening on port " + PORT);

            Thread loginServiceThread = new Thread(new LoginService());
            loginServiceThread.start();

            Thread registerServiceThread = new Thread(new RegisterService());
            registerServiceThread.start();

            Thread postServiceThread = new Thread(new PostService());
            postServiceThread.start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = input.readLine();
            String[] parts = message.split(" ");
            String command = parts[0];
            System.out.println(command);
            switch (command) {
                case "register":
                    forwardToService(parts, "localhost", 2138, output);
                    break;
                case "login":
                    System.out.println("gg");
                    forwardToService(parts, "localhost", 2139, output);
                    break;
                case "send":
                    forwardToService(parts, "localhost", 2136, output);
                    break;
                case "receive":
                    forwardToService(parts, "localhost", 2135, output);
                    break;
                case "post":
                    System.out.println("ff");
                    forwardToService(parts, "localhost", 2134, output);
                    break;
                case "view_posts":
                    forwardToService(parts, "localhost", 2134, output);
                    break;
                default:
                    output.writeBytes("Unknown command\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void forwardToService(String[] parts, String host, int port, DataOutputStream clientOutput) throws IOException {
        try (Socket serviceSocket = new Socket(host, port);
             DataOutputStream serviceOutput = new DataOutputStream(serviceSocket.getOutputStream());
             BufferedReader serviceInput = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream()))) {

            serviceOutput.writeBytes(String.join(" ", parts) + "\n");
            String response = serviceInput.readLine();
            clientOutput.writeBytes(response + "\n");
        }
    }
}
