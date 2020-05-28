package view;

import model.*;
import controller.*;
import utilities.*;

import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class NewOrder extends JDialog {
    private static final long serialVersionUID = 1L;
    private Order result;
    private LocalDate begDate;

    public NewOrder(final Application app) {
        super(MainWindow.instance);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        final NewOrder itself = this;
        result = null;
        setTitle(app.isCurrentFrench() ? "Nouvelle commande" : "New order");
        MainWindow.instance.addNewSubWindow(this);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(final WindowEvent e) {
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                MainWindow.instance.removeSubWindow(itself);
            }

            @Override
            public void windowClosed(final WindowEvent e) {
            }

            @Override
            public void windowIconified(final WindowEvent e) {
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
            }

            @Override
            public void windowActivated(final WindowEvent e) {
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
            }
        });
        result = null;
        setSize(350, 200);
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        } catch (final IOException e) {
        }
        setLayout(new FlowLayout());
        final JPanel placeholder = new JPanel();
        final JPanel mainPanel1 = new JPanel();
        mainPanel1.setLayout(new BoxLayout(mainPanel1, BoxLayout.Y_AXIS));
        add(placeholder);
        placeholder.add(mainPanel1);
        final JComboBox<Person> persons = new JComboBox<Person>();
        app.getPeople().forEach((p) -> persons.addItem(p));
        mainPanel1.add(Functions.alignHorizontal(
                new Component[] { new JLabel(app.isCurrentFrench() ? "Client :" : "Customer :"), persons }));
        begDate = LocalDate.now();
        JButton addingDate = new JButton(begDate.toString(), new ImageIcon("images/cal.png"));
        JButton resetAddingDate = new JButton(app.isCurrentFrench() ? "Aujourd'hui" : "Today");
        mainPanel1.add(Functions.alignHorizontal(new Component[] { addingDate, resetAddingDate }));
        resetAddingDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begDate = LocalDate.now();
                addingDate.setText(begDate.toString());
                addingDate.revalidate();
            }
        });
        addingDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatePicker picker = new DatePicker(begDate, app.isCurrentFrench() ? Locale.FRENCH : Locale.ENGLISH,
                        itself);
                picker.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        LocalDate date = picker.getResult();
                        if (date != null) {
                            begDate = date;
                            addingDate.setText(begDate.toString());
                            addingDate.revalidate();
                        }
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
                picker.setVisible(true);
            }
        });
        JSpinner reductionPrice = new JSpinner(new SpinnerNumberModel(0, 0, 9999, .1f));
        mainPanel1.add(Functions.alignHorizontal(new Component[] {
                new JLabel(app.isCurrentFrench() ? "Réduction :" : "Reduction :"), reductionPrice, new JLabel(" €") }));
        JButton next = new JButton(app.isCurrentFrench() ? "Suivant" : "Next");
        mainPanel1.add(next);
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel mainPanel2 = new JPanel();
                mainPanel2.setLayout(new BoxLayout(mainPanel2, BoxLayout.Y_AXIS));
                placeholder.removeAll();
                placeholder.add(mainPanel2);
                DefaultListModel<Product> stock = new DefaultListModel<Product>();
                JList<Product> productList = new JList<Product>(stock);
                DefaultListModel<Product> selection = new DefaultListModel<Product>();
                JList<Product> selectedList = new JList<Product>(selection);
                for (Product prod : app.getStock())
                    stock.add(stock.size(), prod);
                Map<Product, Integer> finalProds = new HashMap<Product, Integer>();
                JButton previous = new JButton(app.isCurrentFrench() ? "Précédent" : "Previous");
                previous.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        placeholder.removeAll();
                        placeholder.add(mainPanel1);
                        itself.revalidate();
                        setSize(350, 200);
                    }
                });
                JButton add = new JButton(">");
                add.setEnabled(false);
                JButton remove = new JButton("<");
                remove.setEnabled(false);
                SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 1, 9999, 1);
                JSpinner daysCount = new JSpinner(spinnerModel);
                daysCount.setEnabled(false);
                productList.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (productList.getSelectedValue() != null) {
                            if (app.getProductCountInStock(productList.getSelectedValue(), begDate) > 0)
                                add.setEnabled(true);
                            else
                                add.setEnabled(false);
                            selectedList.clearSelection();
                            remove.setEnabled(false);
                            daysCount.setEnabled(false);
                        }
                        else
                            add.setEnabled(false);
                    }
                });
                selectedList.addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (selectedList.getSelectedValue() != null) {
                            productList.clearSelection();
                            remove.setEnabled(true);
                            daysCount.setEnabled(true);
                            long max = app.getAvailableDayCount(selectedList.getSelectedValue(), begDate);
                            if (max == -1)
                                max = 9999;
                            daysCount.setValue((int)finalProds.get(selectedList.getSelectedValue()));
                            spinnerModel.setMaximum((int)max);
                        }
                        else
                            add.setEnabled(false);
                    }
                });
                add.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Product p = stock.remove(productList.getSelectedIndex());
                        finalProds.put(p, 1);
                        daysCount.setValue(1);
                        selection.add(selection.size(), p);
                        selectedList.setSelectedValue(p, true);
                    }
                });
                remove.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Product p = selection.remove(selectedList.getSelectedIndex());
                        finalProds.remove(p);
                        stock.add(stock.size(), p);
                        remove.setEnabled(false);
                        daysCount.setEnabled(false);
                    }
                });
                daysCount.addChangeListener(new ChangeListener(){
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        finalProds.put(selectedList.getSelectedValue(), (int)daysCount.getValue());
                    }
                });
                mainPanel2.add(Functions.alignHorizontal(new Component[] {
                    new JScrollPane(productList),
                    Functions.alignVertical(new Component[]{add, remove}),
                    new JScrollPane(selectedList)
                }));
                mainPanel2.add(Functions.alignHorizontal(new Component[]{
                    new JLabel(app.isCurrentFrench()?"Jours :":"Days :"),
                    daysCount
                }));
                JButton finish = new JButton(app.isCurrentFrench()?"Terminer":"Finish");
                finish.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (finalProds.containsKey(null))
                            finalProds.remove(null);
                        result = new Order((Person)persons.getSelectedItem(), begDate, new Price((double)reductionPrice.getValue()));
                        for (Map.Entry<Product, Integer> entry : finalProds.entrySet()) {
                            result.addProduct(entry.getKey(), entry.getValue());
                        }
                        itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                    }
                });
                mainPanel2.add(Functions.alignHorizontal(new Component[]{previous, finish}));
                setSize(700, 500);
                itself.revalidate();
            }
        });
    }
    public Order getResult() {
        return result;
    }
}