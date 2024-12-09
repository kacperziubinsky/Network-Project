import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FileService implements Runnable {
    private static final int PORT = 2136;
    private static final String CLIENT_DATA_DIR = "src/Clientdata";
    private static final String SERVER_DATA_DIR = "src/ServerData";

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("FileService is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleFileTransfer(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFileTransfer(Socket clientSocket) {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            String message = input.readLine();
            String[] parts = message.split(" ");

            if (parts.length < 3) {
                output.writeBytes("Invalid file transfer format\n");
                return;
            }

            String command = parts[0];
            String username = parts[1];
            String filename = parts[2];
            File clientUserDir = new File(CLIENT_DATA_DIR, username);
            File serverUserDir = new File(SERVER_DATA_DIR, username);

            if (!clientUserDir.exists()) clientUserDir.mkdirs();
            if (!serverUserDir.exists()) serverUserDir.mkdirs();

            switch (command) {
                case "send":
                    sendFile(username, filename, output);
                    break;
                case "rec":
                    receiveFile(username, filename, output);
                    break;
                default:
                    output.writeBytes("Unknown file transfer command\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String username, String filename, DataOutputStream output) throws IOException {
        File sourceDir = new File(CLIENT_DATA_DIR, username);
        File destDir = new File(SERVER_DATA_DIR, username);

        File sourceFile = new File(sourceDir, filename);
        File destFile = new File(destDir, filename);

        if (!sourceFile.exists()) {
            output.writeBytes("File not found in client directory\n");
            return;
        }

        try (
                FileInputStream fis = new FileInputStream(sourceFile);
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        }
        if (sourceFile.delete()) {
            output.writeBytes("File sent successfully\n");
        } else {
            output.writeBytes("File sent but could not be deleted\n");
        }
    }

    private void receiveFile(String username, String filename, DataOutputStream output) throws IOException {
        File sourceDir = new File(SERVER_DATA_DIR, username);
        File destDir = new File(CLIENT_DATA_DIR, username);

        File sourceFile = new File(sourceDir, filename);
        File destFile = new File(destDir, filename);

        if (!sourceFile.exists()) {
            output.writeBytes("File not found in server directory\n");
            return;
        }

        try (
                FileInputStream fis = new FileInputStream(sourceFile);
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        }

        if (sourceFile.delete()) {
            output.writeBytes("File received successfully\n");
        } else {
            output.writeBytes("File received but could not be deleted\n");
        }
    }
}