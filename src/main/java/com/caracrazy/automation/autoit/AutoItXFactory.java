package com.caracrazy.automation.autoit;

import autoitx4java.AutoItX;
import com.caracrazy.automation.Automator;
import com.caracrazy.automation.robot.RobotFactory;
import com.jacob.com.LibraryLoader;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public enum AutoItXFactory {

    INSTANCE();

    private final Map<AutoItXData, Automator> cache = new HashMap<>();

    private String getDllPath(AutoItXData config) {
        return config.getDll().getDirectory();
    }

    private String getDllName(AutoItXData config) {
        return config.getDll().getName();
    }

    private String resolveDllPath(String dllDirectory, String dllName) {
        return Paths
                .get(dllDirectory)
                .resolve(dllName)
                .toAbsolutePath()
                .toString();
    }

    private void loadJacob(String dllPath) {
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, dllPath);
        LibraryLoader.loadJacobLibrary();
    }

    private AutoItX loadAutoItX(String dllDirectory, String dllName) {
        loadJacob(resolveDllPath(dllDirectory, dllName));
        return new AutoItX();
    }

    private Automator createAutomator(AutoItXData config) {
        AutoItX autoItX = loadAutoItX(getDllPath(config), getDllName(config));
        return new AutoItXAutomator(autoItX, RobotFactory.create());
    }

    public Automator create(AutoItXData config) {
        return cache.computeIfAbsent(config, this::createAutomator);
    }
}
