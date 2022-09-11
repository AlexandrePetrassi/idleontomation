package org.example;

import autoitx4java.AutoItX;
import com.jacob.com.LibraryLoader;

import java.nio.file.Paths;

public class AutoItXFactory {

    private static AutoItX cache = null;

    private static ConfigurationPojo config = ConfigurationFactory.create();

    public static void setConfig(ConfigurationPojo config) {
        AutoItXFactory.config = config;
    }

    private AutoItXFactory() {
        throw new IllegalStateException("Utility class");
    }

    private static String getDllPath() {
        return config.getAutoIt().getDll().getDirectory();
    }

    private static String getDllName() {
        return config.getAutoIt().getDll().getName();
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

    public static AutoItX create() {
        if (cache == null) {
            cache = loadAutoIt(getDllPath(), getDllName());
        }
        return cache;
    }
}
