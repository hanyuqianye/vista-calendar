package com.common.vistacalendar.internal;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Dmitry Savchenko
 */
public class AnimatedPanel extends JPanel {

    public enum ANIMATIONTYPE {

        LEFTRIGHT, RIGHTLEFT, ALPHA, ZOOMIN, ZOOMOUT
    };
    private BufferedImage lastImage;
    private BufferedImage newImage;
    private ANIMATIONTYPE type;
    private double stepSize = 0.09;//timer will go throught the 0 - 1 with this step
    private int timerDelay = 10;
    private double currentStep = 0;
    private boolean isAnimation = false;//is panel in animation state
    private Object parameter = null;
    private ActionListener endAnimationEvent = null;
    protected boolean skipOwnerDraw = false;

    public void animate(BufferedImage lastImage, BufferedImage newImage, ANIMATIONTYPE type, Object parameter, ActionListener endAnimation) {
        this.lastImage = lastImage;
        this.newImage = newImage;
        this.type = type;
        this.parameter = parameter;
        endAnimationEvent = endAnimation;
        createSchedule();
    }

    private void createSchedule() {
        currentStep = 0;
        isAnimation = true;
        Timer timer = new Timer(timerDelay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                repaint();
                if (!checkEndAnimation()) {
                    Timer t=(Timer)e.getSource();
                    t.stop();
                    endAnimationEvent = null;
                }
            }
        });
        timer.setInitialDelay(10);
        timer.setRepeats(true);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isAnimation) {
            skipOwnerDraw = true;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(CalendarSettings.BACKGROUNDCOLOR);
            g2d.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            switch (type) {
                case ALPHA:
                    drawAlpha(g2d);
                    break;
                case LEFTRIGHT:
                    drawLeftRight(g2d);
                    break;
                case RIGHTLEFT:
                    drawRightLeft(g2d);
                    break;
                case ZOOMIN:
                    drawZoomIn(g2d);
                    break;
                case ZOOMOUT:
                    drawZoomOut(g2d);
                    break;
            }
        } else {
            super.paintComponent(g);
        }
    }

    private void clear(Graphics2D gr) {
        gr.clearRect(0, 0, getWidth(), getHeight());
    }

    private boolean checkEndAnimation() {
        currentStep += stepSize;
        if (currentStep >= 1) {
            currentStep = 0;
            isAnimation = false;
            if (endAnimationEvent != null) {
                endAnimationEvent.actionPerformed(null);
            }
            repaint();
            return false;
        }

        return true;
    }

    private Point getCenterPosition(BufferedImage image) {
        Rectangle rect = getBounds();
        int x = rect.width / 2 - image.getWidth() / 2;
        int y = rect.height / 2 - image.getHeight() / 2;
        Point point = new Point(x, y);
        return point;
    }

    private void drawAlpha(Graphics2D gr) {
        Composite lastComposite = gr.getComposite();
        float lastImageAlpha = (float) (1 - currentStep);
        if (lastImageAlpha > 1) {
            lastImageAlpha = 1;
        }
        if (lastImageAlpha < 0) {
            lastImageAlpha = 0;
        }
        gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lastImageAlpha));
        Point position = getCenterPosition(lastImage);
        gr.drawImage(lastImage, position.x, position.y, null);

        float newImageAlpha = (float) currentStep;
        if (newImageAlpha > 1) {
            newImageAlpha = 1;
        }
        if (newImageAlpha < 0) {
            newImageAlpha = 0;
        }
        gr.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, newImageAlpha));
        position = getCenterPosition(newImage);
        gr.drawImage(newImage, position.x, position.y, null);
        gr.setComposite(lastComposite);
    }

    private void drawLeftRight(Graphics2D gr) {
        Point lastPos = getCenterPosition(lastImage);
        Point newPos = getCenterPosition(newImage);

        int offsetLastImage = (int) (lastImage.getWidth() * currentStep);
        int lx = lastPos.x + offsetLastImage;
        gr.drawImage(lastImage, lx, lastPos.y, null);
        gr.drawImage(newImage, lx - newImage.getWidth(), newPos.y, null);
    }

    private void drawRightLeft(Graphics2D gr) {
        Point lastPos = getCenterPosition(lastImage);
        Point newPos = getCenterPosition(newImage);

        int offsetLastImage = (int) (lastImage.getWidth() * currentStep);
        int lx = lastPos.x - offsetLastImage;
        gr.drawImage(lastImage, lx, lastPos.y, null);
        gr.drawImage(newImage, lx + lastImage.getWidth(), newPos.y, null);
    }

    private void drawZoomIn(Graphics2D gr) {
        Point lastPos = getCenterPosition(lastImage);
        Point newPos = getCenterPosition(newImage);
        Rectangle param = (Rectangle) parameter;
        int x = (int) (param.x - param.x * currentStep) + newPos.x - 1;
        int y = (int) (param.y - param.y * currentStep) + newPos.y;
        int w = (int) ((newImage.getWidth() - param.width) * currentStep) + param.width;
        int h = (int) ((newImage.getHeight() - param.height) * currentStep) + param.height;
//        int centerX=(x+(x+w))/2;
//        int centerY=(y+(y+h))/2;
//        double dx1=(centerX-x)/(double)(centerX-param.x);
//        double dy1=(centerY-y)/(double)(centerY-param.y);
//        
//        
//        double dx2=(centerX-(x+w))/(double)(centerX-(param.x+param.width));
//        double dy2=(centerY-(y+h))/(double)(centerY-(param.y+param.height));
//        
//        int lastPosX1=(int)(centerX-(centerX*dx1));
//        int lastPosY1=(int)(centerY-(centerY*dy1));
//        
//        int lastPosX2=(int)((centerX*dx2)-centerX);
//        int lastPosY2=(int)((centerY*dy2)-centerY);

        //int lastPosX=(int)((lastPos.x*x)/(float)param.x)-lastPos.x;
        //int lastPosY=(int)((lastPos.y*y)/(float)param.y)-lastPos.y;
        //int ndw=(int)((lastImage.getWidth()*w)/(float)param.width);
        //int ndh=(int)((lastImage.getHeight()*h)/(float)param.height);
//        int dx=(int)(param.x-x*1.5);
//        int dy=(int)(param.y-y*1.5);
//        int dw=(int)(w-param.width*1.5);
//        int dh=(int)(h-param.height*1.5);
        //gr.drawImage(lastImage, lastPosX1, lastPosY1, lastPosX2-lastPosX1, lastPosY2-lastPosY1, null);
        gr.drawImage(lastImage, lastPos.x, lastPos.y, null);
        gr.drawImage(newImage, x, y, w, h, this);
    }

    private void drawZoomOut(Graphics2D gr) {
        Point lastPos = getCenterPosition(lastImage);
        Point newPos = getCenterPosition(newImage);
        gr.drawImage(newImage, newPos.x, newPos.y, null);
        Rectangle param = (Rectangle) parameter;
        double step = 1 - currentStep;
        if (step < 0) {
            step = 0;
        }
        if (step > 1) {
            step = 1;
        }

        int x = (int) (param.x - param.x * step) + lastPos.x - 1;
        int y = (int) (param.y - param.y * step) + lastPos.y;
        int w = (int) ((lastImage.getWidth() - param.width) * step) + param.width;
        int h = (int) ((lastImage.getHeight() - param.height) * step) + param.height;
        gr.drawImage(lastImage, x, y, w, h, this);
    }
}
