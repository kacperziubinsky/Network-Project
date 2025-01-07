public class ServiceMeshManager {
    public static void main(String[] args) throws InterruptedException {
        Agent loginAgent = new Agent(LoginService.class);
        Agent registerAgent = new Agent(RegisterService.class);
        Agent postAgnet = new Agent(PostService.class);

        loginAgent.startService(3001);
        loginAgent.startService(3002);

        registerAgent.startService(2137);
        registerAgent.startService(2138);

        postAgnet.startService(2111);




        System.out.println("Running services on ports: " + loginAgent.getRunningServicesPorts());
        System.out.println("Running services on ports: " + registerAgent.getRunningServicesPorts());


        System.out.println(loginAgent.getPort());
        loginAgent.stopService(3002);
        System.out.println(loginAgent.getPort());

        System.out.println("Running services on ports: " + loginAgent.getRunningServicesPorts());

        loginAgent.stopAllServices();
        registerAgent.stopAllServices();
        postAgnet.stopAllServices();

    }
}
