import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientFile {
    public static void main(String args[]){
        try(
                Socket socket = new Socket("localhost", 8000);
                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                ) {
            System.out.println("Połączono z serwerem..");
            File file = new File("file.dat");

            try(
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    ) {
                int k = 0;
                byte[] data = new byte[512];
                System.out.println("Plik w trakcie pobierania...");
                while ((k = bis.read(data)) != -1)
                        bos.write(data, 0 , k);
                System.out.println(String.format("Zakończono zapis pliku %s",file.getAbsolutePath()));
            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (UnknownHostException e){
            System.err.println("Nieznany host");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
