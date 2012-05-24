package com.common.vistacalendar.internal;

import com.common.vistacalendar.components.InteractiveIcon;
import com.common.vistacalendar.components.LinkIconLabel;
import diplom.dialogs.utils.BooleanEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * @author Dmitry Savchenko
 */
public class NavigationPanel extends AnimatedPanelWithText {

    private LinkIconLabel leftButton = null;
    private LinkIconLabel rightButton = null;
    private DatePanelEvent action = null;
    private final int UPENVENT = 1;
    private final int LEFTEVENT = 2;
    private final int RIGHTEVENT = 3;

    public NavigationPanel() {
        init();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        Dimension d = new Dimension(175, 30);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        addMouseListeners();
        leftButton = new LinkIconLabel(null, new InteractiveIcon(createLeftArrow()));
        leftButton.addClickEvent(new BooleanEvent() {

            @Override
            public boolean fireEvent(Object object) {
                fireAction(LEFTEVENT);
                return true;
            }
        });
        rightButton = new LinkIconLabel(null, new InteractiveIcon(createRightArrow()));
        rightButton.addClickEvent(new BooleanEvent() {

            @Override
            public boolean fireEvent(Object object) {
                fireAction(RIGHTEVENT);
                return true;
            }
        });
        d = leftButton.getPreferredSize();
        d.height = 28;
        d = rightButton.getPreferredSize();
        d.height = 28;
        add(leftButton);
        add(Box.createHorizontalGlue());
        add(rightButton);
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                textColor = Color.BLUE;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                textColor = Color.BLACK;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                fireAction(UPENVENT);
            }
        });
    }

    //Draw small left arrow
    private BufferedImage createLeftArrow() {
        return createArrowImage(true);
    }

    //draw small right arrow
    private BufferedImage createRightArrow() {
        return createArrowImage(false);
    }

    //if leftOrientation = true - left arrow, else - right arrow
    private BufferedImage createArrowImage(boolean leftOrientation) {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        Polygon p = new Polygon();
        if (leftOrientation == true) {
            p.addPoint(5, 0);
            p.addPoint(5, 8);
            p.addPoint(1, 4);
        } else {
            p.addPoint(4, 0);
            p.addPoint(8, 4);
            p.addPoint(4, 8);
        }
        g.fillPolygon(p);
        g.dispose();
        return image;
    }

    public void addActionListener(DatePanelEvent action) {
        this.action = action;
    }

    public void fireAction(int type) {
        if (action != null) {
            action.fireEvent(type);
        }
    }
}