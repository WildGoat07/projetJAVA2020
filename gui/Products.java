package gui;

import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import data.*;

import javax.swing.border.*;
import javax.swing.event.*;

import utilities.*;

public class Products extends JPanel {
    private Application app;
    private JCheckBox comics;
    private JCheckBox novels;
    private JCheckBox schoolBooks;
    private JCheckBox dicts;
    private JCheckBox dvds;
    private JCheckBox cds;
    private JComboBox<String> productTypes;
    private JPanel productsList;
    private boolean useCurrentTime;
    private LocalDate customDate;
    private JLabel minPrice;
    private JLabel maxPrice;
    private JLabel minStock;
    private JLabel maxStock;
    private JLabel minRented;
    private JLabel maxRented;
    private JSlider minPriceSlider;
    private JSlider maxPriceSlider;
    private JSlider minStockSlider;
    private JSlider maxStockSlider;
    private JSlider minRentedSlider;
    private JSlider maxRentedSlider;

    private Comparator<Product> currentComparator;
    private Comparator<Product> nameComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    };
    private Comparator<Product> priceComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            int res = o1.getPrice(1).compareTo(o2.getPrice(1));
            if (res == 0)
                return nameComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Product> categComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            int res = Functions.getProductType(o1, app.isCurrentFrench()).compareTo(Functions.getProductType(o2, app.isCurrentFrench()));
            if (res == 0)
                return nameComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Product> inStockComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            int res = Integer.valueOf(app.getProductCountInStock(o1)).compareTo(app.getProductCountInStock(o2));
            if (res == 0)
                return nameComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Product> rentedComparator = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            int res = Integer.valueOf(app.getRentedProductCount(o1)).compareTo(app.getRentedProductCount(o2));
            if (res == 0)
                return nameComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Product> reverseNameComparator = nameComparator.reversed();
    private Comparator<Product> reversePriceComparator = priceComparator.reversed();
    private Comparator<Product> reverseCategComparator = categComparator.reversed();
    private Comparator<Product> reverseInStockComparator = inStockComparator.reversed();
    private Comparator<Product> reverseRentedComparator = rentedComparator.reversed();
    private LocalDate getTime() {
        return useCurrentTime?LocalDate.now():customDate;
    }

    public Products(Application app) throws Exception {
        this.app = app;
        setLayout(new BorderLayout());
        currentComparator = nameComparator;
        {
            useCurrentTime = true;
            customDate = LocalDate.now();
            JPanel bar = new JPanel();
            add(bar, BorderLayout.NORTH);
            bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
            productTypes = new JComboBox<String>();
            bar.add(productTypes);
            productTypes.addItem(app.isCurrentFrench()?"Tous les produits":"All products");
            productTypes.addItem(app.isCurrentFrench()?"Produits en stock":"Products in stock");
            productTypes.addItem(app.isCurrentFrench()?"Produits loués":"Rented products");
            productTypes.addItem(app.isCurrentFrench()?"Produits non disponibles":"Unavailable products");
        }
        
        {
            JPanel Filters = new JPanel();
            add(Filters, BorderLayout.WEST);
            Filters.setLayout(new BoxLayout(Filters, BoxLayout.Y_AXIS));
            final JCheckBox docs = new JCheckBox("Documents");
            final JCheckBox books = new JCheckBox(app.isCurrentFrench()?"Livres":"Books");
            final JCheckBox numerics = new JCheckBox(app.isCurrentFrench()?"Numérique":"Numeric");
            Runnable updateLastCheckboxes = () -> {
                books.setSelected(comics.isSelected() && schoolBooks.isSelected() && novels.isSelected());
                numerics.setSelected(cds.isSelected() && dvds.isSelected());
                docs.setSelected(books.isSelected() && dicts.isSelected());
                update();
                revalidate();
            };
            docs.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    books.setSelected(docs.isSelected());
                    dicts.setSelected(docs.isSelected());
                    comics.setSelected(docs.isSelected());
                    novels.setSelected(docs.isSelected());
                    schoolBooks.setSelected(docs.isSelected());
                    update();
                    revalidate();
                }
            });
            books.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    comics.setSelected(books.isSelected());
                    novels.setSelected(books.isSelected());
                    schoolBooks.setSelected(books.isSelected());
                    updateLastCheckboxes.run();
                }
            });
            numerics.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cds.setSelected(numerics.isSelected());
                    dvds.setSelected(numerics.isSelected());
                    update();
                    revalidate();
                }
            });
            docs.setSelected(true);
            numerics.setSelected(true);
            books.setSelected(true);
            Filters.add(docs);
            books.setBorder(new EmptyBorder(0, 20, 0, 0));
            Filters.add(books);
            comics = new JCheckBox(app.isCurrentFrench()?"BD":"Comics");
            comics.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            comics.setSelected(true);
            comics.setBorder(new EmptyBorder(0, 40, 0, 0));
            Filters.add(comics);
            novels = new JCheckBox(app.isCurrentFrench()?"Romans":"Novels");
            novels.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            novels.setSelected(true);
            novels.setBorder(new EmptyBorder(0, 40, 0, 0));
            Filters.add(novels);
            schoolBooks = new JCheckBox(app.isCurrentFrench()?"Livres scolaires":"School books");
            schoolBooks.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            schoolBooks.setSelected(true);
            schoolBooks.setBorder(new EmptyBorder(0, 40, 0, 0));
            Filters.add(schoolBooks);
            dicts = new JCheckBox(app.isCurrentFrench()?"Dictionnaires":"Dictionnaries");
            dicts.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            dicts.setSelected(true);
            dicts.setBorder(new EmptyBorder(0, 20, 0, 0));
            Filters.add(dicts);
            Filters.add(numerics);
            cds = new JCheckBox("CDs");
            cds.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            cds.setSelected(true);
            cds.setBorder(new EmptyBorder(0, 20, 0, 0));
            Filters.add(cds);
            dvds = new JCheckBox("DVDs");
            dvds.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateLastCheckboxes.run();
                }
            });
            dvds.setSelected(true);
            dvds.setBorder(new EmptyBorder(0, 20, 0, 0));
            Filters.add(dvds);

            Filters.add(Box.createRigidArea(new Dimension(1, 20)));
            JCheckBox currentDate = new JCheckBox(app.isCurrentFrench()?"Date actuelle":"Current date");
            currentDate.setSelected(true);
            Filters.add(currentDate);
            JButton changeDate = new JButton(app.isCurrentFrench()?"Changer la date":"Change the date", new ImageIcon("images/cal.png"));
            changeDate.setEnabled(false);
            Filters.add(changeDate);
            JLabel displayDate = new JLabel(customDate.toString());
            displayDate.setEnabled(false);
            Filters.add(displayDate);
            currentDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changeDate.setEnabled(!currentDate.isSelected());
                    displayDate.setEnabled(!currentDate.isSelected());
                    useCurrentTime = currentDate.isSelected();
                    update();
                    revalidate();
                }
            });
            changeDate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DatePicker picker = new DatePicker(customDate, app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
                    picker.setTitle(app.isCurrentFrench()?"Choisir une nouvelle date":"Select a new date");
                    picker.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            LocalDate res = picker.getResult();
                            if (res != null) {
                                customDate = res;
                                displayDate.setText(customDate.toString());
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
            Filters.add(Box.createRigidArea(new Dimension(1, 20)));
            minPrice = new JLabel();
            Filters.add(minPrice);
            minPriceSlider = new JSlider(0, 100, 0);
            minPriceSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(minPriceSlider);
            maxPrice = new JLabel();
            Filters.add(maxPrice);
            maxPriceSlider = new JSlider(0, 100, 100);
            maxPriceSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(maxPriceSlider);
            minStock = new JLabel();
            Filters.add(minStock);
            minStockSlider = new JSlider(0, 100, 0);
            minStockSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(minStockSlider);
            maxStock = new JLabel();
            Filters.add(maxStock);
            maxStockSlider = new JSlider(0, 100, 100);
            maxStockSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(maxStockSlider);
            minRented = new JLabel();
            Filters.add(minRented);
            minRentedSlider = new JSlider(0, 100, 0);
            minRentedSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(minRentedSlider);
            maxRented = new JLabel();
            Filters.add(maxRented);
            maxRentedSlider = new JSlider(0, 100, 100);
            maxRentedSlider.addChangeListener(new ChangeListener(){
                @Override
                public void stateChanged(ChangeEvent e) {
                    update();
                    revalidate();
                }
            });
            Filters.add(maxRentedSlider);
            JButton newProduct = new JButton(app.isCurrentFrench()?"Ajouter un produit":"Add a product");
            newProduct.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NewProduct dialog = new NewProduct(app);
                    dialog.setVisible(true);
                    dialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            Product prod = dialog.getResult();
                            if (prod != null) {
                                try {
                                    app.addProduct(prod, dialog.getQuantity());
                                    update();
                                    revalidate();
                                }
                                catch(Exception exc) {}
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
                }
            });
            Filters.add(newProduct);
        }
        productTypes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                revalidate();
            }
        });

        {
            productsList = new JPanel();
            productsList.setLayout(new BorderLayout());
            add(productsList);
            productsList.add(new JButton());
        }

        update();
    }
    private void update() {
        java.util.List<Product> toDisplay = null;
        switch (productTypes.getSelectedIndex())
        {
            case 0:
                toDisplay = app.getStock();
                break;
            case 1:
                toDisplay = app.getAvailableProducts(getTime());
                break;
            case 2:
                toDisplay = app.getRentedProducts(getTime());
                break;
            case 3:
                toDisplay = app.geUnavailableProducts(getTime());
                break;
        }
        float lowestPrice = Collections.min(Functions.convert(app.getStock(), (p) -> p.getPrice(1).floatValue()));
        float highestPrice = Collections.max(Functions.convert(app.getStock(), (p) -> p.getPrice(1).floatValue()));
        float lowestStock = Collections.min(Functions.convert(app.getStock(), (p) -> (float)app.getProductCountInStock(p)));
        float highestStock = Collections.max(Functions.convert(app.getStock(), (p) -> (float)app.getProductCountInStock(p)));
        float lowestRented = Collections.min(Functions.convert(app.getStock(), (p) -> (float)app.getRentedProductCount(p)));
        float highestRented = Collections.max(Functions.convert(app.getStock(), (p) -> (float)app.getRentedProductCount(p)));
        float choosenLowestPrice = (minPriceSlider.getValue()/100f)*(highestPrice-lowestPrice)+lowestPrice;
        float choosenHighestPrice = (maxPriceSlider.getValue()/100f)*(highestPrice-lowestPrice)+lowestPrice;
        float choosenLowestStock = Math.round((minStockSlider.getValue()/100f)*(highestStock-lowestStock)+lowestStock);
        float choosenHighestStock = Math.round((maxStockSlider.getValue()/100f)*(highestStock-lowestStock)+lowestStock);
        float choosenLowestRented = Math.round((minRentedSlider.getValue()/100f)*(highestRented-lowestRented)+lowestRented);
        float choosenHighestRented = Math.round((maxRentedSlider.getValue()/100f)*(highestRented-lowestRented)+lowestRented);
        minPrice.setText((app.isCurrentFrench()?"Prix minimum : ":"Minimum price : ") + new Price(choosenLowestPrice).toString());
        maxPrice.setText((app.isCurrentFrench()?"Prix maximum : ":"Maximum price : ") + new Price(choosenHighestPrice).toString());
        minStock.setText((app.isCurrentFrench()?"Stock minimum : ":"Minimum stock : ") + Integer.valueOf((int)choosenLowestStock).toString());
        maxStock.setText((app.isCurrentFrench()?"Stock maximum : ":"Maximum stock : ") + Integer.valueOf((int)choosenHighestStock).toString());
        minRented.setText((app.isCurrentFrench()?"Loués minimum : ":"Minimum rented : ") + Integer.valueOf((int)choosenLowestRented).toString());
        maxRented.setText((app.isCurrentFrench()?"Loués maximum : ":"Maximum rented : ") + Integer.valueOf((int)choosenHighestRented).toString());
        toDisplay = Functions.where(toDisplay, (p) -> {
            if (p.getPrice(1).floatValue() < choosenLowestPrice)
                return false;
            if ((float)app.getProductCountInStock(p) < choosenLowestStock)
                return false;
            if ((float)app.getRentedProductCount(p) < choosenLowestRented)
                return false;
            if (p.getPrice(1).floatValue() > choosenHighestPrice)
                return false;
            if ((float)app.getProductCountInStock(p) > choosenHighestStock)
                return false;
            if ((float)app.getRentedProductCount(p) > choosenHighestRented)
                return false;
            if (comics.isSelected() && p instanceof Comic)
                return true;
            if (novels.isSelected() && p instanceof Novel)
                return true;
            if (schoolBooks.isSelected() && p instanceof SchoolBook)
                return true;
            if (dicts.isSelected() && p instanceof data.Dictionary)
                return true;
            if (dvds.isSelected() && p instanceof DVD)
                return true;
            if (cds.isSelected() && p instanceof CD)
                return true;
            return false;
        });
        Collections.sort(toDisplay, currentComparator);
        productsList.removeAll();
        JPanel productData = new JPanel();
        productsList.add(new JScrollPane(productData, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        GridBagConstraints gbc = new GridBagConstraints();
        productData.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 30;
        JButton nameField = new JButton(app.isCurrentFrench()?"Nom":"Name");
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
                update();
                revalidate();
            }
        });
        productData.add(nameField, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        JButton priceField = new JButton(app.isCurrentFrench()?"Prix":"Price");
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
        productData.add(priceField, gbc);
        gbc.gridx++;
        gbc.weightx = 18;
        JButton categField = new JButton(app.isCurrentFrench()?"Catégorie":"Category");
        if (currentComparator == categComparator)
            categField.setText(categField.getText()+" ↑");
        else if (currentComparator == reverseCategComparator)
            categField.setText(categField.getText()+" ↓");
        categField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == categComparator)
                    currentComparator = reverseCategComparator;
                else
                    currentComparator = categComparator;
                update();
                revalidate();
            }
        });    
        productData.add(categField, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        JButton inStockField = new JButton(app.isCurrentFrench()?"En stock":"In stock");
        if (currentComparator == inStockComparator)
            inStockField.setText(inStockField.getText()+" ↑");
        else if (currentComparator == reverseInStockComparator)
            inStockField.setText(inStockField.getText()+" ↓");
        inStockField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentComparator == inStockComparator)
                        currentComparator = reverseInStockComparator;
                    else
                        currentComparator = inStockComparator;
                    update();
                    revalidate();
                }
            });
        productData.add(inStockField, gbc);
        gbc.gridx++;
        JButton rentedField = new JButton(app.isCurrentFrench()?"Loué":"Rented");
        if (currentComparator == rentedComparator)
            rentedField.setText(rentedField.getText()+" ↑");
        else if (currentComparator == reverseRentedComparator)
            rentedField.setText(rentedField.getText()+" ↓");
        rentedField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentComparator == rentedComparator)
                        currentComparator = reverseRentedComparator;
                    else
                        currentComparator = rentedComparator;
                    update();
                    revalidate();
                }
            });
        productData.add(rentedField, gbc);
        gbc.gridy++;
        for (Product product : toDisplay) {
            gbc.gridx = 0;
            gbc.weightx = 30;
            final JLabel productName = new JLabel(product.getTitle());
            productName.setBorder(LineBorder.createGrayLineBorder());
            productData.add(productName, gbc);
            gbc.gridx++;
            gbc.weightx = 1;
            final JLabel productPrice = new JLabel(product.getPrice(1).toString());
            productPrice.setBorder(LineBorder.createGrayLineBorder());
            productData.add(productPrice, gbc);
            gbc.gridx++;
            gbc.weightx = 18;
            final JLabel productCateg = new JLabel(Functions.getProductType(product, app.isCurrentFrench()));
            productCateg.setBorder(LineBorder.createGrayLineBorder());
            productData.add(productCateg, gbc);
            gbc.gridx++;
            gbc.weightx = 1;
            final JLabel productInStock = new JLabel(Integer.valueOf(app.getProductCountInStock(product, getTime())).toString());
            productInStock.setBorder(LineBorder.createGrayLineBorder());
            productData.add(productInStock, gbc);
            gbc.gridx++;
            final JLabel rentedProduct = new JLabel(Integer.valueOf(app.getRentedProductCount(product, getTime())).toString());
            rentedProduct.setBorder(LineBorder.createGrayLineBorder());
            productData.add(rentedProduct, gbc);
            MouseListener mouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    rentedProduct.setOpaque(true);
                    rentedProduct.setBackground(new Color(200, 200, 200));
                    rentedProduct.repaint();
                    productInStock.setOpaque(true);
                    productInStock.setBackground(new Color(200, 200, 200));
                    productInStock.repaint();
                    productCateg.setOpaque(true);
                    productCateg.setBackground(new Color(200, 200, 200));
                    productCateg.repaint();
                    productPrice.setOpaque(true);
                    productPrice.setBackground(new Color(200, 200, 200));
                    productPrice.repaint();
                    productName.setOpaque(true);
                    productName.setBackground(new Color(200, 200, 200));
                    productName.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    rentedProduct.setOpaque(false);
                    rentedProduct.repaint();
                    productInStock.setOpaque(false);
                    productInStock.repaint();
                    productCateg.setOpaque(false);
                    productCateg.repaint();
                    productPrice.setOpaque(false);
                    productPrice.repaint();
                    productName.setOpaque(false);
                    productName.repaint();
                }
            };
            rentedProduct.addMouseListener(mouseListener);
            productInStock.addMouseListener(mouseListener);
            productCateg.addMouseListener(mouseListener);
            productPrice.addMouseListener(mouseListener);
            productName.addMouseListener(mouseListener);
            gbc.gridy++;
        }
        gbc.gridwidth = 5;
        gbc.weighty = 1;
        //dummy
        productData.add(new JLabel(), gbc);
    }
}