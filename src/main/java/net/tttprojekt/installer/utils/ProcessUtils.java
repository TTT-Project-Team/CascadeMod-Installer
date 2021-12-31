package net.tttprojekt.installer.utils;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessUtils {

    public static Process getProcess(String... processArguments) {
        return getProcess(null, processArguments);
    }

    public static Process getProcess(File processDirectory, String... processArguments) {
        ProcessBuilder processBuilder = new ProcessBuilder(processArguments);
        processBuilder.directory(processDirectory);
        try {
            Process process = processBuilder.start();
            process.waitFor();
            return process;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getProcessInput(Process process) {
        List<String> input = Lists.newArrayList();
        if (process == null) return input;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                input.add(line);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

}
