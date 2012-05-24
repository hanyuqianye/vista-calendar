package com.common.vistacalendar;

import com.common.vistacalendar.internal.AnimatedPanel.ANIMATIONTYPE;
import com.common.vistacalendar.internal.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * @author Dmitry Savchenko
 */
public class CalendarPanel extends JPanel {

    protected enum CALENDARTYPE {

        DAY, MONTH, YEAR, DECADE
    };
    private NavigationPanel navigation = null;
    private CalendarSettings settings = new CalendarSettings();
    private DayPanel dayPanel = null;
    private MonthPanel monthPanel = null;
    private YearPanel yearPanel = null;
    private DecadePanel decadePanel = null;
    private CALENDARTYPE currentType = CALENDARTYPE.DAY;
    private AnimatedPanel canvas = null;
    private AbstractPanel currentPanel = null;
    private DateExt selectedDate = new DateExt();
    private boolean isInAnimation = false;
    private DateSelectionAction action = null;

    public CalendarPanel() {
        init();
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        canvas = new AnimatedPanel();
        Dimension d = new Dimension(181, 121);
        canvas.setLayout(new BorderLayout(0, 0));
        canvas.setPreferredSize(d);
        canvas.setMaximumSize(d);
        canvas.setMinimumSize(d);
        canvas.setAlignmentX(0);
        canvas.setBackground(CalendarSettings.BACKGROUNDCOLOR);
        navigation = new NavigationPanel();
        navigation.setAlignmentX(0);
        navigation.addActionListener(new DatePanelEvent() {

            @Override
            public boolean fireEvent(Object param) {
                int type = (Integer) param;
                return navigationHandler(type);
            }
        });

        dayPanel = new DayPanel(settings);
        monthPanel = new MonthPanel(settings);
        yearPanel = new YearPanel(settings);
        decadePanel = new DecadePanel(settings);

        dayPanel.setAlignmentX(0);
        monthPanel.setAlignmentX(0);
        yearPanel.setAlignmentX(0);
        decadePanel.setAlignmentX(0);
        add(navigation);
        add(canvas);
        showPanel(CALENDARTYPE.DAY);
        addPanelsAction();
        updateCaption(dayPanel, false);
    }

    private void enableClick() {
        isInAnimation = false;
    }

    private void disableClick() {
        isInAnimation = true;
    }

    private boolean canClick() {
        return !isInAnimation;
    }

    private void addPanelsAction() {
        DatePanelEvent event = new DatePanelEvent() {

            @Override
            public boolean fireEvent(Object param) {
                if (canClick()) {
                    if(param!=null && param instanceof Integer){
                        int type=(Integer)param;
                        if(type==2 || type==3){//LEFT or RIGHT sliding
                            navigationHandler(type);
                        }
                    }else{
                        zoomIn();
                    }
                }
                return true;

            }
        };
        monthPanel.addPanelEvent(event);
        yearPanel.addPanelEvent(event);
        decadePanel.addPanelEvent(event);
        dayPanel.addPanelEvent(event);
        
        dayPanel.addDateSelectionAction(new DateSelectionAction() {

            @Override
            public void dateSelected(DateExt date) {
                selectedDate = date;
                fireAction(true);
            }
        });
    }

    /**
     * @param type
     * type = 1 - UP
     * type = 2 - LEFT
     * type = 3 - RIGHT
     * @return 
     */
    private boolean navigationHandler(int type) {
        if (!canClick()) {
            return false;
        }

        if (type == 1) { //UP
            CALENDARTYPE newType = getPreviousCalendarType(currentType);
            if (newType == null) {
                return false;
            }
            zoomOut(newType);
        } else {
            slide(type);
        }
        return true;
    }

    private CALENDARTYPE getPreviousCalendarType(CALENDARTYPE type) {
        switch (type) {
            case DAY:
                return CALENDARTYPE.MONTH;
            case MONTH:
                return CALENDARTYPE.YEAR;
            case YEAR:
                return CALENDARTYPE.DECADE;
            case DECADE:
                return null;
        }
        return null;
    }

    private void clearPanels() {
        canvas.removeAll();
    }

    private AbstractPanel getPanelFromType(CALENDARTYPE type) {
        switch (type) {
            case DAY:
                return dayPanel;
            case MONTH:
                return monthPanel;
            case YEAR:
                return yearPanel;
            case DECADE:
                return decadePanel;
        }
        return null;
    }
    //display necessary calendar mode(i.e days, monthes, years...)
    private void showPanel(CALENDARTYPE type) {
        clearPanels();
        switch (type) {
            case DAY:
                currentPanel = dayPanel;
                break;
            case MONTH:
                currentPanel = monthPanel;
                break;
            case YEAR:
                currentPanel = yearPanel;
                break;
            case DECADE:
                currentPanel = decadePanel;
                break;
        }
        canvas.add(currentPanel);
        currentType = type;
        canvas.revalidate();
        canvas.repaint();
    }

    private void zoomOut(final CALENDARTYPE type) {
        BufferedImage lastImage;
        BufferedImage newImage;
        if (currentPanel == null) {
            showPanel(type);
            return;
        }
        disableClick();
        clearPanels();
        lastImage = currentPanel.drawPanelToImage();
        AbstractPanel nextPanel = null;
        switch (type) {
            case DAY:
                nextPanel = dayPanel;
                break;
            case MONTH:
                nextPanel = monthPanel;
                monthPanel.setSelectedMonth(selectedDate.getMonth());
                break;
            case YEAR:
                nextPanel = yearPanel;
                yearPanel.setSelectedYear(selectedDate.getYear());
                break;
            case DECADE:
                nextPanel = decadePanel;
                decadePanel.setSelectedDecade(selectedDate.getYear());
                break;
        }
        newImage = nextPanel.drawPanelToImage();
        Rectangle rect = nextPanel.getSelectedRectangle();
        updateCaption(getPanelFromType(type), true);
        animate(lastImage, newImage, ANIMATIONTYPE.ZOOMOUT, rect, type);
    }

    private void animate(BufferedImage prev, BufferedImage next, ANIMATIONTYPE type, Rectangle rect, final CALENDARTYPE toShowPanel) {
        canvas.animate(prev, next, type, rect, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel(toShowPanel);
                enableClick();
            }
        });
    }

    private void zoomIn() {
        if (currentType == CALENDARTYPE.DAY) {
            return;
        }
        
        disableClick();
        clearPanels();
        BufferedImage last = currentPanel.drawPanelToImage();
        BufferedImage next = null;
        Rectangle rect = null;
        CALENDARTYPE nextType = CALENDARTYPE.DAY;
        if (currentType == CALENDARTYPE.DECADE) {
            selectedDate.setDecade(decadePanel.getSelectedDecade());
            yearPanel.setSelectedYear(selectedDate.getYear());
            next = yearPanel.drawPanelToImage();
            rect = decadePanel.getSelectedRectangle();
            nextType = CALENDARTYPE.YEAR;
        } else if (currentType == CALENDARTYPE.YEAR) {
            selectedDate.setYear(yearPanel.getSelectedYear());
            next = monthPanel.drawPanelToImage();
            rect = yearPanel.getSelectedRectangle();
            nextType = CALENDARTYPE.MONTH;
        } else if (currentType == CALENDARTYPE.MONTH) {
            selectedDate.setMonth(monthPanel.getSelectedMonth());
            dayPanel.setVisibleDate(selectedDate.getDate());
            next = dayPanel.drawPanelToImage();
            rect = monthPanel.getSelectedRectangle();
            nextType = CALENDARTYPE.DAY;
        }
        updateCaption(getPanelFromType(nextType), true);
        animate(last, next, ANIMATIONTYPE.ZOOMIN, rect, nextType);
    }

    private void updateCaption(AbstractPanel panel, boolean animation) {
        String text = "";
        if (panel == dayPanel) {
            String month = settings.getLongMonthName(selectedDate.getMonth());
            String year = String.valueOf(selectedDate.getYear());
            text = month + ", " + year;
        } else if (panel == monthPanel) {
            text = String.valueOf(selectedDate.getYear());
        } else if (panel == yearPanel) {
            int tDecade = selectedDate.getYear() / 10;
            int year1 = tDecade * 10;
            int year2 = year1 + 9;
            text = year1 + "-" + year2;
        } else if (panel == decadePanel) {
            int tDecade = selectedDate.getYear() / 100;
            int year1 = tDecade * 100;
            int year2 = year1 + 99;
            text = year1 + "-" + year2;
        }
//        if (animation == true) {
//            navigation.setText(text);
//        } else {
            navigation.setInternalText(text);
//        }
    }

    //2-left
    //3-right
    private void slide(int type) {
        clearPanels();
        disableClick();
        BufferedImage last = currentPanel.drawPanelToImage();
        BufferedImage next = null;
        ANIMATIONTYPE animType = ANIMATIONTYPE.RIGHTLEFT;

        if (currentPanel == dayPanel) {
            if (type == 2) {
                selectedDate.addMonth(-1);
            } else {
                selectedDate.addMonth(1);
            }
            dayPanel.setVisibleDate(selectedDate.getDate());
            next = currentPanel.drawPanelToImage();
        } else if (currentPanel == monthPanel) {
            if (type == 2) {
                selectedDate.addYear(-1);
            } else {
                selectedDate.addYear(1);
            }
            next = currentPanel.drawPanelToImage();
        } else if (currentPanel == yearPanel) {
            int decade = selectedDate.getDecade();
            if (type == 2) {
                selectedDate.setDecade(decade - 10);
            } else {
                selectedDate.setDecade(decade + 10);
            }
            yearPanel.setSelectedYear(selectedDate.getYear());
            next = currentPanel.drawPanelToImage();
        } else if (currentPanel == decadePanel) {
            int decade = selectedDate.getDecade();
            if (type == 2) {
                selectedDate.setDecade(decade - 100);
            } else {
                selectedDate.setDecade(decade + 100);
            }
            decadePanel.setSelectedDecade(selectedDate.getDecade());
            next = currentPanel.drawPanelToImage();
        }

        if (type == 2) {
            animType = ANIMATIONTYPE.LEFTRIGHT;
        }
        updateCaption(currentPanel, true);
        animate(last, next, animType, null, currentType);
    }

    private void fireAction(boolean ok) {
        if (action != null) {
            if (ok) {
                action.dateSelected(selectedDate);
            } else {
                action.dateSelected(null);
            }
        }
    }

    public DateExt getDate() {
        return selectedDate.clearTime().clone();
    }

    public void setDate(DateExt date, boolean animate) {
        clearPanels();
        date.clearTime();
        //BufferedImage lastImage = currentPanel.drawPanelToImage();
        selectedDate = date.clone();
        dayPanel.setDate(selectedDate.getDate());
        //BufferedImage newImage = dayPanel.drawPanelToImage();
        clearPanels();
        showPanel(CALENDARTYPE.DAY);
        //animate(lastImage, newImage, ANIMATIONTYPE.RIGHTLEFT, null, CALENDARTYPE.DAY);
        updateCaption(dayPanel, true);
    }

    public void addDateSelectionAction(DateSelectionAction action) {
        this.action = action;
    }
}
