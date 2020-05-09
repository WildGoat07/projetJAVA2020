package view;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ColorUIResource;

import utilities.Functions;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Graph extends JComponent {

    private static final long serialVersionUID = 1L;
    private LocalDate minX;
    private LocalDate maxX;
    private int minY;
    private int maxY;
    private SortedMap<LocalDate, Integer> map;
    private int mouseX;
    private int mouseY;
    private boolean mouseOnComponent;
    private LocalDate specialDate;
    private static Color black = new Color(30, 30, 30);
    private static Color white = new Color(200, 200, 200);
    private static Color backPanels = new Color(80, 80, 80);

    private LocalDate getX(float perc) {
        long days = minX.until(maxX, ChronoUnit.DAYS);
        return minX.plusDays((long)(days*perc));
    }
    private int getY(float perc) {
        long days = minX.until(maxX, ChronoUnit.DAYS);
        Iterator<Map.Entry<LocalDate, Integer>> iterator = map.entrySet().iterator();
        Map.Entry<LocalDate, Integer> curr = iterator.next();
        int res = curr.getValue();
        while (minX.plusDays((long)(perc*days)).isAfter(curr.getKey()) ||
        minX.plusDays((long)(perc*days)).isEqual(curr.getKey())) {
            res = curr.getValue();
            curr = iterator.next();
        }
        return res;
    }

    private Graph() {
        setForeground(new Color(50, 150, 200));
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
        addMouseListener(new MouseInputListener(){
            @Override
            public void mouseMoved(MouseEvent e) {
            }
            @Override
            public void mouseDragged(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
                mouseOnComponent = false;
                repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOnComponent = true;
            }
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
        mouseOnComponent = false;
    }

    public Graph(Map<LocalDate, Integer> values) {
        this(values, null);
    }
    public Graph(Map<LocalDate, Integer> values, LocalDate special) {
        this();
        specialDate = special;
        map = new TreeMap<LocalDate, Integer>(values);
        minY = map.entrySet().iterator().next().getValue();
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        maxY = minY;
        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
            if (entry.getValue() < minY)
                minY = entry.getValue();
            if (entry.getValue() > maxY)
                maxY = entry.getValue();
        }
    }
    public Graph(Map<LocalDate, Integer> values, int miny, int maxy) {
        this(values, miny, maxy, null);
    }
    public Graph(Map<LocalDate, Integer> values, int miny, int maxy, LocalDate special) {
        this();
        specialDate = special;
        map = new TreeMap<LocalDate, Integer>(values);
        minY = miny;
        maxY = maxy;
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
        }
    }
    public Graph(Map<LocalDate, Integer> values, int miny) {
        this(values, miny, null);
    }
    public Graph(Map<LocalDate, Integer> values, int miny, LocalDate special) {
        this();
        specialDate = special;
        map = new TreeMap<LocalDate, Integer>(values);
        minY = miny;
        maxY = map.entrySet().iterator().next().getValue();
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
            if (entry.getValue() > maxY)
                maxY = entry.getValue();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension size = getSize();
        g2.setPaint(black);
        g2.fillRect(0, 0, size.width, size.height);
        long days = minX.until(maxX, ChronoUnit.DAYS);
        Period stepX;
        {
            long step = size.width / 50;
            if (step == 0)
                stepX = Period.ofDays(1);
            else {
                step = days / step;
                if (step == 0)
                    stepX = Period.ofDays(1);
                else
                    stepX = Period.ofDays((int)step);
            }
        }
        int stepY;
        {
            float step = size.height / 50f;
            if (step != 0) {
                step = (maxY - minY) / step;
                if (step < 1)
                    stepY = 1;
                else
                    stepY = (int)step;
            }
            else
                stepY = 1;
        }
        g2.setPaint(Color.GRAY);
        for (LocalDate curr = minX;curr.compareTo(maxX) < 0;curr = curr.plus(stepX)) {
            float perc = (float)minX.until(curr, ChronoUnit.DAYS) / days;
            g2.drawLine(30 + (int)(perc*(size.width-30)), 0, 30 + (int)(perc*(size.width-30)), size.height - 30);
        }
        for (int curr = minY;curr < maxY;curr += stepY) {
            float perc = (float)(curr - minY) / (maxY - minY);
            g2.drawLine(30, (int)(size.height - 30 - perc*(size.height-30)), size.width, (int)(size.height - 30 - perc*(size.height-30)));
        }
        int lastX = 30;
        int lastY;
        {
            int val = map.entrySet().iterator().next().getValue();
            lastY = size.height - 30 - (int)((float)(val - minY) / (maxY - minY) * (size.height - 30));
        }
        if (specialDate != null) {
            g2.setPaint(Functions.getInverse(getForeground()));
            float perc = minX.until(specialDate, ChronoUnit.DAYS)/(float)days;
            if (perc >= 0 && perc <= 1) {
                int x = 30 + (int)(perc*(size.width-30));
                g2.drawLine(x, 0, x, size.height-30);
            }
        }
        g2.setPaint(getForeground());
        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            float percX = (float)minX.until(entry.getKey(), ChronoUnit.DAYS) / days;
            float percY = (float)(entry.getValue() - minY) / (maxY - minY);
            int x = 30 + (int)((size.width-30) * percX);
            int y = size.height - 30 - (int)((size.height-30) * percY);
            g2.drawLine(lastX, lastY, x, lastY);
            g2.drawLine(x, lastY, x, y);
            lastX = x;
            lastY = y;
        }
        g2.setPaint(white);
        g2.drawLine(30, size.height - 30, 30, 0);
        g2.drawLine(30, size.height - 30, size.width, size.height - 30);
        java.util.List<Float> xSteps = new ArrayList<Float>();
        java.util.List<Float> ySteps = new ArrayList<Float>();
        for (LocalDate curr = minX;curr.compareTo(maxX) < 0;curr = curr.plus(stepX)) {
            float perc = (float)minX.until(curr, ChronoUnit.DAYS) / days;
            xSteps.add(perc);
            g2.drawLine(30 + (int)(perc*(size.width-30)), size.height - 35, 30 + (int)(perc*(size.width-30)), size.height - 25);
        }
        xSteps.add(1f);
        for (int curr = minY;curr < maxY;curr += stepY) {
            float perc = (float)(curr - minY) / (maxY - minY);
            ySteps.add(perc);
            g2.drawLine(25, (int)(size.height - 30 - perc*(size.height-30)), 35, (int)(size.height - 30 -perc*(size.height-30)));
        }
        ySteps.add(1f);
        if (mouseOnComponent && mouseX > 30 && mouseY < size.height-30) {
            g2.drawLine(mouseX, 0, mouseX, size.height-30);
            g2.drawLine(30, mouseY, size.width, mouseY);

            if (getY((mouseX-30)/(float)(size.width-30)) <= (maxY-minY)/2f) {
                int rectPos = mouseX - 50;
                int arrowPos = mouseX;
                int posY = size.height - 30 - (int)((getY((mouseX-30)/(float)(size.width-30))-minY)/(float)(maxY - minY)*(size.height-30));
                if (rectPos < 30)
                    rectPos = 30;
                if (rectPos > size.width-100)
                    rectPos = size.width-100;
                if (arrowPos < 37)
                    arrowPos = 37;
                if (arrowPos > size.width-7)
                    arrowPos = size.width-7;
                g2.setPaint(backPanels);
                g2.fillRect(rectPos, posY - 65, 100, 50);
                g2.setPaint(white);
                g2.drawRect(rectPos, posY - 65, 100, 50);
                g2.setPaint(backPanels);
                g2.fillPolygon(
                    new int[] { arrowPos, arrowPos+7, arrowPos-7 },
                    new int[] { posY-5, posY-15, posY-15 },
                    3
                );
                g2.setPaint(white);
                g2.drawLine(arrowPos, posY-5, arrowPos+7, posY-15);
                g2.drawLine(arrowPos, posY-5, arrowPos-7, posY-15);
                g2.drawString(getX((mouseX-30)/(float)(size.width-30)).toString(), rectPos + 10, posY-40);
                g2.drawString(getY((mouseX-30)/(float)(size.width-30))+"", rectPos + 40, posY-25);
            }
            else {
                int rectPos = mouseX - 50;
                int arrowPos = mouseX;
                int posY = size.height - 30 - (int)((getY((mouseX-30)/(float)(size.width-30))-minY)/(float)(maxY - minY)*(size.height-30));
                if (rectPos < 30)
                    rectPos = 30;
                if (rectPos > size.width-100)
                    rectPos = size.width-100;
                if (arrowPos < 37)
                    arrowPos = 37;
                if (arrowPos > size.width-7)
                    arrowPos = size.width-7;
                g2.setPaint(backPanels);
                g2.fillRect(rectPos, posY + 15, 100, 50);
                g2.setPaint(white);
                g2.drawRect(rectPos, posY + 15, 100, 50);
                g2.setPaint(backPanels);
                g2.fillPolygon(
                    new int[] { arrowPos, arrowPos+7, arrowPos-7 },
                    new int[] { posY+6, posY+16, posY+16 },
                    3
                );
                g2.setPaint(white);
                g2.drawLine(arrowPos, posY+6, arrowPos+7, posY+15);
                g2.drawLine(arrowPos, posY+6, arrowPos-7, posY+15);
                g2.drawString(getX((mouseX-30)/(float)(size.width-30)).toString(), rectPos + 10, posY+40);
                g2.drawString(getY((mouseX-30)/(float)(size.width-30))+"", rectPos + 40, posY+55);
            }
            if (mouseOnComponent) {
                Float closestXMatch = 0f;
                Float closestYMatch = 0f;
                float closestDist = 2;
                float currXPerc = (mouseX-30)/(float)(size.width-30);
                float currYPerc = (size.height - 30 - mouseY)/(float)(size.height-30);
                for (Float step : xSteps) {
                    if (Math.abs(step - currXPerc) < closestDist) {
                        closestDist = Math.abs(step - currXPerc);
                        closestXMatch = step;
                    }
                }
                closestDist = 2;
                for (Float step : ySteps) {
                    if (Math.abs(step - currYPerc) < closestDist) {
                        closestDist = Math.abs(step - currYPerc);
                        closestYMatch = step;
                    }
                }
                {
                    int mousePosX = (int)(closestXMatch*(size.width-30)+30);
                    int mousePosY = (int)(size.height - 30 - closestYMatch*(size.height-30));
                    int value = (int)(minY + (maxY - minY)*closestYMatch);
                    LocalDate time = minX.plusDays((long)(days * closestXMatch));
                    g2.setPaint(backPanels);
                    int offsetY1 = mousePosY;
                    if (offsetY1 < 10)
                        offsetY1 = 10;
                    if (offsetY1 > size.height - 40)
                        offsetY1 = size.height - 40;
                    int offsetY2 = mousePosY-10;
                    int offsetY3 = mousePosY+10;
                    if (offsetY2 < 0)
                        offsetY2 = 0;
                    if (offsetY3 > size.height - 30)
                        offsetY3 = size.height - 30;

                    int offsetX1 = mousePosX;
                    if (offsetX1 < 65)
                        offsetX1 = 65;
                    if (offsetX1 > size.width - 35)
                        offsetX1 = size.width - 35;
                    int offsetX2 = mousePosX-10;
                    int offsetX3 = mousePosX+10;
                    if (offsetX2 < 30)
                        offsetX2 = 30;
                    if (offsetX3 > size.width)
                        offsetX3 = size.width;
                    g2.fillPolygon(
                        new int[] {5, 25, 25, 30, 25, 25, 5},
                        new int[] {offsetY1 - 10, offsetY1 - 10, offsetY2, mousePosY, offsetY3, offsetY1 + 10, offsetY1+10},
                        7
                    );
                    g2.fillPolygon(
                        new int[] {offsetX1-35, offsetX2, mousePosX, offsetX3, offsetX1+35, offsetX1+35, offsetX1-35},
                        new int[] {size.height-25, size.height-25, size.height-30, size.height-25, size.height-25, size.height - 5, size.height - 5},
                        7
                    );
                    g2.setPaint(white);
                    g2.drawString(value+"", 7, offsetY1+4);
                    g2.drawString(time.toString(), offsetX1-31, size.height-10);
                }
            }
        }
    }
}