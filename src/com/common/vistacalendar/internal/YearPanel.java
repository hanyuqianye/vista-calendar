package com.common.vistacalendar.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * @author Dmitry Savchenko
 */
public class YearPanel extends AbstractPanel {

    private int startYear = 2010;

    public YearPanel() {
        super(4, 3);
        settings = new CalendarSettings();
        cellsStart = 1;
        cellsEnd = 10;
    }

    public YearPanel(CalendarSettings settings) {
        super(4, 3);
        this.settings = settings;
        cellsStart = 1;
        cellsEnd = 10;
    }

    @Override
    protected void drawCell(Graphics2D graph, int x, int y, Rectangle r) {
        int index = getIndexOfCell(x, y);
        if (index == 0 || index == 11) {
            graph.setColor(Color.GRAY);
        } else {
            graph.setColor(Color.BLACK);
        }
        Utils.drawString(graph, r.x, r.y, r.width, r.height, new String[]{String.valueOf(startYear - 1 + index)});
    }

    public int getSelectedYear() {
        if (mouseDownSelection == null) {
            return -1;
        }
        int index = getIndexOfCell(mouseDownSelection.x, mouseDownSelection.y);
        return startYear + index - 1;
    }

    public void setSelectedYear(int year) {
        int tYear = year / 10;
        startYear = tYear * 10;
    }
}
