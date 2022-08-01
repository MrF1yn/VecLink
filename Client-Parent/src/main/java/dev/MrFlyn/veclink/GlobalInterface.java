package dev.MrFlyn.veclink;

public interface GlobalInterface {

    <T> void log(T message);

    <T> void debug(T message);

    String getServerType();

    String getConfigLocation();

    void startKeepAliveTask();

    void stopKeepAliveTask();
}
