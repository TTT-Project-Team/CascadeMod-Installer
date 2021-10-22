package net.tttprojekt.cascademode;

import lombok.Getter;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import net.tttprojekt.cascademode.installer.ForgeInstaller;
import net.tttprojekt.cascademode.installer.IForgeInstaller;
import net.tttprojekt.cascademode.installer.IModInstaller;
import net.tttprojekt.cascademode.installer.ModInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private final DownloadTaskManager downloadTaskManager;
    @Getter private final IModInstaller modInstaller;
    @Getter private final IForgeInstaller forgeInstaller;

    public Launcher() {
        logger.info("Creating launcher...");
        this.downloadTaskManager = new DownloadTaskManager();
        this.modInstaller = new ModInstaller(this.downloadTaskManager);
        this.forgeInstaller = new ForgeInstaller(this.downloadTaskManager);
        logger.info("Launcher created.");
    }

    public void exit() {
        this.downloadTaskManager.stop();
    }
}
