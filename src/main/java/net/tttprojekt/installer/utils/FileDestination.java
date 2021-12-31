package net.tttprojekt.installer.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;

public class FileDestination {
    private static final String MODS_FOLDER = getModDir().getAbsolutePath();

    private static final String FILE_DESTINATION_MOD = MODS_FOLDER + "/Cascade Mod.jar";
    private static final String FILE_DESTINATION_OPTIFINE = MODS_FOLDER + "/OptiFine_1.12.2_HD_U_G5.jar";
    private static final String FILE_DESTINATION_JEI = MODS_FOLDER + "/jei_1.12.2-4.16.1.301.jar";

    private static final String FORGE_INSTALL_FOLDER_PATH = getForgeInstallFolderPath().getAbsolutePath();
    private static final String FORGE_INSTALLER_FILE = FORGE_INSTALL_FOLDER_PATH + "/forge-1.12.2-14.23.5.2855-installer.jar";

    private static File getModDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String mcDir = ".minecraft/mods";
        if (osType.contains("win") && System.getenv("APPDATA") != null)
            return new File(System.getenv("APPDATA"), mcDir);
        if (osType.contains("mac"))
            return new File(new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft"), "mods");
        return new File(userHomeDir, mcDir);
    }

    private static File getForgeInstallFolderPath() {
        String path = Paths.get("").toAbsolutePath().normalize().toString();
        path += "/forgeInstaller";
        return new File(path);
    }

    public static String getModsFolder() {
        return MODS_FOLDER;
    }

    public static String getJustEnoughItems() {
        return FILE_DESTINATION_JEI;
    }

    public static String getCascadeMod() {
        return FILE_DESTINATION_MOD;
    }

    public static String getOptiFine() {
        return FILE_DESTINATION_OPTIFINE;
    }

    public static String getForgeFolderPath() {
        return FORGE_INSTALL_FOLDER_PATH;
    }

    public static String getForgeInstallerFile() {
        return FORGE_INSTALLER_FILE;
    }
}
