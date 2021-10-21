package net.tttprojekt.cascademode.download;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadTask {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTask.class);

    private final String downloadURL;
    private final String fileDestination;

    private AtomicBoolean downloading = new AtomicBoolean();
    private AtomicInteger downloadedBytes = new AtomicInteger();
    private long fileSize = 0;

    private final ExecutorService executorService;

    protected DownloadTask(String url, String destination, ExecutorService executorService) {
        this.downloadURL = url;
        this.fileDestination = destination;
        this.executorService = executorService;
    }


    public void isURLValid() throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(this.downloadURL).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println(downloadURL + " -- " + responseCode);
                throw new IOException(String.format("Cannot download file from url '%s'. Response: %s - %s", downloadURL, responseCode, connection.getResponseMessage()));
            }
        } catch (IOException e) {
            if(e.getMessage().toLowerCase().contains("response")) throw e;
            throw new IOException(String.format("Cannot download file from url '%s'. Could not connect to url.", downloadURL));
        }

    }

    protected void download() throws IOException {
        isURLValid();
        if (isDownloading())
            throw new IllegalStateException("Could not download file. Download is currently running");

        this.fileSize = getFileSize();
        this.downloadedBytes.set(0);
        this.downloading.set(true);

        executorService.submit(() -> {
            logger.info(String.format("Start downloading file from '%s'...", this.downloadURL));
            downloadFile();
            logger.info("Downloaded file.");
            this.downloading.set(false);
        });
    }

    public boolean isDownloading() {
        return this.downloading.get();
    }

    public float getDownloadDifference() {
        return (float) this.downloadedBytes.get() / (float) this.fileSize;
    }

    public int getProgress() {
        return (int) (getDownloadDifference() * 100);
    }

    private void downloadFile() {
        try {
            URL url = new URL(this.downloadURL);

            try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream())) {
                FileOutputStream outputStream = new FileOutputStream(this.fileDestination);
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                    outputStream.write(dataBuffer, 0, bytesRead);
                    this.downloadedBytes.addAndGet(bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private long getFileSize() {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(this.downloadURL).openConnection();
        httpConnection.setRequestMethod("HEAD");
        return httpConnection.getContentLengthLong();
    }

}
