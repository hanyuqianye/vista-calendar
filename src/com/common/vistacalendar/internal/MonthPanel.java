package com.common.vistacalendar.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Dmitry Savchenko
 */
public class MonthPanel extends AbstractPanel {

    public MonthPanel() {
        super(4, 3);
        settings = new CalendarSettings();
    }

    public MonthPanel(CalendarSettings settings) {
        super(4, 3);
        this.settings = settings;
    }

    @Override
    protected void drawCell(Graphics2D graph, int x, int y, Rectangle r) {
        Color lastColor = graph.getColor();
        int index = getIndexOfCell(x, y);
        String monthName = settings.getShortMonthName(index);
        graph.setColor(Color.BLACK);
        Utils.drawString(graph, r.x, r.y, r.width, r.height, new String[]{monthName});
        graph.setColor(lastColor);
    }

    public int getSelectedMonth() {
        if (mouseDownSelection == null) {
            return -1;
        }
        int index = getIndexOfCell(mouseDownSelection.x, mouseDownSelection.y);
        return index;
    }

    public void setSelectedMonth(int month) {
        Point p = getCellFromIndex(month);
        mouseDownSelection.x = p.x;
        mouseDownSelection.y = p.y;
    }
}