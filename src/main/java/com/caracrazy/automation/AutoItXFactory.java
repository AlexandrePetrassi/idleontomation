package com.caracrazy.automation;

import autoitx4java.AutoItX;
import com.jacob.com.LibraryLoader;

import java.nio.file.Paths;

public class AutoItXFactory {

    private AutoItXFactory() {
        throw new IllegalStateException("Utility class");
    }

    private static String getDllPath(AutoItXPojo config) {
        return config.getDll().getDirectory();
    }

    private static String getDllName(AutoItXPojo config) {
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

    public static AutoItX create(AutoItXPojo config) {
        return loadAutoIt(getDllPath(config), getDllName(config));
    }
}
