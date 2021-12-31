package net.tttprojekt.installer.installer;

public interface IForgeInstaller {

    void setup();

    void download();

    boolean install();

    void cleanUp();

}
