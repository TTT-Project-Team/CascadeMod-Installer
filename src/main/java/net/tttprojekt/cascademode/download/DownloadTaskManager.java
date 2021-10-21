package net.tttprojekt.cascademode.download;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadTaskManager {

    private final ExecutorService executorService;

    public DownloadTaskManager() {
        executorService = Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat("DownloadManager T-$d").build());
    }

    public DownloadTask createTask(String url, String fileDestination) {
        return new DownloadTask(url, fileDestination, executorService);
    }

    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
    }

}
