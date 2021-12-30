package net.tttprojekt.cascademode.installer;

public interface IForgeInstaller {

    void setup();

    void download(Runnable runnable);

    boolean install();

    void cleanUp();

}
