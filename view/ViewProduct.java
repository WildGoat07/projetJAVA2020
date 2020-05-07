package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import model.*;
import utilities.Functions;
import controller.Application;

public class ViewProduct extends JDialog {

    private static final long serialVersionUID = 1L;

    public ViewProduct(Application app, Product p) {
        final Window itself = this;
        MainWindow.instance.addNewSubWindow(this);
        addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.instance.remove(itself);
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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(mainPanel, gbc);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(1000, 250);
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
                setSize(1000, 450);
            }
            else
                setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (Exception e) {}
        mainPanel.add(img);
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Catégorie : ":"Category : ")+Functions.getProductType(p, app.isCurrentFrench())));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Nom : ":"Name : ")+p.getTitle()));
        mainPanel.add(new JLabel("ID : "+p.getID().toString()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Prix pour un jour : ":"Price for one day : ")+p.getPrice(1).toString()));
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
        mainPanel.add(new JLabel((app.isCurrentFrench()?"En stock (aujourd'hui) : ":"In stock (today) : ")+app.getProductCountInStock(p)+"/"+app.getRegisteredProductCount(p)));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Loués (aujourd'hui) : ":"Rented (today) : ")+app.getRentedProductCount(p)+"/"+app.getRegisteredProductCount(p)));
        JSpinner nbToRemove = new JSpinner(new SpinnerNumberModel(0, 0, app.getLowestStockProduct(p), 1));
        JButton removeNbProducts = new JButton(app.isCurrentFrench()?"Retirer":"Remove", new ImageIcon("images/trash.png"));
        mainPanel.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Retirer des produits : ":"Remove products : "),
            nbToRemove,
            removeNbProducts
        }));
        removeNbProducts.setEnabled(false);
        nbToRemove.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                removeNbProducts.setEnabled((int)nbToRemove.getValue() > 0);
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
                try {
                    int toRemove = (int)nbToRemove.getValue();
                    app.removeProduct(p, toRemove);
                    MainWindow.addChange(new Change(){
                        @Override
                        public void undo() {
                            app.addProduct(p, toRemove);
                            MainWindow.instance.products.update();
                            MainWindow.instance.products.revalidate();
                        }
                        @Override
                        public void redo() {
                            try {
                                app.removeProduct(p, toRemove);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                            }
                            catch (Exception e) {}
                        }
                    });
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
                catch (Exception exc) {}
            }
        });
        deleteProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int nbInStock = app.getProductCountInStock(p);
                    app.removeProduct(p);
                    MainWindow.addChange(new Change(){
                        @Override
                        public void undo() {
                            app.addProduct(p, nbInStock);
                            MainWindow.instance.products.update();
                            MainWindow.instance.products.revalidate();
                        }
                        @Override
                        public void redo() {
                            try {
                                app.removeProduct(p);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                            }
                            catch (Exception e) {}
                        }
                    });
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
                catch (Exception exc) {}
            }
        });

        gbc.weightx = 1;
        gbc.gridx = 1;
        ArrayList<ProductIO> data = new ArrayList<ProductIO>();
        for (Order order : app.getOrders())
            if (order.getProducts().contains(p)) {
                data.add(new ProductIO(order.getBeginningRental(), false));
                data.add(new ProductIO(order.getEndingRental(), true));
            }
        data.sort(new Comparator<ProductIO>() {
            @Override
            public int compare(ProductIO o1, ProductIO o2) {
                return o1.time.compareTo(o2.time);
            }
        });
        Map<LocalDate, Integer> stock = new HashMap<LocalDate, Integer>();
        int currStock = app.getRegisteredProductCount(p);
        for (ProductIO productIO : data) {
            currStock += productIO.isInput?1:-1;
            stock.put(productIO.time, currStock);
        }
        if (stock.size() > 0) {
            LocalDate min = Collections.min(stock.keySet());
            LocalDate max = Collections.max(stock.keySet());
            long days = min.until(max, ChronoUnit.DAYS);
            stock.put(min.minusDays((long)(days*.15f)), app.getRegisteredProductCount(p));
            stock.put(max.plusDays((long)(days*.15f)), app.getRegisteredProductCount(p));
            add(new Graph(stock, 0, (int)(app.getRegisteredProductCount(p)*1.1f), LocalDate.now()), gbc);
        }
        else {
            LocalDate min = LocalDate.now().minusDays(1);
            LocalDate max = LocalDate.now().plusDays(1);
            long days = min.until(max, ChronoUnit.DAYS);
            stock.put(min.minusDays((long)(days*.15f)), app.getRegisteredProductCount(p));
            stock.put(max.plusDays((long)(days*.15f)), app.getRegisteredProductCount(p));
            add(new Graph(stock, 0, (int)(app.getRegisteredProductCount(p)*1.1f), LocalDate.now()), gbc);
        }
    }
    private class ProductIO {
        public LocalDate time;
        public boolean isInput;
        public ProductIO(LocalDate t, boolean i) {
            time = t;
            isInput = i;
        }
    }
}