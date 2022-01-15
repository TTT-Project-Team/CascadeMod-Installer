package net.tttprojekt.installer.installer;

public interface IModInstaller {

    void backupModFolder();

    void createModFolder();

    void downloadMods(Runnable download);

    void setCreateBackup(boolean createBackup);

    void setDownloadOptiFine(boolean download);

    void setDownloadJustEnoughItems(boolean download);

    void setDownloadToggleSprint(boolean download);

}
