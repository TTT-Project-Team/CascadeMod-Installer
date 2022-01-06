package net.tttprojekt.installer.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class CascadeDownloadFetcher {

    private static final Logger logger = LoggerFactory.getLogger(CascadeDownloadFetcher.class);

    private static final URL VERSION_URL = createVersionURL();
    private static final String DOWNLOAD_URL = "http://23.88.43.243/installer/cascademod/%s/CascadeMod.jar";

    @SneakyThrows
    private static URL createVersionURL() {
        return new URL("http://23.88.43.243/installer/cascademod/version");
    }

    private static JsonObject getVersionObject() {
        try {
            URLConnection connection = VERSION_URL.openConnection();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }

        } catch (IOException e) {
            logger.warn(String.format("Failed to fetch latest cascademod version: %s", e.getMessage()));
        }

        return new JsonObject();
    }

    private static String fetchLatestVersion() {
        logger.info("Fetch the latest cascademod version....");
        JsonObject versionObject = getVersionObject();
        if (!versionObject.has("latest-version")) {
            return null;
        }
        return versionObject.get("latest-version").getAsString();
    }


    public static String getLatestVersion() {
        Optional<String> version = getVersion(fetchLatestVersion());
        version.ifPresent(s -> logger.info(String.format("Download link for the latest CascadeMod received: %s", s)));
        return version.get();
    }

    public static Optional<String> getVersion(String version) {
        if (version == null) return Optional.empty();
        String url = String.format(DOWNLOAD_URL, version);
        return Optional.of(url);
    }

}
