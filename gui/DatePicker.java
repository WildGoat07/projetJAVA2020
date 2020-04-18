package gui;

import java.util.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

public class DatePicker extends JDialog {
    private Locale language;
    private JSpinner year;
    private JLabel month;
    private JPanel days;
    private LocalDate result;
    public DatePicker(Locale lang) {
        language = lang;
        result = LocalDate.now();
        setSize(350, 200);
        setLayout(new BorderLayout());
        {
            JPanel bar = new JPanel();
            add(bar, BorderLayout.NORTH);
            bar.setLayout(new BorderLayout());
            JButton previous = new JButton("←");
            previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (result.getMonth().getValue() == 1) {
                        result = LocalDate.of(result.getYear()-1, 12, result.getDayOfMonth());
                        year.setValue(result.getYear());
                    }
                    else
                        result = LocalDate.of(result.getYear(), result.getMonth().getValue()-1, result.getDayOfMonth());
                    update();
                    revalidate();
                }
            });
            bar.add(previous, BorderLayout.WEST);
            JButton next = new JButton("→");
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (result.getMonth().getValue() == 12) {
                        result = LocalDate.of(result.getYear()+1, 1, result.getDayOfMonth());
                        year.setValue(result.getYear());
                    }
                    else
                        result = LocalDate.of(result.getYear(), result.getMonth().getValue()+1, result.getDayOfMonth());
                    update();
                    revalidate();
                }
            });
            bar.add(next, BorderLayout.EAST);
            JPanel center = new JPanel();
            center.setLayout(new BorderLayout());
            bar.add(center, BorderLayout.CENTER);
            month = new JLabel();
            month.setBorder(new EmptyBorder(0, 50, 0, 0));
            center.add(month, BorderLayout.CENTER);
            year = new JSpinner(new SpinnerNumberModel(2020, 1970, 2999, 1));
            year.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    result = LocalDate.of((int)year.getValue(), result.getMonth(), result.getDayOfMonth());
                    update();
                    revalidate();
                }
            });
            center.add(year, BorderLayout.EAST);
        }
        days = new JPanel();
        add(days, BorderLayout.CENTER);

        update();
    }
    public DatePicker() {
        this(Locale.getDefault());
    }
    public LocalDate getResult() {
        return result;
    }
    private void update() {
        days.removeAll();
        LocalDate firstDayOfMonth = LocalDate.of(result.getYear(), result.getMonth().getValue(), 1);
        int cases;
        {
            int daysDisplayed = result.lengthOfMonth();
            daysDisplayed += firstDayOfMonth.getDayOfWeek().getValue()-1;
            cases = (1 + (int)Math.ceil(daysDisplayed/7f))*7;
            days.setLayout(new GridLayout(1 + (int)Math.ceil(daysDisplayed/7f), 7));
        }
        for (DayOfWeek day : DayOfWeek.values()) {
            days.add(new JLabel(day.getDisplayName(TextStyle.SHORT, language)));
            cases--;
        }
        for (int i = 1;i<firstDayOfMonth.getDayOfWeek().getValue();i++) {
            days.add(new JLabel());
            cases--;
        }
        for (int i = 0;i<result.lengthOfMonth();i++) {
            JButton currentDay = new JButton(Integer.valueOf(i+1).toString());
            final int I = i;
            final Window itself = this;
            currentDay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = LocalDate.of(result.getYear(), result.getMonth(), I+1);
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }

            });
            days.add(currentDay);
            cases--;
        }
        while (cases > 0) {
            days.add(new JLabel());
            cases--;
        }
        month.setText(DateTimeFormatter.ofPattern("MMMM", language).format(result));
    }
}