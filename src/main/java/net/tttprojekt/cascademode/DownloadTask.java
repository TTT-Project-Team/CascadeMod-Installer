package net.tttprojekt.cascademode;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadTask {

    private final String downloadURL;
    private final String fileDestination;

    private AtomicBoolean downloading = new AtomicBoolean();

    public DownloadTask(String url, String destination) {
        this.downloadURL = url;
        this.fileDestination = destination;
    }


    public void download() {
        try {
            URL url = new URL(this.downloadURL);

            try (BufferedInputStream inputStream = new BufferedInputStream(url.openStream())) {
                FileOutputStream outputStream = new FileOutputStream(this.fileDestination);
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

    public boolean isDownloading() {
        return this.downloading.get();
    }

}
