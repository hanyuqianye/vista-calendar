package com.common.vistacalendar.internal;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * @author Dmitry Savchenko
 */
public class AbstractPanel extends JPanel {

    protected int CELLROWS = 0;
    protected int CELLCOLUMNS = 0;
    protected CalendarSettings settings = null;
    protected Color selectionColor = new Color(117, 200, 200);
    protected int cellsStart = -1;
    protected int cellsEnd = -1;
    protected DatePanelEvent event = null;
    protected Point mouseDownSelection = null;
    protected Point mouseOverSelection = null;
    private BufferedImage picture=null;

    public AbstractPanel() {
    }

    public AbstractPanel(int cellColumns, int cellRows) {
        Dimension d = new Dimension(180, 120);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        setBackground(CalendarSettings.BACKGROUNDCOLOR);
        CELLROWS = cellRows;
        CELLCOLUMNS = cellColumns;
        addMouseHandler();
        cellsStart = 0;
        cellsEnd = CELLROWS * CELLCOLUMNS;
        mouseDownSelection = new Point(1, 1);
    }

    private void addMouseHandler() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOverSelection = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMove(e.getX(), e.getY());
            }
        });
    }

    protected int getIndexOfCell(int x, int y) {
        return x + y * CELLCOLUMNS;
    }

    public Point getCellFromIndex(int index) {
        int y = index / CELLCOLUMNS;
        int x = index - y * CELLCOLUMNS;
        return new Point(x, y);
    }

    public void draw(Graphics2D gr) {
        Rectangle rect = new Rectangle();
        int cellWidth = getPreferredSize().width / CELLCOLUMNS;
        int cellHeight = getPreferredSize().height / CELLROWS;
        rect.setSize(cellWidth, cellHeight);
        for (int y = 0; y < CELLROWS; y++) {
            for (int x = 0; x < CELLCOLUMNS; x++) {
                rect.setLocation(x * cellWidth, y * cellHeight);
                if (mouseDownSelection != null && mouseDownSelection.x == x && mouseDownSelection.y == y) {
                    drawHightLightMouseSelected(gr, x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                } 
                    if (mouseOverSelection != null && mouseOverSelection.x == x && mouseOverSelection.y == y) {
                        drawHightLightMouseHover(gr, x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                    }
                
                drawCell(gr, x, y, rect);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graph = (Graphics2D) g;
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        draw(graph);
    }

    protected void drawHightLightMouseHover(Graphics2D gr, int x, int y, int width, int height) {
        Color lastColor = gr.getColor();
        Color color = new Color(108, 192, 255);
        Color color2 = new Color(80, 180, 255);
        GradientPaint gradient = new GradientPaint(0, 0, color, width, height, color2, true);
        gr.setPaint(gradient);
        gr.fillRoundRect(x, y, width - 1, height - 1, 5, 5);
        gr.setPaint(null);
        gr.setColor(lastColor);
    }

    protected void drawHightLightMouseSelected(Graphics2D gr, int x, int y, int width, int height) {
        Color lastColor = gr.getColor();
        gr.setColor(selectionColor);
        gr.drawRoundRect(x, y, width - 1, height - 1, 5, 5);
        gr.setColor(lastColor);
    }

    private void mouseDown(int x, int y) {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int cellHeight = height / CELLROWS;
        int cellWidth = width / CELLCOLUMNS;
        int px = x / cellWidth;
        int py = y / cellHeight;
        int cellNumber = getIndexOfCell(px, py);
        if (cellNumber >= cellsStart && cellNumber <= cellsEnd) {
            mouseDownSelection = new Point(px, py);
            fireAction();
        } else {
            if(cellNumber<cellsStart){
                fireAction(2);
            }else if(cellNumber>cellsEnd){
                fireAction(3);
            }
            mouseDownSelection = null;
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
        int cellNumber = getIndexOfCell(px, py);
        if (cellNumber >= cellsStart && cellNumber <= cellsEnd) {
            mouseOverSelection = new Point(px, py);
        } else {
            mouseOverSelection = null;
        }

        repaint();
    }

    public Rectangle getSelectedRectangle() {
        if (mouseDownSelection == null) {
            return null;
        }

        Rectangle rect = new Rectangle();
        int cellWidth = getPreferredSize().width / CELLCOLUMNS;
        int cellHeight = getPreferredSize().height / CELLROWS;
        rect.setSize(cellWidth, cellHeight);
        rect.setLocation(mouseDownSelection.x * cellWidth, mouseDownSelection.y * cellHeight);
        return rect;
    }

    private void fireAction() {
        if (event != null) {
            event.fireEvent(null);
        }
    }
    
    private void fireAction(Integer type) {
        if (event != null) {
            event.fireEvent(type);
        }
    }

    /*should be overrided in the inherited classes*/
    protected void drawCell(Graphics2D graph, int x, int y, Rectangle r) {
    }

    public void addPanelEvent(DatePanelEvent event) {
        this.event = event;
    }

    public CalendarSettings getSettings() {
        return settings;
    }

    public BufferedImage drawPanelToImage() {
        if(picture==null){
            picture = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D graph= (Graphics2D) picture.getGraphics();
        graph.setColor(CalendarSettings.BACKGROUNDCOLOR);
        graph.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
        draw(graph);
        graph.dispose();
        
        return picture;
    }
}
