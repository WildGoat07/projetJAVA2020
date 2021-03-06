package view;

import java.util.*;
import java.io.*;

import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.*;

/**
 * A date picker is a dialog that let the user select a date
 */
public class DatePicker extends JDialog {
    private static final long serialVersionUID = 1L;
    private Locale language;
    private JSpinner year;
    private JLabel month;
    private JPanel days;
    private LocalDate usedDate;
    private LocalDate result;
    private Window itself;
    /**
     * Constructor
     * @param date starting date to use
     * @param lang language to use
     */
    public DatePicker(LocalDate date, Locale lang, Window caller) {
        super(MainWindow.instance);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        itself = this;
        MainWindow.instance.addNewSubWindow(this);
        addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.instance.removeSubWindow(itself);
            }
            @Override
            public void windowClosed(WindowEvent e) {
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        language = lang;
        usedDate = date;
        setSize(360, 210);
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/cal.png")));
        }
        catch(IOException e){}
        setLayout(new BorderLayout());
        {
            JPanel bar = new JPanel();
            add(bar, BorderLayout.NORTH);
            bar.setLayout(new BorderLayout());
            JButton previous = new JButton("←");
            previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (usedDate.getMonth().getValue() == 1) {
                        usedDate = LocalDate.of(usedDate.getYear()-1, 12, 1);
                        year.setValue(usedDate.getYear());
                    }
                    else
                        usedDate = LocalDate.of(usedDate.getYear(), usedDate.getMonth().getValue()-1, 1);
                    update();
                    revalidate();
                }
            });
            bar.add(previous, BorderLayout.WEST);
            JButton next = new JButton("→");
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (usedDate.getMonth().getValue() == 12) {
                        usedDate = LocalDate.of(usedDate.getYear()+1, 1, 1);
                        year.setValue(usedDate.getYear());
                    }
                    else
                        usedDate = LocalDate.of(usedDate.getYear(), usedDate.getMonth().getValue()+1, 1);
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
            year = new JSpinner(new SpinnerNumberModel(usedDate.getYear(), 1970, 2999, 1));
            year.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    usedDate = LocalDate.of((int)year.getValue(), usedDate.getMonth(), 1);
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
    /**
     * Constructor.
     */
    public DatePicker(Window caller) {
        this(LocalDate.now(), Locale.getDefault(), caller);
    }
    /**
     * Constructor.
     * @param lang the language
     */
    public DatePicker(Locale lang, Window caller) {
        this(LocalDate.now(), lang, caller);
    }
    /**
     * Constructor.
     * @param date the starting date
     */
    public DatePicker(LocalDate date, Window caller) {
        this(date, Locale.getDefault(), caller);
    }
    /**
     * The resulting date.
     * @return the date picked by the user. null if no date has been choosen.
     */
    public LocalDate getResult() {
        return result;
    }
    private void update() {
        days.removeAll();
        LocalDate firstDayOfMonth = LocalDate.of(usedDate.getYear(), usedDate.getMonth().getValue(), 1);
        int cases;
        {
            int daysDisplayed = usedDate.lengthOfMonth();
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
        for (int i = 0;i<usedDate.lengthOfMonth();i++) {
            JButton currentDay = new JButton(Integer.valueOf(i+1).toString());
            final int I = i;
            final Window itself = this;
            currentDay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = LocalDate.of(usedDate.getYear(), usedDate.getMonth(), I+1);
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
        month.setText(DateTimeFormatter.ofPattern("MMMM", language).format(usedDate));
    }
}