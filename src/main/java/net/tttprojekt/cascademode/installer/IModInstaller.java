package net.tttprojekt.cascademode.installer;

import java.util.function.Consumer;

public interface IModInstaller {

    boolean backupModFolder();

    void createModFolder();

    void downloadMods(Runnable download);

    void setCreateBackup(boolean createBackup);

    void setDownloadOptiFine(boolean download);

    void setDownloadJustEnoughItems(boolean download);

}
