public interface Service extends Runnable {
    void run();
    public abstract void stopService();
}
