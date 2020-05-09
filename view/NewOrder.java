package view;

import model.*;
import controller.*;
import utilities.*;

import java.util.*;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class NewOrder extends JDialog {
    private static final long serialVersionUID = 1L;
    private Order result;
    private LocalDate begDate;
    private LocalDate endDate;
    private Runnable fillPanels;

    public NewOrder(Application app) {
        super(MainWindow.instance);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        NewOrder itself = this;
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
        result = null;
        setSize(350, 200);
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (IOException e) {}
        setLayout(new FlowLayout());
        JPanel placeholder = new JPanel();
        JPanel mainPanel = new JPanel();
        add(placeholder);
        placeholder.add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setTitle(app.isCurrentFrench() ? "Ajouter une commande" : "Add an order");

        JComboBox<Person> person = new JComboBox<Person>();
        {
            java.util.List<Person> people = app.getPeople();
            people.sort(new Comparator<Person>() {
                @Override
                public int compare(Person o1, Person o2) {
                    return Functions.simplify(o1.toString()).compareTo(o2.toString());
                }
            });
            for (Person p : people)
                person.addItem(p);
        }
        mainPanel.add(Functions.alignHorizontal(
                new Component[] { new JLabel(app.isCurrentFrench() ? "Client : " : "Customer : "), person }));
        begDate = LocalDate.now();
        JButton changeBegDate = new JButton(begDate.toString(), new ImageIcon("images/cal.png"));
        JButton todayBeg = new JButton(app.isCurrentFrench() ? "Aujourd'hui" : "Today");
        mainPanel.add(Functions.alignHorizontal(
                new Component[] { new JLabel(app.isCurrentFrench() ? "Date de début : " : "Beginning date : "),
                        changeBegDate, todayBeg }));
        changeBegDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatePicker picker = new DatePicker(app.isCurrentFrench() ? Locale.FRENCH : Locale.ENGLISH, itself);
                picker.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        LocalDate res = picker.getResult();
                        if (res != null) {
                            begDate = res;
                            changeBegDate.setText(begDate.toString());
                            revalidate();
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
        todayBeg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begDate = LocalDate.now();
                changeBegDate.setText(begDate.toString());
                revalidate();
            }
        });
        endDate = LocalDate.now();
        JSpinner daysCount = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        mainPanel.add(Functions.alignHorizontal(new Component[] {
                new JLabel(app.isCurrentFrench() ? "Nombre de jours : " : "Number of days : "), daysCount }));
        JSpinner reduction = new JSpinner(new SpinnerNumberModel(0, 0, 999, .1f));
        mainPanel.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Réduction : ":"Reduction : "),
            reduction,
            new JLabel(" €")
        }));
        JButton next = new JButton(app.isCurrentFrench()?"Suivant":"Next");
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeholder.removeAll();
                JPanel selectionPanel = new JPanel();
                endDate = begDate.plusDays((int)daysCount.getValue());
                placeholder.add(selectionPanel);
                selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
                setSize(450, 450);
                java.util.List<Product> selected = new ArrayList<Product>();
                Comparator<Product> comparator = new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return Functions.simplify(o1.getTitle()).compareTo(Functions.simplify(o2.getTitle()));
                    }
                };
                Supplier<java.util.List<Product>> generateproductList = () -> {
                    java.util.List<Product> result = Functions.except(app.getStock(), selected);
                    result.sort(comparator);
                    return result;
                };
                JPanel productsSelection = new JPanel();
                productsSelection.setLayout(new BoxLayout(productsSelection, BoxLayout.Y_AXIS));
                JPanel selectedProducts = new JPanel();
                selectedProducts.setLayout(new BoxLayout(selectedProducts, BoxLayout.Y_AXIS));
                fillPanels = () -> {
                    productsSelection.removeAll();
                    selectedProducts.removeAll();
                    for (Product product : generateproductList.get()) {
                        JButton addTo = new JButton(app.isCurrentFrench()?"Ajouter":"Add");
                        if (app.getLowestStockProduct(product, begDate, endDate) == 0) {
                            addTo.setEnabled(false);
                            addTo.setToolTipText(app.isCurrentFrench()?"Produit en rupture pour cette commande":"Product out of stock for this order");
                        }
                        productsSelection.add(Functions.alignHorizontal(new Component[] {
                            new JLabel(product.getTitle().length()>20?product.getTitle().subSequence(0, 20)+"...":product.getTitle()),
                            addTo
                        }));
                        addTo.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                selected.add(product);
                                selected.sort(comparator);
                                fillPanels.run();
                            }
                        });
                    }
                    for (Product product : selected) {
                        JButton removeFrom = new JButton(app.isCurrentFrench()?"Retirer":"Remove");
                        selectedProducts.add(Functions.alignHorizontal(new Component[] {
                            new JLabel(product.getTitle().length()>20?product.getTitle().subSequence(0, 20)+"...":product.getTitle()),
                            removeFrom
                        }));
                        removeFrom.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                selected.remove(product);
                                fillPanels.run();
                            }
                        });
                    }
                    placeholder.revalidate();
                    placeholder.repaint();
                };
                fillPanels.run();
                JScrollPane scrollySelection = new JScrollPane(productsSelection, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollySelection.getVerticalScrollBar().setUnitIncrement(16);
                scrollySelection.setPreferredSize(new Dimension(200, 300));
                JScrollPane scrollySelected = new JScrollPane(selectedProducts, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollySelected.getVerticalScrollBar().setUnitIncrement(16);
                scrollySelected.setPreferredSize(new Dimension(200, 300));
                selectionPanel.add(Functions.alignHorizontal(new Component[] {
                    Functions.alignVertical(new Component[] {
                        new JLabel("Stock"),
                        scrollySelection
                    }),
                    Functions.alignVertical(new Component[] {
                        new JLabel(app.isCurrentFrench()?"Produits sélectionnés":"Selected products"),
                        scrollySelected
                    })
                }));
                revalidate();
                JButton previous = new JButton(app.isCurrentFrench()?"Précédent":"Previous");
                previous.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSize(350, 180);
                        placeholder.removeAll();
                        placeholder.add(mainPanel);
                        placeholder.revalidate();
                        placeholder.repaint();
                    }
                });
                JButton finish = new JButton(app.isCurrentFrench()?"Passer la commande":"Finish the order");
                finish.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        result = new Order((Person)person.getSelectedItem(), begDate, endDate, new Price(-(double)reduction.getValue()));
                        selected.forEach((p) -> result.addProduct(p));
                        itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                    }
                });
                selectionPanel.add(Functions.alignHorizontal(new Component[] {
                    previous,
                    finish
                }));
            }
        });
        mainPanel.add(next);
    }
    public Order getResult() {
        return result;
    }
}