package com.caracrazy.automation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum CmdController {
    INSTANCE;

    private static final long EMULATOR_START_TIMEOUT = 1500L;

    private static final String SYSTEM_ENV = System.getenv("windir");

    public void abrirUnicaInstancia(String dir, String app) {
        try {
            new ProcessBuilder("cmd", "/k", "start /D " + dir + " " + app)
                    .start()
                    .waitFor(EMULATOR_START_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void fecharInstancia(String dir, String app) {
        try {
            new ProcessBuilder("cmd", "/k", "taskkill /T /F /IM " + dir + " " + app)
                    .start()
                    .waitFor(EMULATOR_START_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public long contarProcessos(String regex) {
        return contarProcessos(regex, false);
    }

    public long contarProcessos(String nomeProcesso, boolean log) {
        List<String> lista = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new InputStreamReader(process().getInputStream()))) {
            input.lines().forEach(lista::add);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        if (log) System.out.println(lista.stream().reduce((a, b) -> a + "\n" + b));
        return lista.stream().filter(it -> it.contains(nomeProcesso)).count();
    }

    private static Process process() {
        try {
            return Runtime.getRuntime().exec(SYSTEM_ENV + "\\system32\\tasklist.exe");
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
