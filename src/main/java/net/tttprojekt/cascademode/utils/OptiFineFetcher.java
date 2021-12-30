package net.tttprojekt.cascademode.utils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import net.tttprojekt.cascademode.download.DownloadTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class OptiFineFetcher {

    private static final Logger logger = LoggerFactory.getLogger(OptiFineFetcher.class);

    static {
        //Disable HTMLUnit warnings
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
    }

    public static Optional<String> fetchLink(OptiFineVersion version) {
        try {
            logger.info(String.format("Fetch download link for OptiFine version %s...", version));
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            HtmlPage htmlPage = webClient.getPage(version.getLink());
            HtmlSpan downloadSpan = htmlPage.getHtmlElementById("Download");
            HtmlAnchor downloadAnchor = (HtmlAnchor) downloadSpan.getElementsByTagName("a").get(0);

            URL downloadURL = htmlPage.getFullyQualifiedUrl(downloadAnchor.getHrefAttribute());

            if (downloadURL == null) {
                logger.error(String.format("Error fetching OptiFine download link for version %s. DownloadURL is null.", version));
                return Optional.empty();
            }

            String downloadLink = downloadURL.toString();
            logger.info(String.format("Download link for OptiFine version %s received. URL: %s", downloadLink, downloadLink));

            htmlPage.cleanUp();
            webClient.close();

            return Optional.ofNullable(downloadURL.toString());
        } catch (IOException e) {
            logger.error(String.format("Error fetching OptiFine download link for version %s", version), e);
            return Optional.empty();
        }
    }

    public static DownloadTask getDownloadTask(String destination, OptiFineVersion optiFineVersion) {
        Optional<String> ofURLOptional = OptiFineFetcher.fetchLink(optiFineVersion);
        if (!ofURLOptional.isPresent()) {
            logger.error("The OptiFine download link could not be retrieved.");
            return null;
        }
        return DownloadTask.of(ofURLOptional.get(), destination);
    }

}
