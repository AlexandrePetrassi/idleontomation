package org.example.data;

public class ConfigurationPojo {
    private AutoIt autoIt;

    public AutoIt getAutoIt() {
        return autoIt;
    }
    public static class AutoIt {
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
