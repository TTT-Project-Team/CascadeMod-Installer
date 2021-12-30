package net.tttprojekt.cascademode.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadTask {

    private static final Logger logger = LoggerFactory.getLogger(DownloadTask.class);

    private final String downloadURL;
    private final String fileDestination;

    private AtomicBoolean downloading = new AtomicBoolean();

    public static DownloadTask of(String url, String destination) {
        return new DownloadTask(url, destination);
    }

    protected DownloadTask(String url, String destination) {
        this.downloadURL = url;
        this.fileDestination = destination;
    }


    private void isURLValid() throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(this.downloadURL).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException(String.format("Cannot download file. Server returned HTTP response code: %s for URL: %s", responseCode, this.downloadURL));
            }

        } catch (IOException e) {
            setDownloading(false);
            if (e.getMessage().toLowerCase().contains("response")) throw e;
            throw new IOException(String.format("Cannot download file. Could not connect to URL: %s", this.downloadURL));
        }

    }

    protected void download() throws IOException {
        isURLValid();

        logger.info(String.format("Start downloading file from '%s'...", this.downloadURL));
        downloadFile();
        logger.info("Successfully downloaded file.");
        logger.info(String.format(" >> Downloaded from URL: %s", this.downloadURL));
        logger.info(String.format(" >> File Location: %s", this.fileDestination));
    }

    public boolean isDownloading() {
        return this.downloading.get();
    }

    protected void setDownloading(boolean downloading) {
        this.downloading.set(downloading);
    }

    private void downloadFile() {
        try {
            URL url = new URL(this.downloadURL);

            try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
                 FileOutputStream outputStream = new FileOutputStream(this.fileDestination)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                    outputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
