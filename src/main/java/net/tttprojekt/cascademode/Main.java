package net.tttprojekt.cascademode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        printInformation();

        Installer installer = new Installer();
        installer.createModFolder();

        installer.stop();
    }


    private static void printInformation() {
        logger.info("");
        logger.info("-------------------------------------------------------------------------------");
        logger.info("                      Starting TTT-MP Mod-Installer");
        logger.info("                                by Noci");
        logger.info("-------------------------------------------------------------------------------");
        logger.info("");
        logger.info(String.format("Running Java Version %s", System.getProperty("java.version")));
        logger.info(String.format("Application is running on %s (%s)", System.getProperty("os.name"), System.getProperty("os.arch")));
        logger.info(String.format("Operation System Version: %s", System.getProperty("os.version")));
        logger.info("");
        logger.info("-------------------------------------------------------------------------------");
        logger.info("    >>>>>>>>> This installer installs you forge and the needed mods <<<<<<<<<  ");
        logger.info("-------------------------------------------------------------------------------");
        logger.info("");
    }

}
