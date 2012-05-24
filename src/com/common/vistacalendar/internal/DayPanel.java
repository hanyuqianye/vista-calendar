package com.common.vistacalendar.internal;

import com.common.vistacalendar.DateExt;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Date;

/**
 * @author Dmitry Savchenko
 */
public class DayPanel extends AbstractPanel {

    private DateExt currentDate = null;
    private Color colorDayOfCurrentMonth = Color.BLACK;
    private Color colorDayOfNotCurrentMonth = Color.GRAY;
    private Color colorSeparatorLine = CalendarSettings.BACKGROUNDCOLOR.darker();
    private DateExt currentSelectedDate = null;
    private int actualMonthStartCell = -1;
    private int actualMonthEndCell = -1;
    private DateSelectionAction action = null;

    public DayPanel() {
        settings = new CalendarSettings();
        init();
    }

    public DayPanel(CalendarSettings settings) {
        this.settings = settings;
        init();
    }

    private void init() {
        CELLROWS = 7;
        CELLCOLUMNS = 7;
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        Dimension d = new Dimension(180, 120);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        currentDate = new DateExt();
        currentSelectedDate = currentDate.clone();
        currentDate.setFirstDayOfWeek(settings.getStartDay());
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMove(e.getX(), e.getY());
            }
        });
    }

    private void mouseDown(int x, int y) {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int cellHeight = height / CELLROWS;
        int cellWidth = width / CELLCOLUMNS;
        int px = x / cellWidth;
        int py = y / cellHeight;
        if (py == 0) {
            return;
        }
        int cellNumber = getCellNumber(px, py);
        if (cellNumber >= actualMonthStartCell && cellNumber < actualMonthEndCell) {
            mouseDownSelection = new Point(px, py);
            fireAction();
        }else{
            if(cellNumber<actualMonthStartCell){
                fireDatePanelEventAction(2);//LEFT SCROLLING
            }else{
                fireDatePanelEventAction(3);//RIGHT SCROLLING
            }
        }
        repaint();
    }

    private void mouseMove(int x, int y) {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int cellHeight = height / CELLROWS;
        int cellWidth = width / CELLCOLUMNS;
        int px = x / cellWidth;
        int py = y / cellHeight;
        if (py == 0) {
            return;
        }
        int cellNumber = getCellNumber(px, py);
        if (cellNumber >= actualMonthStartCell && cellNumber < actualMonthEndCell) {
            mouseOverSelection = new Point(px, py);
        } else {
            mouseOverSelection = null;
        }
        repaint();
    }

    private int getCellNumber(int x, int y) {
        return y * CELLCOLUMNS + x;
    }

    private Point getCellOfDay(int day) {
        Point p;
        int index = day + actualMonthStartCell;
        p = getCellFromIndex(index);
        return p;
    }

    private int getDayFromCell(int x, int y) {
        int index = getCellNumber(x, y);
        if (index >= actualMonthStartCell && index < actualMonthEndCell) {
            return index - actualMonthStartCell + 1;
        }
        return -1;
    }

    public void setDate(Date date) {
        currentDate.setDate(date);
        currentSelectedDate = currentDate.clone();
        mouseDownSelection = null;
        mouseOverSelection = null;
        setDateMark(currentDate);
        repaint();
    }

    public void setDateMark(DateExt date) {
        DateExt temp = date.clone();
        temp.setDay(1);
        int startDateOfWeek = settings.getStartDay();
        int monthStartDateOfWeek = DateUtils.dayOfWeekToCurrent(temp.getDayOfWeek(), startDateOfWeek);
        actualMonthStartCell = monthStartDateOfWeek + CELLCOLUMNS - 1;
        if (monthStartDateOfWeek == 1) {
            actualMonthStartCell = CELLCOLUMNS * 2;
        }

        mouseDownSelection = getCellOfDay(currentSelectedDate.getDay() - 1);
    }

    public void setVisibleDate(Date date) {
        currentDate.setDate(date);
        repaint();
    }

    @Override
    public void draw(Graphics2D graph) {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int cellHeight = height / CELLROWS;
        graph.setColor(CalendarSettings.BACKGROUNDCOLOR);
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int startDateOfWeek = settings.getStartDay();
        DateExt temp = currentDate.clone();
        temp.setDay(1);
        int monthStartDateOfWeek = DateUtils.dayOfWeekToCurrent(temp.getDayOfWeek(), startDateOfWeek);
        actualMonthStartCell = monthStartDateOfWeek + CELLCOLUMNS - 1;
        int x = monthStartDateOfWeek;
        int y = 1;
        if (monthStartDateOfWeek == 1) {
            y++;
            actualMonthStartCell = CELLCOLUMNS * 2;
        }
        int daysInMonth = currentDate.getDaysInMonth();
        actualMonthEndCell = actualMonthStartCell + daysInMonth;
        if(mouseDownSelection!=null){
            int selectedIndex=getIndexOfCell(mouseDownSelection.x, mouseDownSelection.y);
            if(selectedIndex>actualMonthEndCell || selectedIndex<actualMonthStartCell){
                mouseDownSelection=null;
            }
        }
        drawHeader(graph);
        graph.setColor(colorSeparatorLine);
        graph.fillRect(5, cellHeight - 2, width - 10, 2);
        for (int dayCounter = 1; dayCounter <= daysInMonth; dayCounter++) {
            boolean selected = false;
            if (temp.getDate().equals(currentSelectedDate.getDate())) {
                selected = true;
            }
            drawDay(graph, temp, null, x, y, true, selected);
            x++;
            if (x > 7) {
                x = 1;
                y++;
            }
            temp.addDay(1);
        }
        drawPreviousMonth(graph);
        drawNextMonth(graph);
    }

    private void drawPreviousMonth(Graphics2D graph) {
        DateExt prevDate = currentDate.clone();
        prevDate.addMonth(-1);
        prevDate.setDay(prevDate.getDaysInMonth());//set last day in month. ie 28FEB or 31MARCH
        int limit = CELLCOLUMNS;
        for (int i = actualMonthStartCell - 1; i >= limit; i--) {
            Point p = getCellFromIndex(i);
            drawDay(graph, prevDate, null, (int) p.getX() + 1, (int) p.getY(), false, false);
            prevDate.addDay(-1);
        }
    }

    private void drawNextMonth(Graphics2D graph) {
        DateExt nextDate = currentDate.clone();
        nextDate.addMonth(1);
        nextDate.setDay(1);
        int lastCell = CELLCOLUMNS * CELLROWS;
        for (int i = actualMonthEndCell; i < lastCell; i++) {
            Point p = getCellFromIndex(i);
            drawDay(graph, nextDate, null, (int) p.getX() + 1, (int) p.getY(), false, false);
            nextDate.addDay(1);
        }
    }

    private void drawHeader(Graphics2D gr) {
        for (int i = 1; i <= 7; i++) {
            drawDay(gr, null, settings.getDayNameShort(i), i, 0, true, false);
        }
    }

    //draws string into calendar cell
    //you can provide date or text -> date="date", text=null. or date=null, text="text"
    private void drawDay(Graphics2D gr, DateExt date, String text, int x, int y, boolean currentMonth, boolean selected) {
        x--;
        Font lastFont = null;
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int cellWidth = width / CELLCOLUMNS;
        int cellHeight = height / CELLROWS;
        int px = x * cellWidth;
        int py = y * cellHeight;
        if (mouseDownSelection != null && mouseDownSelection.getX() == x && mouseDownSelection.getY() == y) {
            drawHightLightMouseSelected(gr, px, py, cellWidth, cellHeight);
        }
        if (mouseOverSelection != null && mouseOverSelection.getX() == x && mouseOverSelection.getY() == y) {
            drawHightLightMouseHover(gr, px, py, cellWidth, cellHeight);
        }
        if (selected) {
            lastFont = gr.getFont();
            gr.setFont(gr.getFont().deriveFont(Font.BOLD));
            gr.setColor(Color.BLUE);
            //drawHightLight(gr, px, py, cellWidth, cellHeight);
        } else {
            if (currentMonth) {
                gr.setColor(colorDayOfCurrentMonth);
            } else {
                gr.setColor(colorDayOfNotCurrentMonth);
            }
        }

        String string;
        if (date != null) {
            string = String.valueOf(date.getDay());
        } else {
            string = text;
        }
        Dimension stringD = Utils.getStringWidth(gr, string);
        px += cellWidth / 2 - stringD.width / 2;
        py += cellHeight / 2 - stringD.height / 2 - 3;
        py += stringD.height;

        gr.drawString(string, px, py);
        if (selected) {
            gr.setFont(lastFont);
        }
    }

    public DateExt getSelectedDate() {
        if (mouseDownSelection == null) {
            return null;
        }
        int day = getDayFromCell(mouseDownSelection.x, mouseDownSelection.y);
        if (day == -1) {
            return null;
        }
        DateExt date = currentDate.clone();
        date.setDay(day);
        return date;
    }

    private void fireDatePanelEventAction(Integer type) {
        if (event != null) {
            event.fireEvent(type);
        }
    }
    
    private void fireAction() {
        if (action == null) {
            return;
        }
        DateExt date = getSelectedDate();
        action.dateSelected(date);
    }

    public void addDateSelectionAction(DateSelectionAction action) {
        this.action = action;
    }
}
