package net.tttprojekt.cascademode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        printInformation();
        logger.info("Starting application...");
        new Launcher();
        logger.info("Application started.");

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

    private static boolean shouldInstallForge() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Do you want to install forge? [y/n]");

        boolean installForge = true;

        while (true) {
            String input = scan.next();
            if (input.equalsIgnoreCase("y")) break;
            if (input.equalsIgnoreCase("n")) {
                installForge = false;
                break;
            }
        }

        return installForge;
    }

}
