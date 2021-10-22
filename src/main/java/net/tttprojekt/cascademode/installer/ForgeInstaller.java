package net.tttprojekt.cascademode.installer;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import net.tttprojekt.cascademode.download.DownloadTask;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class ForgeInstaller {

    private static final Logger logger = LoggerFactory.getLogger(ForgeInstaller.class);
    private static final String FORGE_INSTALLER_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/1.12.2-14.23.5.2855/forge-1.12.2-14.23.5.2855-installer.jar";
    private static final String FORGE_INSTALL_FOLDER_PATH = getForgeInstallFolderPath().getAbsolutePath();
    private static final String FORGE_INSTALLER_FILE = FORGE_INSTALL_FOLDER_PATH + "/forge-1.12.2-14.23.5.2855-installer.jar";

    private final DownloadTaskManager downloadTaskManager;
    private final DownloadTask forgeInstallerDownloadTask;

    public ForgeInstaller(DownloadTaskManager downloadTaskManager) {
        this.downloadTaskManager = downloadTaskManager;
        this.forgeInstallerDownloadTask = downloadTaskManager.createTask(FORGE_INSTALLER_URL, FORGE_INSTALLER_FILE);
    }

    private static File getForgeInstallFolderPath() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        path += "/forgeInstaller";
        return new File(path);
    }

    public void setup() {

        File forgeInstallFolder = new File(FORGE_INSTALL_FOLDER_PATH);
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

    public void download() {
        this.downloadTaskManager.startTask(this.forgeInstallerDownloadTask);
    }

    public void install() {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", FORGE_INSTALLER_FILE);
        pb.directory(new File(FORGE_INSTALL_FOLDER_PATH));
        try {
            Process process = pb.start();
            process.waitFor();
            List<String> forgeInput = getForgeInput(process.getInputStream());

            if (forgeInput.stream().anyMatch(s -> s.equalsIgnoreCase("Finished!"))) {
                logger.info("Forge was successfully installed.");
            } else {
                logger.error("An error occurred during the installation of forge.");
            }

        } catch (Exception e) {
            logger.error("Error while installing forge.", e);
        }
    }

    public void cleanUp() {
        logger.info("Cleaning up forge installation...");
        try {
            FileUtils.deleteDirectory(new File(FORGE_INSTALL_FOLDER_PATH));
            logger.info("The forge installation was successfully cleaned up.");
        } catch (IOException e) {
            logger.error("Error while cleaning up forge installation.", e);
        }
    }

    @SneakyThrows
    private static List<String> getForgeInput(InputStream inputStream) {
        List<String> input = Lists.newArrayList();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = in.readLine()) != null) {
            input.add(line);
        }

        in.close();
        return input;
    }

}
