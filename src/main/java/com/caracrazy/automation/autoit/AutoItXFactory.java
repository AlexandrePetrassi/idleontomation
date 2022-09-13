package com.caracrazy.automation.autoit;

import autoitx4java.AutoItX;
import com.jacob.com.LibraryLoader;

import java.nio.file.Paths;

import static com.caracrazy.localization.Messages.messages;

public class AutoItXFactory {

    private AutoItXFactory() {
        throw new IllegalStateException(messages().getErrorUtilityClass());
    }

    private static String getDllPath(AutoItXData config) {
        return config.getDll().getDirectory();
    }

    private static String getDllName(AutoItXData config) {
        return config.getDll().getName();
    }

    private static String getDllPath(String dllDirectory, String dllName) {
        return Paths
                .get(dllDirectory)
                .resolve(dllName)
                .toAbsolutePath()
                .toString();
    }

    private static void loadJacob(String dllPath) {
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, dllPath);
        LibraryLoader.loadJacobLibrary();
    }

    private static AutoItX loadAutoIt(String dllDirectory, String dllName) {
        loadJacob(getDllPath(dllDirectory, dllName));
        return new AutoItX();
    }

    public static AutoItX create(AutoItXData config) {
        return loadAutoIt(getDllPath(config), getDllName(config));
    }
}
