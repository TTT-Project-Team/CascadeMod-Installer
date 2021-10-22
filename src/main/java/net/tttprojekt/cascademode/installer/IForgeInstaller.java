package net.tttprojekt.cascademode.installer;

public interface IForgeInstaller {

    void setup();

    void download(Runnable runnable);

    void install();

    void cleanUp();

}
