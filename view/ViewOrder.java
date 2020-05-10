package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import model.*;
import utilities.*;
import controller.Application;

public class ViewOrder extends JDialog {
    private static final long serialVersionUID = 1L;
    private Runnable update;
    private Comparator<Map.Entry<Product, Price>> currentComparator;
    public ViewOrder(Application app, Order o) {
        super(MainWindow.instance);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        Window itself = this;
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
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        setLayout(new FlowLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(500, 550);
        setLocationRelativeTo(MainWindow.instance);
        setTitle(app.isCurrentFrench()?"Commande":"Order");
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (IOException e) {}
        JButton customer = new JButton(o.getCustomer().toString());
        customer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewPerson(app, o.getCustomer(), itself).setVisible(true);
            }
        });
        mainPanel.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Client : ":"Customer : "),
            customer
        }));
        Long duration = Long.valueOf(o.getBeginningRental().until(o.getEndingRental(), ChronoUnit.DAYS));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Date de départ : ":"Beginning date : ")+o.getBeginningRental().toString()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Date de fin : ":"Ending date : ")+o.getEndingRental().toString()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Durée : ":"Duration : ")+
                    duration.toString() +
                    (app.isCurrentFrench()?" jours":" days")));
        mainPanel.add(new JLabel("ID : "+o.getID().toString()));
        JPanel productList = new JPanel();
        productList.setLayout(new GridBagLayout());
        {
            Comparator<Map.Entry<Product, Price>> nameComparator = new Comparator<Map.Entry<Product, Price>>() {
                @Override
                public int compare(Map.Entry<Product, Price> o1, Map.Entry<Product, Price> o2) {
                    return Functions.simplify(o1.getKey().getTitle()).compareTo(Functions.simplify(o2.getKey().getTitle()));
                }
            };
            Comparator<Map.Entry<Product, Price>> priceComparator = new Comparator<Map.Entry<Product, Price>>() {
                @Override
                public int compare(Map.Entry<Product, Price> o1, Map.Entry<Product, Price> o2) {
                    int res = o1.getValue().compareTo(o2.getValue());
                    if (res == 0)
                        return nameComparator.compare(o1, o2);
                    else
                        return res;
                }
            };
            Comparator<Map.Entry<Product, Price>> reverseNameComparator = nameComparator.reversed();
            Comparator<Map.Entry<Product, Price>> reversePriceComparator = priceComparator.reversed();
            currentComparator = nameComparator;
            update = () -> {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1;
                productList.removeAll();
                JButton nameField = new JButton(app.isCurrentFrench()?"Nom":"Name");
                productList.add(nameField, gbc);
                if (currentComparator == nameComparator)
                    nameField.setText(nameField.getText()+" ↑");
                else if (currentComparator == reverseNameComparator)
                    nameField.setText(nameField.getText()+" ↓");
                nameField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentComparator == nameComparator)
                            currentComparator = reverseNameComparator;
                        else
                            currentComparator = nameComparator;
                        update.run();
                        productList.revalidate();
                    }
                });
                gbc.gridx++;
                gbc.weightx = 0;
                JButton priceField = new JButton(app.isCurrentFrench()?"Prix":"Price");
                productList.add(priceField, gbc);
                if (currentComparator == priceComparator)
                    priceField.setText(priceField.getText()+" ↑");
                else if (currentComparator == reversePriceComparator)
                    priceField.setText(priceField.getText()+" ↓");
                priceField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentComparator == priceComparator)
                            currentComparator = reversePriceComparator;
                        else
                            currentComparator = priceComparator;
                        update.run();
                        productList.revalidate();
                    }
                });
                gbc.gridy++;
                java.util.List<Map.Entry<Product, Price>> toDisplay = Functions.convert(o.getPrices().entrySet(), (item) -> item);
                toDisplay.sort(currentComparator);
                for (Map.Entry<Product, Price> prod : toDisplay) {
                    gbc.gridx = 0;
                    gbc.weightx = 1;
                    final JLabel productName = new JLabel(prod.getKey().getTitle());
                    productName.setBorder(new LineBorder(MainWindow.borders, 1));
                    productList.add(productName, gbc);
                    gbc.gridx++;
                    gbc.weightx = 0;
                    final JLabel productPrice = new JLabel(prod.getValue().toString());
                    productPrice.setBorder(new LineBorder(MainWindow.borders, 1));
                    productList.add(productPrice, gbc);
                    MouseListener mouseListener = new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            new ViewProduct(app, prod.getKey(), itself).setVisible(true);
                        }
                        @Override
                        public void mousePressed(MouseEvent e) {
                        }
                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            productName.setOpaque(true);
                            productName.setBackground(MainWindow.itemHovered);
                            productName.repaint();
                            productPrice.setOpaque(true);
                            productPrice.setBackground(MainWindow.itemHovered);
                            productPrice.repaint();
                        }
        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            productName.setOpaque(false);
                            productName.repaint();
                            productPrice.setOpaque(false);
                            productPrice.repaint();
                        }
                    };
                    productName.addMouseListener(mouseListener);
                    productPrice.addMouseListener(mouseListener);
                    gbc.gridy++;
                }
                gbc.gridwidth = 2;
                gbc.weighty = 1;
                //dummy filling component
                productList.add(new JLabel(), gbc);
                    };
            update.run();
        }
        mainPanel.add(new JLabel(app.isCurrentFrench()?"Produits :":"Products :"));
        JScrollPane scrollyBoi = new JScrollPane(productList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollyBoi.getVerticalScrollBar().setUnitIncrement(16);
        scrollyBoi.setPreferredSize(new Dimension(400, 280));
        mainPanel.add(scrollyBoi);
        Price total = new Price();
        for (Product prod : o.getProducts())
            total.add(prod.getPrice(duration, o.getBeginningRental()));
        mainPanel.add(new JLabel(
            (app.isCurrentFrench()?"Total produits : ":"Total products : ") +
            total.toString()
        ));
        if (o.getCustomer().isLoyal()) {
            JLabel reduc = new JLabel(
                (app.isCurrentFrench()?"Client fidèle : ":"Loyal customer : ")+
                Price.multiply(-.1f, total).toString()+
                " (-10%)"
            );
            reduc.setForeground(Color.red);
            mainPanel.add(reduc);
            total.multiply(.9f);
            mainPanel.add(new JLabel("Total : "+total.toString()));
        }
        if (o.getReduction().compareTo(new Price()) != 0) {
            JLabel reduc = new JLabel(
                (app.isCurrentFrench()?"Réduction : ":"Reduction : ")+
                o.getReduction().toString()
            );
            reduc.setForeground(Color.red);
            mainPanel.add(reduc);
            total.add(o.getReduction());
            if (total.compareTo(new Price()) < 0)
                total = new Price();
            mainPanel.add(new JLabel("Total : "+total.toString()));
        }
        JButton delete = new JButton(app.isCurrentFrench()?"Supprimer":"Delete", new ImageIcon("images/trash.png"));
        mainPanel.add(delete);
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    app.removeOrder(o);
                    MainWindow.addChange(new Change(){
                        @Override
                        public void undo() {
                                app.addOrder(o);
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                        }
                        @Override
                        public void redo() {
                            app.removeOrder(o);
                            MainWindow.instance.orders.update();
                            MainWindow.instance.orders.revalidate();
                    }
                    });
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
            }
        });
    }
}