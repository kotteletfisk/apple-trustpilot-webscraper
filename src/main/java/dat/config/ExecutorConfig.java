package dat.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorConfig
{
    private final static int THREADS = 4; // global value. My laptop has 4 cores

    private static ExecutorService executorService;
    public static ExecutorService getExecutorService()
    {
        return executorService == null ? Executors.newFixedThreadPool(THREADS) : executorService;
    }
}
