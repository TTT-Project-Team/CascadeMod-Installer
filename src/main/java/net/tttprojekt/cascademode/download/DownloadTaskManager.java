package net.tttprojekt.cascademode.download;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTaskManager.class);
    private final ExecutorService executorService;

    private final List<DownloadTask> runningTasks = Lists.newArrayList();

    public DownloadTaskManager() {
        executorService = Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setNameFormat("DownloadManager").build());
    }

    public DownloadTask createTask(String url, String fileDestination) {
        logger.info("Creating new download task");
        logger.info(String.format(" >> Download url: %s ", url));
        logger.info(String.format(" >> File location: %s ", fileDestination));
        return new DownloadTask(url, fileDestination, this);
    }

    public void waitForDownloads() {
        while (true) {
            List<DownloadTask> list = Lists.newArrayList(this.runningTasks);
            if (list.stream().noneMatch(DownloadTask::isDownloading)) break;
        }
    }

    public void startAsyncTask(DownloadTask task) {
        try {
            task.download();
            runningTasks.add(task);
        } catch (IOException e) {
            logger.error("An unexpected error occurred in a download task.", e);
        }
    }

    public void startTask(DownloadTask task) {
        try {
            task.download();
            task.block();
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

    protected void removeTask(DownloadTask downloadTask) {
        this.runningTasks.remove(downloadTask);
    }

    protected ExecutorService getExecutorService() {
        return this.executorService;
    }
}
