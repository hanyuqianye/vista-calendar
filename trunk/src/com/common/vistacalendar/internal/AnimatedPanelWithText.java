package com.common.vistacalendar.internal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * @author Dmitry Savchenko
 */
public class AnimatedPanelWithText extends AnimatedPanel {

    protected String text = "";
    protected Color textColor = Color.BLACK;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (skipOwnerDraw == false) {
            draw((Graphics2D) g, text, textColor);
        } else {
            skipOwnerDraw = false;
        }
    }

    @Override
    public void setForeground(Color fg) {
        textColor=fg;
        super.setForeground(fg);
    }

    public void setText(String text) {
        final String endText = text;
        BufferedImage lastImage = createImageWithText(getText(), Color.BLACK);
        BufferedImage newImage = createImageWithText(endText, Color.BLACK);
        animate(lastImage, newImage, ANIMATIONTYPE.ALPHA, new Rectangle(100, 10, 10, 20), new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setInternalText(endText);
            }
        });
    }

    public String getText() {
        return text;
    }

    public void setInternalText(String text) {
        this.text = text;
        repaint();
    }

    private BufferedImage createImageWithText(String text, Color color) {
        BufferedImage image = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        draw((Graphics2D) image.getGraphics(), text, color);
        return image;
    }

    private void draw(Graphics2D gr, String text, Color color) {
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color lastColor = gr.getColor();
        gr.setColor(color);
        Utils.drawString(gr, 0, 0, getPreferredSize().width, getPreferredSize().height, new String[]{text});
        gr.setColor(lastColor);
    }
}
