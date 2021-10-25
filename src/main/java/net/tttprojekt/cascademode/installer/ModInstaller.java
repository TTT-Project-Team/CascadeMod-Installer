package net.tttprojekt.cascademode.installer;

import lombok.Setter;
import net.tttprojekt.cascademode.download.DownloadTask;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import net.tttprojekt.cascademode.utils.OptiFineFetcher;
import net.tttprojekt.cascademode.utils.OptiFineVersion;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.SplittableRandom;


public class ModInstaller implements IModInstaller {

    private static final Logger logger = LoggerFactory.getLogger(ModInstaller.class);

    private static final String MOD_DOWNLOAD_URL = "https://github.com/xNoci/CascadeMod-Installer/blob/dev/modfile/Cascade%20Mod.jar?raw=true";
    private static final String JEI_DOWNLOAD_URL = "https://media.forgecdn.net/files/3040/523/jei_1.12.2-4.16.1.301.jar";

    private static final String MODS_FOLDER = getModDir().getAbsolutePath();

    private static final String FILE_DESTINATION_MOD = MODS_FOLDER + "/Cascade Mod.jar";
    private static final String FILE_DESTINATION_OPTIFINE = MODS_FOLDER + "/OptiFine_1.12.2_HD_U_G5.jar";
    private static final String FILE_DESTINATION_JEI = MODS_FOLDER + "/jei_1.12.2-4.16.1.301.jar";

    private static File getModDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String mcDir = ".minecraft/mods";
        if (osType.contains("win") && System.getenv("APPDATA") != null)
            return new File(System.getenv("APPDATA"), mcDir);
        if (osType.contains("mac"))
            return new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft");
        return new File(userHomeDir, mcDir);
    }

    private final DownloadTaskManager downloadTaskManager;
    private final DownloadTask modDownloadTask;
    private final DownloadTask jeiDownloadTask;

    @Setter private boolean createBackup;
    @Setter private boolean downloadOptiFine;
    @Setter private boolean downloadJustEnoughItems;

    public ModInstaller(DownloadTaskManager downloadTaskManager) {
        logger.info("Creating download tasks...");
        this.downloadTaskManager = downloadTaskManager;

        this.modDownloadTask = downloadTaskManager.createTask(MOD_DOWNLOAD_URL, FILE_DESTINATION_MOD);
        this.jeiDownloadTask = downloadTaskManager.createTask(JEI_DOWNLOAD_URL, FILE_DESTINATION_JEI);
        logger.info("Download tasks created.");

    }

    @Override
    public boolean backupModFolder() {
    public void backupModFolder() {
        boolean emptyDirectory = true;

        File modFolder = new File(MODS_FOLDER);
        try {
            emptyDirectory = FileUtils.isEmptyDirectory(modFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (modFolder.exists() && !emptyDirectory) {
            String backupFolderName = String.format("%s - tttmp-installer backup [%s]", MODS_FOLDER, getRandomHexString(8));
            File modFolderBackup = new File(backupFolderName);
            logger.info(String.format("Backing up mod folder to '%s'...", modFolderBackup.getPath()));
            try {
                FileUtils.copyDirectory(modFolder, modFolderBackup);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Successfully backed up mod folder.");
        }
    }

    @Override
    public void createModFolder() {
        File modFolder = new File(MODS_FOLDER);

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

        this.downloadTaskManager.startAsyncTask(this.modDownloadTask);
        if (this.downloadOptiFine) {
            DownloadTask ofDownloadTask = getOptiFineDownloadTask();
            if (ofDownloadTask != null)
                this.downloadTaskManager.startAsyncTask(ofDownloadTask);
        }
        if (this.downloadJustEnoughItems)
            this.downloadTaskManager.startAsyncTask(this.jeiDownloadTask);

        this.downloadTaskManager.waitForDownloads();
        downloadRunnable.run();
        logger.info("Mods downloaded successfully.");
    }

    private DownloadTask getOptiFineDownloadTask() {
        Optional<String> ofURLOptional = OptiFineFetcher.fetchLink(OptiFineVersion.V_1_12_2_HD_U_G5);
        if (!ofURLOptional.isPresent()) {
            logger.error("The OptiFine download link could not be retrieved.");
            return null;
        }
        return this.downloadTaskManager.createTask(ofURLOptional.get(), FILE_DESTINATION_OPTIFINE);
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
