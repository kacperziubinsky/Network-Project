import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTXT {
    public static void  main(String args[]) throws Exception{
        String zdanieOdKlienta;
        String zmienioneZdanie;

        ServerSocket gniazdoPowitalne = new ServerSocket(2137);

        while(true){
            Socket gniazdoPolaczeniowe = gniazdoPowitalne.accept();
            BufferedReader odKlienta = new BufferedReader(new InputStreamReader(gniazdoPolaczeniowe.getInputStream()));
            DataOutputStream doKlienta = new DataOutputStream(gniazdoPolaczeniowe.getOutputStream());

            zdanieOdKlienta = odKlienta.readLine();

            zmienioneZdanie = zdanieOdKlienta.toUpperCase() + '\n';

            doKlienta.writeBytes(zmienioneZdanie);
        }
    }
}
