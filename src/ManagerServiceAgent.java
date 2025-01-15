class ManagerServiceAgent implements ServiceAgent {
    private final ServiceMeshManager manager;

    public ManagerServiceAgent(ServiceMeshManager manager) {
        this.manager = manager;
    }

    @Override
    public int getServicePort(String serviceName) {
        Agent agent = manager.getAgentForService(serviceName);
        return agent != null ? agent.getPort() : -1;
    }
}