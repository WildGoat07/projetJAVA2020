package view;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import utilities.Functions;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Graph<T extends Comparable<T>> extends JComponent {

    public interface Converter<U> {
        float convertToFloat(U value);
        int convertToInt(U value);
        U convert(int value);
    }

    private static final long serialVersionUID = 1L;
    private LocalDate minX;
    private LocalDate maxX;
    private T minY;
    private T maxY;
    private SortedMap<LocalDate, T> map;
    private int mouseX;
    private int mouseY;
    private boolean mouseOnComponent;
    private LocalDate specialDate;
    private static Color black = new Color(30, 30, 30);
    private static Color white = new Color(200, 200, 200);
    private static Color backPanels = new Color(80, 80, 80);
    private int leftSpace;
    private Converter<T> operation;

    private LocalDate getX(float perc) {
        long days = minX.until(maxX, ChronoUnit.DAYS);
        return minX.plusDays((long) (days * perc));
    }

    public int getLeftSpace() {
        return leftSpace;
    }

    public void setLeftSpace(int leftSpace) {
        this.leftSpace = leftSpace;
    }
    public LocalDate getSpecialDate() {
        return specialDate;
    }

    public void setSpecialDate(LocalDate date) {
        specialDate = date;
    }

    private T getY(float perc) {
        long days = minX.until(maxX, ChronoUnit.DAYS);
        Iterator<Map.Entry<LocalDate, T>> iterator = map.entrySet().iterator();
        Map.Entry<LocalDate, T> curr = iterator.next();
        T res = curr.getValue();
        while (minX.plusDays((long)(perc*days)).isAfter(curr.getKey()) ||
        minX.plusDays((long)(perc*days)).isEqual(curr.getKey())) {
            res = curr.getValue();
            curr = iterator.next();
        }
        return res;
    }

    private Graph(Converter<T> op) {
        operation = op;
        leftSpace = 30;
        specialDate = null;
        setForeground(new Color(50, 150, 200));
        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
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

    public Graph(Converter<T> op, Map<LocalDate, T> values) {
        this(op);
        map = new TreeMap<LocalDate, T>(values);
        minY = map.entrySet().iterator().next().getValue();
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        maxY = minY;
        for (Map.Entry<LocalDate, T> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
            if (entry.getValue().compareTo(minY) < 0)
                minY = entry.getValue();
            if (entry.getValue().compareTo(maxY) > 0)
                maxY = entry.getValue();
        }
    }
    public Graph(Converter<T> op, Map<LocalDate, T> values, T miny, T maxy) {
        this(op);
        map = new TreeMap<LocalDate, T>(values);
        minY = miny;
        maxY = maxy;
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        for (Map.Entry<LocalDate, T> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
        }
    }
    public Graph(Converter<T> op, Map<LocalDate, T> values, T miny) {
        this(op);
        map = new TreeMap<LocalDate, T>(values);
        minY = miny;
        maxY = map.entrySet().iterator().next().getValue();
        minX = map.entrySet().iterator().next().getKey();
        maxX = minX;
        for (Map.Entry<LocalDate, T> entry : map.entrySet()) {
            if (entry.getKey().compareTo(minX) < 0)
                minX = entry.getKey();
            if (entry.getKey().compareTo(maxX) > 0)
                maxX = entry.getKey();
            if (entry.getValue().compareTo(maxY) > 0)
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
        int maxYi = (int)Math.ceil(operation.convertToFloat(maxY));
        int minYi = (int)Math.floor(operation.convertToFloat(minY));
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
                step = (maxYi - minYi) / step;
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
            g2.drawLine(leftSpace + (int)(perc*(size.width-leftSpace)), 0, leftSpace + (int)(perc*(size.width-leftSpace)), size.height - 30);
        }
        for (int curr = minYi;curr < maxYi;curr += stepY) {
            float perc = (float)(curr - minYi) / (maxYi - minYi);
            g2.drawLine(leftSpace, (int)(size.height - 30 - perc*(size.height-30)), size.width, (int)(size.height - 30 - perc*(size.height-30)));
        }
        int lastX = leftSpace;
        int lastY;
        {
            float val = operation.convertToFloat(map.entrySet().iterator().next().getValue());
            lastY = size.height - 30 - (int)((val - minYi) / (maxYi - minYi) * (size.height - 30));
        }
        if (specialDate != null) {
            g2.setPaint(Functions.getInverse(getForeground()));
            float perc = minX.until(specialDate, ChronoUnit.DAYS)/(float)days;
            if (perc >= 0 && perc <= 1) {
                int x = leftSpace + (int)(perc*(size.width-leftSpace));
                g2.drawLine(x, 0, x, size.height-30);
            }
        }
        g2.setPaint(white);
        g2.drawLine(leftSpace, size.height - 30, leftSpace, 0);
        g2.drawLine(leftSpace, size.height - 30, size.width, size.height - 30);
        g2.setPaint(getForeground());
        for (Map.Entry<LocalDate, T> entry : map.entrySet()) {
            float percX = (float)minX.until(entry.getKey(), ChronoUnit.DAYS) / days;
            float percY = (operation.convertToFloat(entry.getValue()) - minYi) / (maxYi - minYi);
            int x = leftSpace + (int)((size.width-leftSpace) * percX);
            int y = size.height - 30 - (int)((size.height-30) * percY);
            g2.drawLine(lastX, lastY, x, lastY);
            g2.drawLine(x, lastY, x, y);
            lastX = x;
            lastY = y;
        }
        g2.setPaint(white);
        java.util.List<Float> xSteps = new ArrayList<Float>();
        java.util.List<Float> ySteps = new ArrayList<Float>();
        for (LocalDate curr = minX;curr.compareTo(maxX) < 0;curr = curr.plus(stepX)) {
            float perc = (float)minX.until(curr, ChronoUnit.DAYS) / days;
            xSteps.add(perc);
            g2.drawLine(leftSpace + (int)(perc*(size.width-leftSpace)), size.height - 35, leftSpace + (int)(perc*(size.width-leftSpace)), size.height - 25);
        }
        xSteps.add(1f);
        for (int curr = minYi;curr < maxYi;curr += stepY) {
            float perc = (float)(curr - minYi) / (maxYi - minYi);
            ySteps.add(perc);
            g2.drawLine(leftSpace-5, (int)(size.height - 30 - perc*(size.height-30)), leftSpace+5, (int)(size.height - 30 -perc*(size.height-30)));
        }
        ySteps.add(1f);
        if (mouseOnComponent && mouseX > leftSpace && mouseY < size.height-30) {
            g2.drawLine(mouseX, 0, mouseX, size.height-30);
            g2.drawLine(leftSpace, mouseY, size.width, mouseY);

            if (operation.convertToFloat(getY((mouseX-leftSpace)/(float)(size.width-leftSpace))) <= (maxYi-minYi)/2f) {
                int rectPos = mouseX - leftSpace - 20;
                int arrowPos = mouseX;
                int posY = size.height - 30 - (int)((operation.convertToFloat(getY((mouseX-leftSpace)/(float)(size.width-leftSpace)))-minYi)/(maxYi - minYi)*(size.height-30));
                if (rectPos < leftSpace)
                    rectPos = leftSpace;
                if (rectPos > size.width-100)
                    rectPos = size.width-100;
                if (arrowPos < leftSpace+7)
                    arrowPos = leftSpace+7;
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
                g2.drawString(getX((mouseX-leftSpace)/(float)(size.width-leftSpace)).toString(), rectPos + 10, posY-40);
                g2.drawString(getY((mouseX-leftSpace)/(float)(size.width-leftSpace))+"", rectPos + 40, posY-25);
            }
            else {
                int rectPos = mouseX - leftSpace;
                int arrowPos = mouseX;
                int posY = size.height - 30 - (int)((operation.convertToFloat(getY((mouseX-leftSpace)/(float)(size.width-leftSpace)))-minYi)/(maxYi - minYi)*(size.height-30));
                if (rectPos < leftSpace)
                    rectPos = leftSpace;
                if (rectPos > size.width-100)
                    rectPos = size.width-100;
                if (arrowPos < leftSpace+7)
                    arrowPos = leftSpace+7;
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
                g2.drawString(getX((mouseX-leftSpace)/(float)(size.width-leftSpace)).toString(), rectPos + 10, posY+40);
                g2.drawString(getY((mouseX-leftSpace)/(float)(size.width-leftSpace))+"", rectPos + 40, posY+55);
            }
            Float closestXMatch = 0f;
            Float closestYMatch = 0f;
            float closestDist = 2;
            float currXPerc = (mouseX-leftSpace)/(float)(size.width-leftSpace);
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
                int mousePosX = (int)(closestXMatch*(size.width-leftSpace)+leftSpace);
                int mousePosY = (int)(size.height - 30 - closestYMatch*(size.height-30));
                T value = operation.convert((int)(minYi + (maxYi - minYi)*closestYMatch));
                LocalDate time = minX.plusDays((long)Math.round(days * closestXMatch));
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
                if (offsetX1 < leftSpace+35)
                    offsetX1 = leftSpace+35;
                if (offsetX1 > size.width - 35)
                    offsetX1 = size.width - 35;
                int offsetX2 = mousePosX-10;
                int offsetX3 = mousePosX+10;
                if (offsetX2 < leftSpace)
                    offsetX2 = leftSpace;
                if (offsetX3 > size.width)
                    offsetX3 = size.width;
                g2.fillPolygon(
                    new int[] {5, leftSpace-5, leftSpace-5, leftSpace, leftSpace-5, leftSpace-5, 5},
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