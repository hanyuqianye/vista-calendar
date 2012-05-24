package com.common.vistacalendar.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Dmitry Savchenko
 */
public class DecadePanel extends AbstractPanel {

    private int startVisibleDecade = 2000;
    private int selectedYear = 2000;

    public DecadePanel() {
        super(4, 3);
        settings = new CalendarSettings();
        init();
    }

    public DecadePanel(CalendarSettings settings) {
        super(4, 3);
        this.settings = settings;
        init();
    }

    private void init() {
        cellsStart = 1;
        cellsEnd = 10;
        setSelectedDecade(2011);
    }

    @Override
    protected void drawCell(Graphics2D graph, int x, int y, Rectangle r) {
        int index = getIndexOfCell(x, y);
        if (index == 0 || index == 11) {
            graph.setColor(Color.GRAY);
        } else {
            graph.setColor(Color.BLACK);
        }
        int currentDecade = (startVisibleDecade - 10) + (index * 10);
        String[] strings = new String[2];
        strings[0] = String.valueOf(currentDecade);
        strings[1] = String.valueOf(currentDecade + 9);
        Utils.drawString(graph, r.x, r.y, r.width, r.height, strings);
    }

    public void setSelectedDecade(int decade) {
        int tDecade = decade / 100;
        startVisibleDecade = tDecade * 100;
        selectedYear = decade;
        Point p = getCellFromIndex((selectedYear - startVisibleDecade) / 10 + 1);
        if(mouseDownSelection==null){
            mouseDownSelection=new Point();
        }
        mouseDownSelection.x = p.x;
        mouseDownSelection.y = p.y;
    }

    public int getVisibleDecade() {
        return startVisibleDecade;
    }

    public int getSelectedDecade() {
        int index = getIndexOfCell(mouseDownSelection.x, mouseDownSelection.y);
        int currentDecade = (startVisibleDecade - 10) + (index * 10);

        return currentDecade;
    }
}
