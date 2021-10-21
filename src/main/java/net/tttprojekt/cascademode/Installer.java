package net.tttprojekt.cascademode;

import net.tttprojekt.cascademode.download.DownloadTask;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.SplittableRandom;


public class Installer {

    private static final Logger logger = LoggerFactory.getLogger(Installer.class);
    private static final String MODS_FOLDER;

    static {
        String modsFolder;
        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            modsFolder = System.getenv("AppData");
            modsFolder = modsFolder.replaceAll("\\\\", "/");
        } else {
            modsFolder = System.getProperty("user.home");
            modsFolder += "/Library/Application Support";

        }

        modsFolder += "/.minecraft/mods";
        MODS_FOLDER = modsFolder;
    }

    private static final String MOD_DOWNLOAD_URL = "https://github.com/xNoci/CascadeMod-Installer/blob/dev/modfile/Cascade%20Mod.jar?raw=true";
    private static final String OPTIFINE_DOWNLOAD_URL = "https://optifine.net/downloadx?f=OptiFine_1.12.2_HD_U_G5.jar&x=95dd0de8fe2ef755e347876857646d28";
    private static final String JEI_DOWNLOAD_URL = "https://www.curseforge.com/minecraft/mc-mods/jei/download/3040523/file";

    private static final String FILE_DESTINATION_MOD = MODS_FOLDER + "/Cascade Mod.jar";
    private static final String FILE_DESTINATION_OPTIFINE = MODS_FOLDER + "/OptiFine_1.12.2_HD_U_G5.jar";
    private static final String FILE_DESTINATION_JEI = MODS_FOLDER + "/jei_1.12.2-4.16.1.301.jar";

    private DownloadTaskManager downloadTaskManager;
    private DownloadTask modDownloadTask;
    private DownloadTask optifineDownloadTask;
    private DownloadTask jeiDownloadTask;

    public Installer() {
        logger.info("Creating download tasks");
        this.downloadTaskManager = new DownloadTaskManager();

        this.modDownloadTask = downloadTaskManager.createTask(MOD_DOWNLOAD_URL, FILE_DESTINATION_MOD);
        this.optifineDownloadTask = downloadTaskManager.createTask(OPTIFINE_DOWNLOAD_URL, FILE_DESTINATION_OPTIFINE);
        this.jeiDownloadTask = downloadTaskManager.createTask(JEI_DOWNLOAD_URL, FILE_DESTINATION_JEI);
    }


    public void stop() {
        this.downloadTaskManager.stop();
    }

}
