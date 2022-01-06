package net.tttprojekt.installer.installer;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.tttprojekt.installer.download.Download;
import net.tttprojekt.installer.download.DownloadTaskManager;
import net.tttprojekt.installer.utils.FileDestination;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.SplittableRandom;


public class ModInstaller implements IModInstaller {

    private static final Logger logger = LoggerFactory.getLogger(ModInstaller.class);

    private final DownloadTaskManager downloadTaskManager;

    @Getter @Setter private boolean createBackup;
    @Setter private boolean downloadOptiFine;
    @Setter private boolean downloadJustEnoughItems;

    public ModInstaller(DownloadTaskManager downloadTaskManager) {
        logger.info("Creating download tasks...");
        this.downloadTaskManager = downloadTaskManager;
        logger.info("Download tasks created.");
    }

    @SneakyThrows
    @Override
    public void backupModFolder() {
        if (!createBackup) return;

        File modFolder = new File(FileDestination.getModsFolder());
        if (!modFolder.exists()) return;
        if (FileUtils.isEmptyDirectory(modFolder)) return;

        String backupFolderName = String.format("%s - tttmp-installer backup [%s]", FileDestination.getModsFolder(), getRandomHexString(8));
        File modFolderBackup = new File(backupFolderName);
        logger.info(String.format("Backing up mod folder to '%s'...", modFolderBackup.getPath()));
        try {
            FileUtils.copyDirectory(modFolder, modFolderBackup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Successfully backed up mod folder.");
    }

    @Override
    public void createModFolder() {
        File modFolder = new File(FileDestination.getModsFolder());

        if (modFolder.exists()) {
            try {
                logger.info("Deleting old mod folder...");
                FileUtils.deleteDirectory(modFolder);
                logger.info("Successfully deleted mod folder.");
            } catch (IOException e) {
                logger.error("Error while deleting mod folder", e);
            }
        }

        logger.info("Creating new mod folder...");
        if (modFolder.mkdirs()) {
            logger.info("Successfully created mod folder.");
        } else {
            logger.error("Error while creating mod folder.");
            System.exit(-1);
        }
    }

    @Override
    public void downloadMods(Runnable downloadRunnable) {
        logger.info("Download mods...");

        this.downloadTaskManager.submitAsync(Download.CASCADE_MOD);
        if (this.downloadOptiFine) this.downloadTaskManager.submitAsync(Download.OPTIFINE);
        if (this.downloadJustEnoughItems) this.downloadTaskManager.submitAsync(Download.JEI);

        this.downloadTaskManager.waitForDownloads();
        downloadRunnable.run();
        logger.info("Mods downloaded successfully.");
    }


    private String getRandomHexString(int numchars) {
        SplittableRandom r = new SplittableRandom();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.substring(0, numchars);
    }

}
