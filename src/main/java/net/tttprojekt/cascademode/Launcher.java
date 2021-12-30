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
        createWindow();

        this.downloadTaskManager = new DownloadTaskManager();
        this.modInstaller = new ModInstaller(this.downloadTaskManager);
        this.forgeInstaller = new ForgeInstaller(this.downloadTaskManager);

        instantiateWindow();
        logger.info("Launcher created.");

    }

    public void exit() {
        this.downloadTaskManager.stop();
    }

    private void createWindow() {
        SwingUtilities.invokeLater(() -> {
            float aspectRatio = (float) 9 / 16;
            int height = 400;
            int width = (int) (height * aspectRatio);

            Launcher.this.gui = new GUI("CascadeMod-Installer", width, height, () -> {
                this.exit();
                logger.info("Successfully closed installer.");
            });

            Launcher.this.gui.loading(true);
        });
    }

    private void instantiateWindow() {
        this.gui.setModInstaller(this.modInstaller);
        this.gui.setForgeInstaller(this.forgeInstaller);
        this.gui.updateCheckBox();
        Launcher.this.gui.loading(false);
    }
}
