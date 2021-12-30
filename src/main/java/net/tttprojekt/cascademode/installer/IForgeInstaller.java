package net.tttprojekt.cascademode.installer;

public interface IForgeInstaller {

    void setup();

    void download();

    boolean install();

    void cleanUp();

}
