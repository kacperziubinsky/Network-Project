import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFile {
    public static void main(String[] args) throws IOException{
        try{
            File file = new File("file2.dat");
            if(file.exists()) {
                ServerSocket server = new ServerSocket(8000);
                System.out.println("Serwer uruchiomiony...");
                while (true) {
                    try (
                            Socket socket = server.accept();
                            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
                    ) {
                        System.out.println("Zaakceptowano połączenie z klientem..");

                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        int k = 0;
                        byte[] data = new byte[512];
                        System.out.println("Plik w trkacie wysyłania");
                        while ((k = bis.read(data)) != -1)
                            bos.write(data, 0, k);
                        System.out.println("Zakończono transfer");
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }else{
                System.err.println(String.format("Plik '%s' nie został odnaleziony!!", file.getAbsolutePath()));

                }
            } catch (Exception e){
            System.err.print(e);
        }
    }
}
