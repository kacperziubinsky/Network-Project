import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientTXT {
    public static void main(String args[]) throws Exception{
        String zdanie;
        String zmodyfikowaneZdanie;

        BufferedReader odUzytkownika = new BufferedReader(new InputStreamReader(System.in));
        Socket gniazdoKlienta = new Socket("hostname", 2137);

        DataOutputStream doSerwera = new DataOutputStream(gniazdoKlienta.getOutputStream());
        BufferedReader odSerwera = new BufferedReader(new InputStreamReader(gniazdoKlienta.getInputStream()));

        zdanie = odUzytkownika.readLine();

        doSerwera.writeBytes(zdanie + '\n');

        zmodyfikowaneZdanie = odSerwera.readLine();

        System.out.println("Od Serwera: " + zmodyfikowaneZdanie);
        gniazdoKlienta.close();
    }
}
