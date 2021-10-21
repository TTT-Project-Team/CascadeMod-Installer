package net.tttprojekt.cascademode;

import net.tttprojekt.cascademode.download.DownloadTask;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.SplittableRandom;


public class Installer {

    private static final Logger logger = LoggerFactory.getLogger(Installer.class);

    private static final String MOD_DOWNLOAD_URL = "https://github.com/xNoci/CascadeMod-Installer/blob/dev/modfile/Cascade%20Mod.jar?raw=true";
    private static final String OPTIFINE_DOWNLOAD_URL = "https://optifine.net/downloadx?f=OptiFine_1.12.2_HD_U_G5.jar&x=95dd0de8fe2ef755e347876857646d28";
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

    private DownloadTaskManager downloadTaskManager;
    private DownloadTask modDownloadTask;
    private DownloadTask optifineDownloadTask;
    private DownloadTask jeiDownloadTask;

    private final ForgeInstaller forgeInstaller;

    public Installer() {
        logger.info("Creating download tasks...");
        this.downloadTaskManager = new DownloadTaskManager();

        this.modDownloadTask = downloadTaskManager.createTask(MOD_DOWNLOAD_URL, FILE_DESTINATION_MOD);
        this.optifineDownloadTask = downloadTaskManager.createTask(OPTIFINE_DOWNLOAD_URL, FILE_DESTINATION_OPTIFINE);
        this.jeiDownloadTask = downloadTaskManager.createTask(JEI_DOWNLOAD_URL, FILE_DESTINATION_JEI);
        logger.info("Download tasks created.");

        logger.info("Initialising forge installer...");
        this.forgeInstaller = new ForgeInstaller(this.downloadTaskManager);
        logger.info("Forge installer initialised.");

    }

    private boolean backupModFolder(File modFolder) {
        boolean emptyDirectory = true;

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
                return false;
            }
            logger.info("Successfully backed up mod folder.");
        }
        return true;
    }

    public void createModFolder() {
        File modFolder = new File(MODS_FOLDER);
        if (!backupModFolder(modFolder)) {
            logger.error("Error while backing up mod folder.");
            System.exit(-1);
            return;
        }

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

    public void downloadMods() {
        downloadTaskManager.startAsyncTask(modDownloadTask);
        downloadTaskManager.startAsyncTask(optifineDownloadTask);
        downloadTaskManager.startAsyncTask(jeiDownloadTask);
    }

    public void installForge() {
        logger.info("Installing forge...");
        forgeInstaller.setup();
        forgeInstaller.download();
        forgeInstaller.install();
        forgeInstaller.cleanUp();
        logger.info("Finished forge installation.");
    }

    public void stop() {
        this.downloadTaskManager.stop();
    }

    private String getRandomHexString(int numchars) {
        SplittableRandom r = new SplittableRandom();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }

}
