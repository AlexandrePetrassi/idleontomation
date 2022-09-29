package com.caracrazy.automation.autoit;

import java.util.Objects;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dll dll = (Dll) o;
            return getName().equals(dll.getName()) && getDirectory().equals(dll.getDirectory());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getName(), getDirectory());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoItXData that = (AutoItXData) o;
        return getDll().equals(that.getDll());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDll());
    }
}
