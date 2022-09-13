package org.example.graphics;

import java.awt.*;

public class ColorExtensions {

    public static boolean matchPixel(Color a, Color b, int limit) {
        int red = Math.abs(a.getRed() - b.getRed());
        int blue = Math.abs(a.getBlue() - b.getBlue());
        int green = Math.abs(a.getGreen() - b.getGreen());
        int medium = (red + blue + green) / 3;
        return (medium <= 1 + limit) && (medium >= 1 - limit);
    }

    public static Color averageColor(Color colorA, Color colorB) {
        int r = (colorA.getRed() + colorB.getRed()) / 2;
        int g = (colorA.getGreen() + colorB.getGreen()) / 2;
        int b = (colorA.getBlue() + colorB.getBlue()) / 2;
        return new Color(r, g, b);
    }
}
