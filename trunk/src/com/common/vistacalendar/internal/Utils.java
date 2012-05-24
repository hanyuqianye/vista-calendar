package com.common.vistacalendar.internal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * @author Dmitry Savchenko
 */
public class Utils {

    public static Dimension getStringWidth(Graphics2D gr, String string) {
        if (string == null || string.isEmpty()) {
            return new Dimension(0, 0);
        }
        FontMetrics fm = gr.getFontMetrics(gr.getFont());
        return new Dimension(fm.stringWidth(string), fm.getHeight());
    }

    public static void drawString(Graphics2D gr, int x, int y, int width, int height, String[] string) {
        Color lastColor = gr.getColor();
        int[] widths = new int[string.length];
        int maxWidth = 0;
        int stringHeight = 0;
        for (int i = 0; i < string.length; i++) {
            Dimension d = getStringWidth(gr, string[i]);
            stringHeight = d.height;
            widths[i] = d.width;
            if (maxWidth < d.width) {
                maxWidth = d.width;
            }
        }
        int strH = stringHeight * string.length;
        y += height / 2 - strH / 2;
        int ty = y;
        for (int i = 0; i < string.length; i++) {
            int tx = x + width / 2 - widths[i] / 2;
            gr.drawString(string[i], tx, ty + stringHeight - 3);
            ty += stringHeight;
        }

        gr.setColor(lastColor);
    }
}
