public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Tworzymy menedżera
        Manager manager = new Manager();

        // Rejestrujemy usługi
        manager.registerService("LoginService", "localhost:2139");
        manager.registerService("RegisterService", "localhost:2140");


        // Uruchamiamy usługi
        manager.startService("LoginService");
        manager.startService("RegisterService");

        // Sprawdzamy status usług
        manager.checkServicesStatus();

        // Czekamy chwilę, aby zobaczyć wynik
        Thread.sleep(5000);

        // Zatrzymujemy usługę
        manager.stopService("LoginService");

        // Sprawdzamy status po zatrzymaniu
        manager.checkServicesStatus();
    }
}
