package org.example;

import autoitx4java.AutoItX;

import java.io.IOException;

public class Paint {

    private static final String APP = "mspaint";
    private static final String UNABLE_TO_OPEN_ERROR = "Erro ao abrir aplicação: " + APP;

    private Paint() {
        throw new IllegalStateException("Utility class");
    }

    private static Process open() {
        try {
            return Runtime.getRuntime().exec(APP);
        } catch (IOException e) {
            throw new IllegalStateException(UNABLE_TO_OPEN_ERROR, e);
        }
    }

    private static void close(Process process) {
        process.destroy();
    }

    private static void drawCircle(AutoItX autoItX) {
        int size = 128;
        double increment = Math.PI * 2 / size;
        int offsetX = 1000;
        int offsetY = 500;
        int x = 0;
        int y = 0;
        for (double i = 0; i < Math.PI * 2; i += increment) {
            x = (int)(Math.sin(i) * size + offsetX);
            y = (int)(Math.cos(i) * size + offsetY);
            autoItX.mouseMove(x, y, 0);
            autoItX.mouseDown("left");
        }
        autoItX.mouseUp("left");
    }

    public static void drawCircleUsingPaint(AutoItX autoItX) {
        Process paint = open();
        autoItX.mouseMove(0, 0);
        drawCircle(autoItX);
        close(paint);
    }
}
