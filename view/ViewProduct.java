package view;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import model.*;
import utilities.Functions;
import utilities.Price;
import controller.Application;

public class ViewProduct extends JDialog {

    private static final long serialVersionUID = 1L;

    public ViewProduct(Application app, Product p, Window caller) {
        super(caller);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        final Window itself = this;
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
        setLayout(new FlowLayout());
        add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(450, 300);
        setLocationRelativeTo(MainWindow.instance);
        mainPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        setTitle(p.getTitle());
        JLabel img = new JLabel();
        try {
            InputStream stream = p.getImage();
            if (stream != null) {
                BufferedImage thumbnail = ImageIO.read(stream);
                int finalX = 200, finalY = 200;
                if (thumbnail.getWidth() > thumbnail.getHeight()) {
                    float ratio = (float)thumbnail.getWidth()/thumbnail.getHeight();
                    finalY = (int)(finalX / ratio);
                }
                else if (thumbnail.getHeight() > thumbnail.getWidth()) {
                    float ratio = (float)thumbnail.getWidth()/thumbnail.getHeight();
                    finalX = (int)(finalY * ratio);
                }
                img.setIcon(new ImageIcon(Functions.resizeImage(finalX, finalY, thumbnail)));
                stream.reset();
                setIconImage(Functions.resizeImage(16, 16, thumbnail));
                stream.close();
                setSize(450, 300+finalY);
            }
            else
                setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (IOException e) {}
        mainPanel.add(img);
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Catégorie : ":"Category : ")+Functions.getProductType(p, app.isCurrentFrench())));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Nom : ":"Name : ")+p.getTitle()));
        mainPanel.add(new JLabel("ID : "+p.getID().toString()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Prix pour un jour (aujourd'hui) : ":"Price for one day (today) : ")+p.getPrice(1, LocalDate.now()).toString()));
        Component specificCompo = null;
        if (p instanceof Book) {
            Book prod = (Book)p;
            specificCompo = new JLabel((app.isCurrentFrench()?"Auteur : ":"Author : ")+prod.getAuthor());
        }
        else if (p instanceof model.Dictionary) {
            model.Dictionary prod = (model.Dictionary)p;
            specificCompo = new JLabel((app.isCurrentFrench()?"Langue : ":"Language : ")+prod.getLanguage().getDisplayLanguage(app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH));
        }
        else if (p instanceof CD) {
            CD prod = (CD)p;
            specificCompo = new JLabel((app.isCurrentFrench()?"Date de sortie : ":"Release date : ")+prod.getReleaseDate());
        }
        else if (p instanceof DVD) {
            DVD prod = (DVD)p;
            specificCompo = new JLabel((app.isCurrentFrench()?"Réalisateur : ":"Director : ")+prod.getDirector());
        }
        mainPanel.add(specificCompo);
        mainPanel.add(new JLabel((app.isCurrentFrench()?"En stock (aujourd'hui) : ":"In stock (today) : ")+app.getProductCountInStock(p)+"/"+app.getRegisteredProductCount(p, LocalDate.now())));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Loués (aujourd'hui) : ":"Rented (today) : ")+app.getRentedProductCount(p)+"/"+app.getRegisteredProductCount(p, LocalDate.now())));
        JButton removeNbProducts = new JButton(app.isCurrentFrench()?"Retirer une quantité de produits":"Remove a quantity of products", new ImageIcon("images/trash.png"));
        mainPanel.add(removeNbProducts);
        JButton viewPrice = new JButton(app.isCurrentFrench()?"Voir le graphe des prix":"Show the price graph");
        JButton viewStock = new JButton(app.isCurrentFrench()?"Voir le graphe du stock":"Show the stock graph");
        viewStock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(itself, app.isCurrentFrench()?"Graphe du stock":"Stock graph");
                dialog.setSize(900, 600);
                dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
                MainWindow.instance.addNewSubWindow(dialog);
                dialog.setLocationRelativeTo(itself);
                dialog.addWindowListener(new WindowListener(){
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }
                    @Override
                    public void windowClosing(WindowEvent e) {
                        MainWindow.instance.removeSubWindow(dialog);
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
                ArrayList<ProductIO> data = new ArrayList<ProductIO>();
                for (Order order : app.getOrders())
                    if (order.getProducts().contains(p)) {
                        data.add(new ProductIO(order.getBeginningRental(), false));
                        data.add(new ProductIO(order.getEndOfBorrowing(p), true));
                    }
                for (Map.Entry<LocalDate, Integer> input : app.getProductInput(p).entrySet())
                    data.add(new ProductIO(input.getKey(), true, input.getValue()));

                Collections.sort(data);
                Map<LocalDate, Integer> stock = new HashMap<LocalDate, Integer>();
                int currStock = 0;
                for (ProductIO productIO : data) {
                    currStock += (productIO.isInput?1:-1)*productIO.count;
                    stock.put(productIO.time, currStock);
                }
                LocalDate min = Collections.min(stock.keySet());
                LocalDate max = Collections.max(stock.keySet());
                long days = min.until(max, ChronoUnit.DAYS);
                Integer maxValue = (int)(Collections.max(stock.values())*1.1f);
                if (maxValue == Collections.max(stock.values()))
                    maxValue++;
                int lastValue;
                {
                    java.util.List<Integer> values = Functions.convert(stock.values(), (item)->item);
                    lastValue = values.get(values.size()-1);
                }
                stock.put(min.minusDays((long)Math.max(1, days*.15f)), 0);
                stock.put(max.plusDays((long)Math.max(1, days*.15f)), lastValue);
                Graph<Integer> graph = new Graph<Integer>(new Graph.Converter<Integer>() {
    
                    @Override
                    public int convertToInt(Integer value) {
                        return value;
                    }
    
                    @Override
                    public Integer convert(int value) {
                        return value;
                    }
    
                    @Override
                    public float convertToFloat(Integer value) {
                        return value;
                    }
                },stock, 0, maxValue);
                graph.setSpecialDate(LocalDate.now());
                graph.setLeftSpace(40);
                dialog.add(graph);
                dialog.setVisible(true);
            }
        });
        viewPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(itself, app.isCurrentFrench()?"Graphe des prix":"Price graph");
                dialog.setSize(900, 600);
                dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
                MainWindow.instance.addNewSubWindow(dialog);
                dialog.setLocationRelativeTo(itself);
                dialog.addWindowListener(new WindowListener(){
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }
                    @Override
                    public void windowClosing(WindowEvent e) {
                        MainWindow.instance.removeSubWindow(dialog);
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
                Map<LocalDate, Price> prices = p.getHistory();
                if (prices.size() > 0) {
                    LocalDate min = Collections.min(prices.keySet());
                    LocalDate max = Collections.max(prices.keySet());
                    long days = min.until(max, ChronoUnit.DAYS);
                    Price maxValue = new Price(Collections.max(prices.values()));
                    {
                        if (Price.multiply(maxValue, 1.1f).intValue() == maxValue.intValue())
                            maxValue.add(new Price(1));
                        else
                            maxValue.multiply(1.1f);
                    }
                    prices.put(min.minusDays(Math.max(1, (long)(days*.15f))), p.getInitialPrice());
                    prices.put(max.plusDays(Math.max(1, (long)(days*.15f))), p.getPrice(1, max.plusDays(1)));
                    Graph<Price> graph = new Graph<Price>(new Graph.Converter<Price>() {
        
                        @Override
                        public int convertToInt(Price value) {
                            return Price.multiply(value, 4).intValue();
                        }
        
                        @Override
                        public Price convert(int value) {
                            return new Price(value/4f);
                        }
        
                        @Override
                        public float convertToFloat(Price value) {
                            return Price.multiply(value, 4).floatValue();
                        }
                    },prices, new Price(), maxValue);
                    graph.setSpecialDate(LocalDate.now());
                    graph.setLeftSpace(60);
                    dialog.add(graph);
                }
                else {
                    LocalDate min = LocalDate.now().minusDays(1);
                    LocalDate max = LocalDate.now().plusDays(1);
                    long days = min.until(max, ChronoUnit.DAYS);
                    Price maxValue = p.getInitialPrice();
                    {
                        if (Price.multiply(maxValue, 1.1f).intValue() == maxValue.intValue())
                            maxValue.add(new Price(1));
                        else
                            maxValue.multiply(1.1f);
                    }
                    prices.put(min.minusDays((long)((days*.15f))), p.getInitialPrice());
                    prices.put(max.plusDays((long)((days*.15f))), p.getInitialPrice());
                    Graph<Price> graph = new Graph<Price>(new Graph.Converter<Price>() {
        
                        @Override
                        public int convertToInt(Price value) {
                            return Price.multiply(value, 4).intValue();
                        }
        
                        @Override
                        public Price convert(int value) {
                            return new Price(value/4f);
                        }
        
                        @Override
                        public float convertToFloat(Price value) {
                            return Price.multiply(value, 4).floatValue();
                        }
                    },prices, new Price(), maxValue);
                    graph.setSpecialDate(LocalDate.now());
                    graph.setLeftSpace(60);
                    dialog.add(graph);
                }
                dialog.setVisible(true);
            }
        });
        mainPanel.add(Functions.alignHorizontal(new Component[]{viewPrice, viewStock}));
        JButton editProduct = new JButton(app.isCurrentFrench()?"Éditer le produit":"Edit the product");
        mainPanel.add(editProduct);
        editProduct.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                new EditProduct(app, p, caller).setVisible(true);
            }
        });
        JButton deleteProduct = new JButton(app.isCurrentFrench()?"Supprimer le produit":"Delete the product",  new ImageIcon("images/trash.png"));
        mainPanel.add(deleteProduct);
        if (!app.canRemoveProduct(p)) {
            deleteProduct.setEnabled(false);
            deleteProduct.setToolTipText(app.isCurrentFrench()?"Ce produit est mentioné dans une commande":"This product is mentioned in an order");
        }
        removeNbProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    DatePicker picker = new DatePicker(Locale.getDefault(), itself);
                    picker.setTitle(app.isCurrentFrench()?"Quand retirer ces produits ?":"When to remove these products ?");
                    picker.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            picker.setVisible(false);
                            LocalDate date = picker.getResult();
                            if (date != null) {
                                JDialog counter = new JDialog(itself, app.isCurrentFrench()?"Combien en retirer ?":"How much to remove ?");
                                JPanel counterPanel = new JPanel();
                                counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.Y_AXIS));
                                counter.add(counterPanel);
                                counter.setLocationRelativeTo(itself);
                                counter.setSize(200, 100);
                                counter.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
                                JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, Math.max(app.getLowestStockProduct(p, date), 1), 1));
                                counterPanel.add(quantity);
                                JButton removeTheseThings = new JButton(app.isCurrentFrench()?"Retirer":"Remove", removeNbProducts.getIcon());
                                counterPanel.add(removeTheseThings);
                                if (app.getLowestStockProduct(p, date) == 0) {
                                    quantity.setValue(0);
                                    quantity.setEnabled(false);
                                    removeTheseThings.setEnabled(false);
                                    removeTheseThings.setToolTipText(app.isCurrentFrench()?"Le stock est déjà à zéro à cette date":"The stock is already at zero at this date");
                                    quantity.setToolTipText(app.isCurrentFrench()?"Le stock est déjà à zéro à cette date":"The stock is already at zero at this date");
                                }
                                removeTheseThings.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        int toRemove = (int)quantity.getValue();
                                        app.removeProduct(p, toRemove, date);
                                        MainWindow.addChange(new Change(){
                                            @Override
                                            public void undo() {
                                                app.addProduct(p, toRemove, date);
                                                MainWindow.instance.products.update();
                                                MainWindow.instance.products.revalidate();
                                            }
                                            @Override
                                            public void redo() {
                                                    app.removeProduct(p, toRemove, date);
                                                    MainWindow.instance.products.update();
                                                    MainWindow.instance.products.revalidate();
                                            }
                                        });
                                        itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                                    }
                                });
                                counter.setVisible(true);
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
        deleteProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    Map<LocalDate, Integer> history = app.getProductInput(p);
                    app.removeProduct(p);
                    MainWindow.addChange(new Change(){
                        @Override
                        public void undo() {
                            for (Map.Entry<LocalDate, Integer> entry : history.entrySet())
                                if (entry.getValue() > 0)
                                    app.addProduct(p, entry.getValue(), entry.getKey());
                                else
                                    app.removeProduct(p, -entry.getValue(), entry.getKey());
                                MainWindow.instance.products.update();
                            MainWindow.instance.products.revalidate();
                        }
                        @Override
                        public void redo() {
                                app.removeProduct(p);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                        }
                    });
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
            }
        });
    }
    private class ProductIO implements Comparable<ProductIO> {
        public LocalDate time;
        public boolean isInput;
        public int count;
        public ProductIO(LocalDate t, boolean i) {
            this(t, i, 1);
        }
        public ProductIO(LocalDate t, boolean i, int count) {
            time = t;
            isInput = i;
            this.count = count;
        }

        @Override
        public int compareTo(ProductIO o) {
            return time.compareTo(o.time);
        }
    }
}