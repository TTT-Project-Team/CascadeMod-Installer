package net.tttprojekt.cascademode.download;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskManager.class);
    private final ExecutorService executorService;

    public DownloadTaskManager() {
        executorService = Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat("DownloadManager").build());
    }

    public DownloadTask createTask(String url, String fileDestination) {
        logger.info("Creating new download task");
        logger.info(String.format(" >> Download url: %s ", url));
        logger.info(String.format(" >> File location: %s ", fileDestination));
        return new DownloadTask(url, fileDestination, executorService);
    }

    public void startTask(DownloadTask task) {
        try {
            task.download();
        } catch (IOException e) {
            logger.error("An unexpected error occurred in a download task.", e);
        }
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
