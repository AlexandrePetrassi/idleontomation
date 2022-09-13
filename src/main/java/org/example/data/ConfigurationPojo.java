package org.example.data;

public class ConfigurationPojo {
    private AutoItData autoIt;

    public AutoItData getAutoIt() {
        return autoIt;
    }
    public static class AutoItData {
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
}
