package net.tttprojekt.installer.download;

import net.tttprojekt.installer.utils.CascadeDownloadFetcher;
import net.tttprojekt.installer.utils.FileDestination;
import net.tttprojekt.installer.utils.OptiFineFetcher;
import net.tttprojekt.installer.utils.OptiFineVersion;

public enum Download {

    JEI(FileDestination.getJustEnoughItems(), "https://media.forgecdn.net/files/3040/523/jei_1.12.2-4.16.1.301.jar"),
    OPTIFINE(FileDestination.getOptiFine(), OptiFineVersion.V_1_12_2_HD_U_G5),
    CASCADE_MOD(FileDestination.getCascadeMod(), CascadeDownloadFetcher.getLatestVersion()),
    FORGE(FileDestination.getForgeInstallerFile(), "https://maven.minecraftforge.net/net/minecraftforge/forge/1.12.2-14.23.5.2855/forge-1.12.2-14.23.5.2855-installer.jar");

    private final DownloadTask downloadTask;

    Download(String destination, String url) {
        this.downloadTask = DownloadTask.of(url, destination);
    }

    Download(String destination, OptiFineVersion optiFineVersion) {
        this.downloadTask = OptiFineFetcher.getDownloadTask(destination, optiFineVersion);
    }

    DownloadTask get() {
        return this.downloadTask;
    }

    boolean isDownloading() {
        return get().isDownloading();
    }

    public void updateURL(String url) {
        this.downloadTask.setDownloadURL(url);
    }
}
