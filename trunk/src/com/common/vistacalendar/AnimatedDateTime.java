package com.common.vistacalendar;

import com.common.vistacalendar.internal.AnimatedPanelWithText;
import com.common.vistacalendar.internal.CalendarSettings;
import com.common.vistacalendar.internal.ClockPanel;
import com.common.vistacalendar.internal.DateSelectionAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 * @author Dmitry Savchenko
 */
public class AnimatedDateTime extends JPanel {

    private CalendarPanel calendar = null;
    private ClockPanel time = null;
    private boolean includeTime = false;
    private DateSelectionAction action = null;
    private DateSelectionAction todayAction = null;

    public AnimatedDateTime(boolean includeTime) {
        this.includeTime = includeTime;
        init();
    }

    public static class CalendarDialog extends JDialog {

        private AnimatedDateTime dateTime = null;

        public CalendarDialog(Window parent, boolean includeTime, final DateSelectionAction action) {
            super(parent);
            setUndecorated(true);
            getContentPane().setBackground(CalendarSettings.BACKGROUNDCOLOR);
            setLayout(new BorderLayout());
            dateTime = new AnimatedDateTime(includeTime);
            dateTime.addDateSelectionAction(new DateSelectionAction() {

                @Override
                public void dateSelected(DateExt date) {
                    if (action != null) {
                        try {
                            action.dateSelected(date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    CalendarDialog.this.setVisible(false);
                    CalendarDialog.this.dispose();
                    dateTime.disableTimer();
                }
            });
            add(dateTime);
        }

        public AnimatedDateTime getDateTime() {
            return dateTime;
        }
    }

    public void disableTimer() {
        if (includeTime == true) {
            time.enableAutoGo(false);
        }
    }

    public static CalendarDialog createDialog(Window parent, boolean include_time, DateSelectionAction action) {
        CalendarDialog dialog = new CalendarDialog(parent, include_time, action);
        dialog.pack();
        return dialog;
    }

    private void init() {
        setLayout(new GridBagLayout());
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        setBorder(BorderFactory.createLineBorder(CalendarSettings.BACKGROUNDCOLOR.darker()));
        Dimension d = new Dimension(176, 171);
        JPanel today = getTodayPanel();

        addComponent(today, 0, 0, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0));

        calendar = new CalendarPanel();
        calendar.addDateSelectionAction(new DateSelectionAction() {

            @Override
            public void dateSelected(DateExt date) {
                if (action != null) {
                    action.dateSelected(new DateExt(getDate()));
                }
            }
        });
        addComponent(calendar, 0, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0));

        if (includeTime) {
            time = new ClockPanel();
            d = new Dimension(305, 171);
            addComponent(time, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0));
            time.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    fireSelectDateAction();
                }
            });
        }

        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    public void fireSelectDateAction() {
        if (action != null) {
            action.dateSelected(new DateExt(getDate()));
        }
    }

    private AnimatedPanelWithText getTodayPanel() {
        final AnimatedPanelWithText panel = new AnimatedPanelWithText();
        panel.setInternalText("Today");
        panel.setBackground(CalendarSettings.BACKGROUNDCOLOR);
        panel.setMinimumSize(new Dimension(180, 20));
        panel.setPreferredSize(new Dimension(180, 20));
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setForeground(Color.BLUE);
                String text=dateToString(new Date(), includeTime);
                
                panel.setText(text);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setForeground(Color.BLACK);
                panel.setText("Today");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                today();
            }
        });
        return panel;
    }
    
    private SimpleDateFormat dateFormatterNotTime=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat dateFormatterWithTime=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public String dateToString(Date date, boolean includeTime){
        if(!includeTime){
            return dateFormatterNotTime.format(date);
        }else{
            return dateFormatterWithTime.format(date);
        }
    }

    public void today() {
        DateExt date = new DateExt();
        setDate(date.getDate());
        if (includeTime) {
            time.enableAutoGo(true);
        }
        fireTodayAction(date);
    }

    private void fireTodayAction(DateExt date) {
        if (todayAction != null) {
            todayAction.dateSelected(date);
        }
    }

    public void setDate(Date date) {
        if (date == null) {
            calendar.setDate(new DateExt(), false);
        } else {
            calendar.setDate(new DateExt(date), false);
        }
        if (time != null) {
            if (date == null) {
                time.setDate(new DateExt());
            } else {
                time.setDate(new DateExt(date));
            }
        }
    }

    public Date getDate() {
        DateExt date = calendar.getDate();
        date.clearTime();
        if (time != null) {
            DateExt d = time.getDate();
            date.setHour(d.getHour()).setMinute(d.getMinute()).setSeconds(d.getSeconds());
        }
        return date.getDate();
    }

    private void addComponent(Component component,
            int gridx, int gridy,
            int gridwidth, int gridheight,
            int anchor, int fill, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
                anchor, fill, insets, 1, 1);
        add(component, gbc);
    }

    public void addDateSelectionAction(DateSelectionAction action) {
        this.action = action;
    }

    public void addTodayClickAction(DateSelectionAction action) {
        this.todayAction = action;
    }
}
