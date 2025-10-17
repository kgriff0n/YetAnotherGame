package dev.kgriffon.simplegame.util;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {

    public static Color randomColor() {
        Random rand = new Random();
        return HSLtoRGB(rand.nextFloat(), 0.8f, 0.5f);
    }

    public static Color HSLtoRGB(float h, float s, float l) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRGB(p, q, h + 1f/3f);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1f/3f);
        }

        return new Color(r, g, b);
    }

    private static float hueToRGB(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f/6f) return p + (q - p) * 6 * t;
        if (t < 1f/2f) return q;
        if (t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6;
        return p;
    }
}
