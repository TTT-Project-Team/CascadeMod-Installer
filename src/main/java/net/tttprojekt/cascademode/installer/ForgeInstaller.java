package net.tttprojekt.cascademode.installer;

import net.tttprojekt.cascademode.download.Download;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import net.tttprojekt.cascademode.utils.FileDestination;
import net.tttprojekt.cascademode.utils.ProcessUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ForgeInstaller implements IForgeInstaller {

    private static final Logger logger = LoggerFactory.getLogger(ForgeInstaller.class);

    private final DownloadTaskManager downloadTaskManager;

    public ForgeInstaller(DownloadTaskManager downloadTaskManager) {
        logger.info("Initialising forge installer...");
        this.downloadTaskManager = downloadTaskManager;
        logger.info("Forge installer initialised.");
    }

    @Override
    public void setup() {
        File forgeInstallFolder = new File(FileDestination.getForgeFolderPath());
        logger.info("Creating installation folder...");
        if (forgeInstallFolder.exists()) {
            try {
                FileUtils.deleteDirectory(forgeInstallFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        forgeInstallFolder.mkdirs();
        logger.info("Successfully created installation folder.");
    }

    @Override
    public void download() {
        this.downloadTaskManager.submitAndWait(Download.FORGE);
    }

    @Override
    public boolean install() {
        Process process = ProcessUtils.getProcess(new File(FileDestination.getForgeFolderPath()), "java", "-jar", FileDestination.getForgeInstallerFile());
        List<String> processInput = ProcessUtils.getProcessInput(process);

        if (processInput.stream().anyMatch(s -> s.equalsIgnoreCase("Finished!"))) {
            logger.info("Forge was successfully installed.");
            return true;
        } else {
            logger.error("An error occurred during the installation of forge.");
            return false;
        }
    }

    @Override
    public void cleanUp() {
        logger.info("Cleaning up forge installation...");
        try {
            FileUtils.deleteDirectory(new File(FileDestination.getForgeFolderPath()));
            logger.info("The forge installation was successfully cleaned up.");
        } catch (IOException e) {
            logger.error("Error while cleaning up forge installation.", e);
        }
    }

}
