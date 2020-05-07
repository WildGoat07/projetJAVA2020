package view;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import model.*;
import controller.*;

import javax.swing.border.*;
import javax.swing.event.*;

import utilities.*;

public class Orders extends JPanel implements CanUpdate {
    private static final long serialVersionUID = 1L;

    private Application app;

    private JPanel ordersList;
    private JPanel customerPanel;
    private JPanel productPanel;
    private HashMap<Person, JCheckBox> customerList;
    private HashMap<Product, JCheckBox> productList;
    private JComboBox<String> orderTypes;
    private LocalDate currentDate;
    private LocalDate minDate;
    private LocalDate maxDate;
    private JLabel displayMinDate;
    private JLabel displayMaxDate;
    private boolean autoChangeMinDate;
    private boolean autoChangeMaxDate;
    private JLabel minCount;
    private JLabel maxCount;
    private JLabel minPrice;
    private JLabel maxPrice;
    private JSlider minCountSlider;
    private JSlider maxCountSlider;
    private JSlider minPriceSlider;
    private JSlider maxPriceSlider;
    public JButton newOrder;

    private Comparator<Order> currentComparator;
    private static String getSignature(Person p) {
        return Functions.simplify(p.getName()+p.getSurname());
    }
    private Comparator<Order> beginComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            int res = o1.getBeginningRental().compareTo(o2.getBeginningRental());
            if (res == 0)
                return o1.getEndingRental().compareTo(o2.getEndingRental());
            else
                return res;
        }
    };
    private Comparator<Order> endComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            int res = o1.getEndingRental().compareTo(o2.getEndingRental());
            if (res == 0)
                return o1.getBeginningRental().compareTo(o2.getBeginningRental());
            else
                return res;
        }
    };
    private Comparator<Order> countComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            int res = Integer.valueOf(o1.getProducts().size()).compareTo(o2.getProducts().size());
            if (res == 0)
                return beginComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Order> priceComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            int res = o1.getCost().compareTo(o2.getCost());
            if (res == 0)
                return beginComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Order> customerComparator = new Comparator<Order>() {
        @Override
        public int compare(Order o1, Order o2) {
            int res = getSignature(o1.getCustomer()).compareTo(getSignature(o2.getCustomer()));
            if (res == 0)
                return beginComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Order> reverseBeginComparator = beginComparator.reversed();
    private Comparator<Order> reverseEndComparator = endComparator.reversed();
    private Comparator<Order> reverseCountComparator = countComparator.reversed();
    private Comparator<Order> reversePriceComparator = priceComparator.reversed();
    private Comparator<Order> reverseCustomerComparator = customerComparator.reversed();



    public Orders(Application app) throws Exception {
        this.app = app;
        setLayout(new BorderLayout());
        currentComparator = beginComparator;
        {
            currentDate = LocalDate.now();
            orderTypes = new JComboBox<String>();
            orderTypes.addItem(app.isCurrentFrench()?"Toutes les commandes":"All orders");
            orderTypes.addItem(app.isCurrentFrench()?"Commandes actives":"Active orders");
            orderTypes.addItem(app.isCurrentFrench()?"Commandes inactives":"Inactive orders");
            JButton changeDate = new JButton(currentDate.toString(), new ImageIcon("images/cal.png"));
            changeDate.setEnabled(false);
            JButton resetDate = new JButton(app.isCurrentFrench()?"Réinitialiser la date":"Reset the date");
            resetDate.setEnabled(false);
            resetDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentDate = LocalDate.now();
                    changeDate.setText(currentDate.toString());
                    update();
                    revalidate();
                }
            });
            changeDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DatePicker picker = new DatePicker(currentDate, app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
                    picker.setTitle(app.isCurrentFrench()?"Choisir une nouvelle date":"Select a new date");
                    picker.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            LocalDate res = picker.getResult();
                            if (res != null) {
                                currentDate = res;
                                changeDate.setText(currentDate.toString());
                                update();
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
            add(Functions.alignHorizontal(new Component[] {
                orderTypes,
                changeDate,
                resetDate
            }), BorderLayout.NORTH);
            orderTypes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changeDate.setEnabled(orderTypes.getSelectedIndex() > 0);
                    resetDate.setEnabled(orderTypes.getSelectedIndex() > 0);
                    update();
                    revalidate();
                }
            });
        }
        {
            JPanel filters = new JPanel();
            add(filters, BorderLayout.WEST);
            filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));
            autoChangeMaxDate = true;
            autoChangeMinDate = true;
            if (app.getOrders().size() > 0) {
                minDate = Collections.min(Functions.convert(app.getOrders(), (o) -> o.getBeginningRental()));
                maxDate = Collections.max(Functions.convert(app.getOrders(), (o) -> o.getEndingRental()));
            }
            else {
                minDate = LocalDate.now();
                maxDate = minDate;
            }
        displayMinDate = new JLabel((app.isCurrentFrench()?"Date minimum : ":"Minimum date : ")+minDate.toString());
            displayMaxDate = new JLabel((app.isCurrentFrench()?"Date maximum : ":"Maximum date : ")+maxDate.toString());
            JButton changeMinDate = new JButton(app.isCurrentFrench()?"Changer":"Change", new ImageIcon("images/cal.png"));
            JButton changeMaxDate = new JButton(app.isCurrentFrench()?"Changer":"Change", new ImageIcon("images/cal.png"));
            JButton resetMinDate = new JButton(app.isCurrentFrench()?"Réinitialiser":"Reset");
            JButton resetMaxDate = new JButton(app.isCurrentFrench()?"Réinitialiser":"Reset");
            changeMinDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DatePicker picker = new DatePicker(minDate, app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
                    picker.setTitle(app.isCurrentFrench()?"Choisir une nouvelle date":"Select a new date");
                    picker.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            LocalDate res = picker.getResult();
                            if (res != null) {
                                minDate = res;
                                autoChangeMinDate = false;
                                update();
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
            changeMaxDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DatePicker picker = new DatePicker(maxDate, app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
                    picker.setTitle(app.isCurrentFrench()?"Choisir une nouvelle date":"Select a new date");
                    picker.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            LocalDate res = picker.getResult();
                            if (res != null) {
                                maxDate = res;
                                autoChangeMaxDate = false;
                                update();
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
            resetMinDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    autoChangeMinDate = true;
                    update();
                    revalidate();
    }
            });
            resetMaxDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    autoChangeMaxDate = true;
                    update();
                    revalidate();
    }
            });
            filters.add(displayMinDate);
            filters.add(Functions.alignHorizontal(new Component[] {
                changeMinDate,
                resetMinDate
            }));
            filters.add(displayMaxDate);
            filters.add(Functions.alignHorizontal(new Component[] {
                changeMaxDate,
                resetMaxDate
            }));
            {
                customerPanel = new JPanel();
                customerPanel.setLayout(new BoxLayout(customerPanel, BoxLayout.Y_AXIS));
                customerList = new HashMap<Person, JCheckBox>();
                JButton selectAll = new JButton(app.isCurrentFrench()?"Tous":"All");
                selectAll.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : customerList.values())
                            checkBox.setSelected(true);
                        update();
                        revalidate();
                    }
                });
                JButton selectNone = new JButton(app.isCurrentFrench()?"Aucun":"None");
                selectNone.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : customerList.values())
                            checkBox.setSelected(false);
                        update();
                        revalidate();
                    }
                });
                filters.add(new JLabel(app.isCurrentFrench()?"Clients :":"Customers :"));
                filters.add(Functions.alignHorizontal(new Component[] {
                    selectAll,
                    selectNone
                }));
                JScrollPane scrollyBoi = new JScrollPane(customerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollyBoi.getVerticalScrollBar().setUnitIncrement(16);
                scrollyBoi.setPreferredSize(new Dimension(0, 100));
                filters.add(scrollyBoi);
            }
            {
                productPanel = new JPanel();
                productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
                productList = new HashMap<Product, JCheckBox>();
                JButton selectAll = new JButton(app.isCurrentFrench()?"Tous":"All");
                selectAll.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : productList.values())
                            checkBox.setSelected(true);
                        update();
                        revalidate();
                    }
                });
                JButton selectNone = new JButton(app.isCurrentFrench()?"Aucun":"None");
                selectNone.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JCheckBox checkBox : productList.values())
                            checkBox.setSelected(false);
                        update();
                        revalidate();
                    }
                });
                filters.add(new JLabel(app.isCurrentFrench()?"Produits :":"Products :"));
                filters.add(Functions.alignHorizontal(new Component[] {
                    selectAll,
                    selectNone
                }));
                JScrollPane scrollyBoi = new JScrollPane(productPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollyBoi.getVerticalScrollBar().setUnitIncrement(16);
                scrollyBoi.setPreferredSize(new Dimension(0, 100));
                filters.add(scrollyBoi);
            }
            {
                minCount = new JLabel();
                filters.add(minCount);
                minCountSlider = new JSlider(0, 100, 0);
                minCountSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        update();
                        revalidate();
                    }
                });
                filters.add(minCountSlider);
                maxCount = new JLabel();
                filters.add(maxCount);
                maxCountSlider = new JSlider(0, 100, 100);
                maxCountSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        update();
                        revalidate();
                    }
                });
                filters.add(maxCountSlider);
                minPrice = new JLabel();
                filters.add(minPrice);
                minPriceSlider = new JSlider(0, 100, 0);
                minPriceSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        update();
                        revalidate();
                    }
                });
                filters.add(minPriceSlider);
                maxPrice = new JLabel();
                filters.add(maxPrice);
                maxPriceSlider = new JSlider(0, 100, 100);
                maxPriceSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        update();
                        revalidate();
                    }
                });
                filters.add(maxPriceSlider);
            }
            newOrder = new JButton(app.isCurrentFrench()?"Nouvelle commande":"New order");
            newOrder.setIcon(new ImageIcon("images/add-order.png"));
            newOrder.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NewOrder dialog = new NewOrder(app);
                    dialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            Order order = dialog.getResult();
                            if (order != null) {
                                try {
                                    app.addOrder(order);
                                    update();
                                    revalidate();
                                    MainWindow.addChange(new Change() {
                                        @Override
                                        public void undo() {
                                            app.removeOrder(order);
                                            update();
                                            revalidate();
                                        }
                                        @Override
                                        public void redo() {
                                            try {
                                                app.addOrder(order);
                                                update();
                                                revalidate();
                                            }
                                            catch(Exception exc){}
                                        }
                                    });
                                }
                                catch (Exception xc){}
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
                    dialog.setVisible(true);
                }
            });
            filters.add(newOrder);
        }
        ordersList = new JPanel();
        ordersList.setLayout(new BorderLayout());
        add(ordersList);

        update();
    }
    @Override
    public void update() {
        if (autoChangeMinDate && app.getOrders().size() > 0)
            minDate = Collections.min(Functions.convert(app.getOrders(), (o) -> o.getBeginningRental()));
        if (autoChangeMaxDate && app.getOrders().size() > 0)
            maxDate = Collections.max(Functions.convert(app.getOrders(), (o) -> o.getEndingRental()));
        displayMinDate.setText((app.isCurrentFrench()?"Date minimum : ":"Minimum date : ")+minDate.toString());
        displayMaxDate.setText((app.isCurrentFrench()?"Date maximum : ":"Maximum date : ")+maxDate.toString());
        java.util.List<Order> toDisplay = app.getOrders();
        switch(orderTypes.getSelectedIndex()) {
            case 1:
                toDisplay = Functions.where(toDisplay, (o) -> {
                    return (o.getBeginningRental().isBefore(currentDate) ||
                            o.getBeginningRental().isEqual(currentDate)) &&
                            o.getEndingRental().isAfter(currentDate);
                });
                break;
            case 2:
                toDisplay = Functions.where(toDisplay, (o) -> {
                    return o.getBeginningRental().isAfter(currentDate) ||
                            o.getEndingRental().isEqual(currentDate) ||
                            o.getEndingRental().isBefore(currentDate);
                });
                break;
        }
        toDisplay = Functions.where(toDisplay, (o) -> {
            return (o.getEndingRental().isAfter(minDate) ||
                    o.getEndingRental().isEqual(minDate)) &&
                    (o.getBeginningRental().isBefore(maxDate) ||
                    o.getBeginningRental().isEqual(maxDate));
        });
        if (app.getOrders().size() > 0) {
            float lowestPrice = Collections.min(Functions.convert(app.getOrders(), (o) -> o.getCost().floatValue()));
            float highestPrice = Collections.max(Functions.convert(app.getOrders(), (o) -> o.getCost().floatValue()));
            float lowestCount = Collections.min(Functions.convert(app.getOrders(), (o) -> (float)o.getProducts().size()));
            float highestCount = Collections.max(Functions.convert(app.getOrders(), (o) -> (float)o.getProducts().size()));
            float choosenLowestPrice = (minPriceSlider.getValue()/100f)*(highestPrice-lowestPrice)+lowestPrice;
            float choosenHighestPrice = (maxPriceSlider.getValue()/100f)*(highestPrice-lowestPrice)+lowestPrice;
            float choosenLowestCount = Math.round((minCountSlider.getValue()/100f)*(highestCount-lowestCount)+lowestCount);
            float choosenHighestCount = Math.round((maxCountSlider.getValue()/100f)*(highestCount-lowestCount)+lowestCount);
            minPrice.setText((app.isCurrentFrench()?"Montant minimum : ":"Minimum amount : ") + new Price(choosenLowestPrice).toString());
            maxPrice.setText((app.isCurrentFrench()?"Montant maximum : ":"Maximum amount : ") + new Price(choosenHighestPrice).toString());
            minCount.setText((app.isCurrentFrench()?"Quantité minimum : ":"Minimum quantity : ") + Integer.valueOf((int)choosenLowestCount).toString());
            maxCount.setText((app.isCurrentFrench()?"Quantité maximum : ":"Maximum quantity : ") + Integer.valueOf((int)choosenHighestCount).toString());
            toDisplay = Functions.where(toDisplay, (o) -> {
                if (o.getCost().floatValue() < choosenLowestPrice)
                    return false;
                if (o.getCost().floatValue() > choosenHighestPrice)
                    return false;
                if ((float)o.getProducts().size() < choosenLowestCount)
                    return false;
                if ((float)o.getProducts().size() > choosenHighestCount)
                    return false;
                return true;
            });
        }
        {
            customerPanel.removeAll();
            productPanel.removeAll();
            java.util.List<Person> people = app.getPeople();
            people.sort(new Comparator<Person>() {
                @Override
                public int compare(Person o1, Person o2) {
                    return Functions.simplify(o1.getName()+o1.getSurname()).compareTo(Functions.simplify(o2.getName()+o2.getSurname()));
                }
            });
            for (Person person : people) {
                if (!customerList.containsKey(person)) {
                    JCheckBox checkbox = new JCheckBox(person.getName()+" "+person.getSurname());
                    checkbox.setSelected(true);
                    customerList.put(person, checkbox);
                    checkbox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            update();
                            revalidate();
                        }
                    });
                }
                customerPanel.add(customerList.get(person));
            }
            java.util.List<Product> prods = app.getStock();
            prods.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return Functions.simplify(o1.getTitle()).compareTo(o2.getTitle());
                }
            });
            for (Product prod: prods) {
                if (!productList.containsKey(prod)) {
                    JCheckBox checkbox = new JCheckBox(prod.getTitle());
                    checkbox.setSelected(true);
                    productList.put(prod, checkbox);
                    checkbox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            update();
                            revalidate();
                        }
                    });
                }
                productPanel.add(productList.get(prod));
            }
        }
        toDisplay = Functions.where(toDisplay, (o) -> {
            if (customerList.get(o.getCustomer()).isSelected()) {
                boolean display = false;
                for (Product product : o.getProducts())
                    if (productList.get(product).isSelected()) {
                        display = true;
                        break;
                    }
                return display;
            }
            else
                return false;
        });
        Collections.sort(toDisplay, currentComparator);
        ordersList.removeAll();
        JPanel orderData = new JPanel();
        JScrollPane scrollyBoi = new JScrollPane(orderData, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollyBoi.getVerticalScrollBar().setUnitIncrement(16);
        ordersList.add(scrollyBoi);
        GridBagConstraints gbc = new GridBagConstraints();
        orderData.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 5;
        JButton nameField = new JButton(app.isCurrentFrench()?"Nom du client":"Customer's name");
        if (currentComparator == customerComparator)
            nameField.setText(nameField.getText()+" ↑");
        else if (currentComparator == reverseCustomerComparator)
            nameField.setText(nameField.getText()+" ↓");
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == customerComparator)
                    currentComparator = reverseCustomerComparator;
                else
                    currentComparator = customerComparator;
                update();
                revalidate();
            }
        });
        orderData.add(nameField, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        JButton begDateField = new JButton(app.isCurrentFrench()?"Date de départ":"Beginning date");
        if (currentComparator == beginComparator)
            begDateField.setText(begDateField.getText()+" ↑");
        else if (currentComparator == reverseBeginComparator)
            begDateField.setText(begDateField.getText()+" ↓");
        begDateField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == beginComparator)
                    currentComparator = reverseBeginComparator;
                else
                    currentComparator = beginComparator;
                update();
                revalidate();
            }
        });
        orderData.add(begDateField, gbc);
        gbc.gridx++;
        JButton endDateField = new JButton(app.isCurrentFrench()?"Date de fin":"Ending date");
        if (currentComparator == endComparator)
            endDateField.setText(endDateField.getText()+" ↑");
        else if (currentComparator == reverseEndComparator)
            endDateField.setText(endDateField.getText()+" ↓");
        endDateField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == endComparator)
                    currentComparator = reverseEndComparator;
                else
                    currentComparator = endComparator;
                update();
                revalidate();
            }
        });
        orderData.add(endDateField, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        JButton priceField = new JButton(app.isCurrentFrench()?"Montant":"Amount");
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
                update();
                revalidate();
            }
        });
        orderData.add(priceField, gbc);
        gbc.gridx++;
        JButton countField = new JButton(app.isCurrentFrench()?"Quantité":"Quantity");
        if (currentComparator == countComparator)
            countField.setText(countField.getText()+" ↑");
        else if (currentComparator == reverseCountComparator)
            countField.setText(countField.getText()+" ↓");
        countField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == countComparator)
                    currentComparator = reverseCountComparator;
                else
                    currentComparator = countComparator;
                update();
                revalidate();
            }
        });
        orderData.add(countField, gbc);
        gbc.gridy++;
        for (Order order : toDisplay) {
            gbc.gridx = 0;
            gbc.weightx = 5;
            JLabel orderName = new JLabel(order.getCustomer().getName()+" "+order.getCustomer().getSurname());
            orderName.setBorder(LineBorder.createGrayLineBorder());
            orderData.add(orderName, gbc);
            gbc.gridx++;
            gbc.weightx = 1;
            JLabel orderbeg = new JLabel(order.getBeginningRental().toString());
            orderbeg.setBorder(LineBorder.createGrayLineBorder());
            orderData.add(orderbeg, gbc);
            gbc.gridx++;
            JLabel orderend = new JLabel(order.getEndingRental().toString());
            orderend.setBorder(LineBorder.createGrayLineBorder());
            orderData.add(orderend, gbc);
            gbc.gridx++;
            gbc.weightx = 0;
            JLabel orderPrice = new JLabel(order.getCost().toString());
            orderPrice.setBorder(LineBorder.createGrayLineBorder());
            orderData.add(orderPrice, gbc);
            gbc.gridx++;
            JLabel orderCount = new JLabel(Integer.valueOf(order.getProducts().size()).toString());
            orderCount.setBorder(LineBorder.createGrayLineBorder());
            orderData.add(orderCount, gbc);
            MouseListener mouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ViewOrder dialog = new ViewOrder(app, order);
                    dialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            update();
                            revalidate();
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
                    dialog.setVisible(true);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    orderName.setOpaque(true);
                    orderName.setBackground(new Color(200, 200, 200));
                    orderName.repaint();
                    orderbeg.setOpaque(true);
                    orderbeg.setBackground(new Color(200, 200, 200));
                    orderbeg.repaint();
                    orderend.setOpaque(true);
                    orderend.setBackground(new Color(200, 200, 200));
                    orderend.repaint();
                    orderPrice.setOpaque(true);
                    orderPrice.setBackground(new Color(200, 200, 200));
                    orderPrice.repaint();
                    orderCount.setOpaque(true);
                    orderCount.setBackground(new Color(200, 200, 200));
                    orderCount.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    orderName.setOpaque(false);
                    orderName.repaint();
                    orderbeg.setOpaque(false);
                    orderbeg.repaint();
                    orderend.setOpaque(false);
                    orderend.repaint();
                    orderPrice.setOpaque(false);
                    orderPrice.repaint();
                    orderCount.setOpaque(false);
                    orderCount.repaint();
                }
            };
            orderName.addMouseListener(mouseListener);
            orderbeg.addMouseListener(mouseListener);
            orderend.addMouseListener(mouseListener);
            orderPrice.addMouseListener(mouseListener);
            orderCount.addMouseListener(mouseListener);
            gbc.gridy++;
        }
        gbc.gridwidth = 5;
        gbc.weighty = 1;
        //dummy filling component
        orderData.add(new JLabel(), gbc);
    }
}