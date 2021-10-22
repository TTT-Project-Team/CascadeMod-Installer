package net.tttprojekt.cascademode.installer;

public interface IModInstaller {

    boolean backupModFolder();

    void createModFolder();

    void downloadMods();

}
