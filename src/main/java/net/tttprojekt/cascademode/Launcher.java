package net.tttprojekt.cascademode;

import lombok.Getter;
import net.tttprojekt.cascademode.download.DownloadTaskManager;
import net.tttprojekt.cascademode.gui.GUI;
import net.tttprojekt.cascademode.installer.ForgeInstaller;
import net.tttprojekt.cascademode.installer.IForgeInstaller;
import net.tttprojekt.cascademode.installer.IModInstaller;
import net.tttprojekt.cascademode.installer.ModInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Launcher {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private final DownloadTaskManager downloadTaskManager;
    @Getter private final IModInstaller modInstaller;
    @Getter private final IForgeInstaller forgeInstaller;

    private GUI gui;

    public Launcher() {
        logger.info("Creating launcher...");
        this.downloadTaskManager = new DownloadTaskManager();
        this.modInstaller = new ModInstaller(this.downloadTaskManager);
        this.forgeInstaller = new ForgeInstaller(this.downloadTaskManager);
        logger.info("Launcher created.");

        float aspectRatio = (float) 9 / 16;
        int height = 400;
        int width = (int) (height * aspectRatio);

        SwingUtilities.invokeLater(() -> {
            Launcher.this.gui = new GUI("CascadeMod-Installer", width, height, () -> {
                this.exit();
                logger.info("Successfully closed installer.");
            });
            Launcher.this.gui.setModInstaller(this.modInstaller);
            Launcher.this.gui.setForgeInstaller(this.forgeInstaller);
            Launcher.this.gui.updateCheckBox();
        });


    }

    public void exit() {
        this.downloadTaskManager.stop();
    }
}
