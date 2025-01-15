import java.io.*;
import java.net.*;

public class FileService implements Runnable {
    private static final int SERVICE_PORT = 2137;

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(SERVICE_PORT)) {
            System.out.println("File Service started on port " + SERVICE_PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try (
                    Socket socket = server.accept();
                    InputStream in = socket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(in)
                ) {
                    System.out.println("Accepted client connection");

                    ByteArrayOutputStream metadataStream = new ByteArrayOutputStream();
                    byte[] metadataBuffer = new byte[1];
                    while (bis.read(metadataBuffer) != -1) {
                        metadataStream.write(metadataBuffer);
                        if (metadataStream.toString().endsWith("\n\n")) {
                            break;
                        }
                    }

                    String metadata = metadataStream.toString().trim();
                    String[] parts = metadata.split(" ", 3);

                    String username = parts[0];
                    String filename = parts[1] + "f";

                    File file = new File(filename);
                    try (BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(file))) {
                        byte[] buffer = new byte[512];
                        int bytesRead;

                        while ((bytesRead = bis.read(buffer)) != -1) {
                            fileWriter.write(buffer, 0, bytesRead);
                        }
                    }

                    System.out.println("Data from user " + username + " saved as " + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FileService service = new FileService();
        new Thread(service).start();
    }
}