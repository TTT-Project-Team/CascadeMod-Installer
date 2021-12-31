package net.tttprojekt.installer.download;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskManager.class);
    private final ExecutorService executorService;


    public DownloadTaskManager() {
        executorService = Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat("DownloadManager").build());
        int downloads = Download.values().length;
        logger.info(String.format("Found %s different downloads.", downloads));
    }

    public void waitForDownloads() {
        while (true) {
            if (Arrays.stream(Download.values()).noneMatch(Download::isDownloading)) break;
        }
    }

    public void submitAsync(Download task) {
        if (task.get().isDownloading()) {
            throw new IllegalStateException(String.format("Could not start download for %s. Download is currently running.", task.name()));
        }

        task.get().setDownloading(true);
        this.executorService.submit(() -> {
            try {
                task.get().download();
                task.get().setDownloading(false);
            } catch (IOException e) {
                logger.error("An unexpected error occurred in a download task.", e);
            }
        });
    }

    public void submitAndWait(Download task) {
        try {
            task.get().download();
            waitForDownloads();
        } catch (IOException e) {
            logger.error("An unexpected error occurred in a download task.", e);
        }
    }

    public void cleanUp() {
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
