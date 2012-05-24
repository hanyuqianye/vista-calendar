package com.common.vistacalendar.internal;

import com.common.vistacalendar.DateExt;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Dmitry Savchenko
 */
public class ClockPanel extends JPanel {

    private BufferedImage clock = null;
    private int hour = 0;
    private int minute = 0;
    private int seconds = 0;
    private JSpinner timeSpinner = null;
    private Timer timer = null;
    private boolean disableActions = false;
    private ActionListener action = null;

    public ClockPanel() {
        init();
    }

    private void init() {
        getClockImage();//first loading of the clock image. Cannot make lazy initialization, because if it loads at paint time, it blinks
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        Dimension d = new Dimension(130, 150);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        SpinnerDateModel sm = new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY);
        timeSpinner = new JSpinner(sm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(de);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(125));
        timeSpinner.setAlignmentX(0.5f);
        add(timeSpinner);
        d = new Dimension(70, 20);
        timeSpinner.setPreferredSize(d);
        timeSpinner.setMaximumSize(d);
        addSpinnerEventHandler();
        addClickHandler();
    }

    private void addClickHandler() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.actionPerformed(null);
                }
            }
        });
    }

    public void addActionListener(ActionListener action) {
        this.action = action;
    }

    public void enableAutoGo(boolean b) {
        if (b == true) {
            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(1000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    DateExt date = getDate();
                    date.addSecond(1);
                    setDate(date);
                    repaint();
                }
            });
            timer.setRepeats(true);
            timer.start();
        } else {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
        }
    }

    private void addSpinnerEventHandler() {
        DefaultEditor editor = (DefaultEditor) timeSpinner.getEditor();
        editor.getTextField().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                enableAutoGo(false);
            }
        });
        timeSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (disableActions == true) {
                    return;
                }
                DateExt date = new DateExt((Date) timeSpinner.getValue());
                hour = date.getHour();
                minute = date.getMinute();
                seconds = date.getSeconds();
                repaint();
                enableAutoGo(false);
            }
        });
    }

    private BufferedImage getClockImage() {
        if (clock == null) {
            try {
                clock=ImageIO.read(ClockPanel.class.getResourceAsStream("/com/common/vistacalendar/resources/clock.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return clock;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.drawImage(getClockImage(), 0, 0, null);

        double h = getHourAngle();
        double m = getMinuteAngle();
        double s = getSecondAngle();
        gr.setColor(Color.GRAY);
        drawArrow(gr, 2, 30, h, 60, 60);
        drawArrow(gr, 2, 45, m, 60, 60);
        drawArrow(gr, 1, 45, s, 60, 60);
    }

    private double getSecondAngle() {
        double angle = (360.0 / 60.0) * seconds;
        angle -= 90.0;
        return Math.toRadians(angle);
    }

    private double getMinuteAngle() {
        double angle = (360.0 / 60.0) * minute;//basic minute angle
        angle += (360.0 / 60.0) * (seconds / 60.0);   //offset from the second arrow
        angle -= 90.0;
        return Math.toRadians(angle);
    }

    private double getHourAngle() {
        int h = hour;
        if (h > 12) {
            h = h -= 12;
        }
        double angle = (360.0 / 12.0) * h;//basic hour angle
        angle += (360.0 / 12.0) * (minute / 60.0);   //offset from the second arrow
        angle -= 90.0;
        return Math.toRadians(angle);
    }

    private void drawArrow(Graphics2D graph, int width, int length, double angle, int x, int y) {
        BasicStroke stroke = new BasicStroke(width);
        GeneralPath path = new GeneralPath();
        path.moveTo(x, y);

        int nx = (int) (Math.cos(angle) * length) + x;
        int ny = (int) (Math.sin(angle) * length) + y;
        path.lineTo(nx, ny);

        Stroke lastStroke = graph.getStroke();
        graph.setStroke(stroke);
        graph.draw(path);
        graph.setStroke(lastStroke);
    }

    public void setDate(DateExt date) {
        hour = date.getHour();
        minute = date.getMinute();
        seconds = date.getSeconds();
        disableActions = true;
        timeSpinner.setValue(date.getDate());
        disableActions = false;
    }

    public DateExt getDate() {
        return new DateExt().setHour(hour).setMinute(minute).setSeconds(seconds).setMilliseconds(0);
    }
}
