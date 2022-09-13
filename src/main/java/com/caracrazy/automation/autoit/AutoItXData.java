package com.caracrazy.automation.autoit;

public class AutoItXData {
    private Dll dll;

    public Dll getDll() {
        return dll;
    }

    public static class Dll {
        private String name;
        private String directory;

        public String getName() {
            return name;
        }

        public String getDirectory() {
            return directory;
        }
    }
}
