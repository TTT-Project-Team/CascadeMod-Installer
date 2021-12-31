package net.tttprojekt.installer.utils;

import java.util.List;

public class MinecraftChecker {

    private static final String MINECRAFT_PROCESS = "net.minecraft.launchwrapper.Launch";

    public static boolean isMinecraftRunning() {
        Process process = ProcessUtils.getProcess("jps", "-l");
        List<String> input = ProcessUtils.getProcessInput(process);

        return input.stream().anyMatch(processInfo -> processInfo.toLowerCase().contains(MINECRAFT_PROCESS.toLowerCase()));
    }


}
