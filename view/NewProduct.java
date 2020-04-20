package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.io.*;

import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import model.*;
import controller.*;
import utilities.Functions;
import utilities.Price;
import utilities.ToStringOverrider;

public class NewProduct extends JDialog {
    private File productImg;
    private LocalDate productRelease;
    private Product result;
    private int quantity;
    public NewProduct(Application app) {
        Locale currLanguage = app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH;
        NewProduct itself = this;
        setLayout(new FlowLayout());
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (Exception e){}
        result = null;
        quantity = 0;
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(300, 150);
        setTitle(app.isCurrentFrench() ? "Ajouter un produit" : "Add a product");

        JComboBox<Object> productList = new JComboBox<Object>();
        {
            java.util.List<Product> prods = app.getStock();
            prods.sort(new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return Functions.simplify(o1.getTitle()).compareTo(Functions.simplify(o2.getTitle()));
                }
            });
            for (Product prod : prods)
                productList.addItem(prod);
        }
        productList.addItem(app.isCurrentFrench() ? "Créer un nouveau produit" : "Create a new product");
        mainPanel.add(productList);
        JPanel quantityPanel = new JPanel();
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        mainPanel.add(Functions.alignHorizontal(
                new Component[] { new JLabel(app.isCurrentFrench() ? "Quantité :" : "Quantity :"), quantity }));
        quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.X_AXIS));

        JPanel newProd = new JPanel();
        newProd.setVisible(false);
        mainPanel.add(newProd);
        newProd.setLayout(new BoxLayout(newProd, BoxLayout.Y_AXIS));
        JTextField productName = new JTextField(20);
        newProd.add(Functions.alignHorizontal(new Component[] {
                new JLabel(app.isCurrentFrench() ? "Nom du produit :" : "Product's name"),
                productName
            }));
        JSpinner productPrice = new JSpinner(new SpinnerNumberModel(1, .01f, 9999, .1f));
        newProd.add(Functions.alignHorizontal(new Component[] {
                new JLabel(app.isCurrentFrench() ? "Prix du produit :" : "Product's price"),
                productPrice,
                new JLabel(" €")
            }));
        JButton searchImg = new JButton(app.isCurrentFrench() ? "Choisir une image" : "Select an image");
        JButton resetImg = new JButton(app.isCurrentFrench() ? "Retirer l'image" : "Remove the image");
        JLabel productThumbnail = new JLabel();
        productImg = null;
        resetImg.setEnabled(false);
        searchImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser openFile = new JFileChooser(
                        app.isCurrentFrench() ? "Ajouter une image" : "Add an image");
                openFile.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        int i = f.getName().lastIndexOf('.');
                        if (i > 0) {
                            String ext = f.getName().substring(i+1);
                            if(ext.equals("jpeg")
                                || ext.equals("jpg")
                                || ext.equals("png")
                                || ext.equals("bmp"))
                                return true;
                            else
                                return false;
                        }
                        else
                            return false;
                    }
                    @Override
                    public String getDescription() {
                        return "Images";
                    }
                });
                int res = openFile.showOpenDialog(itself);
                try {
                    if (res == JFileChooser.APPROVE_OPTION) {
                        productImg = openFile.getSelectedFile();
                        productThumbnail.setIcon(new ImageIcon(Functions.resizeImage(48, 48, ImageIO.read(productImg))));
                        productThumbnail.revalidate();
                        resetImg.setEnabled(true);
                    }
                }
                catch(Exception exc){}
            }
        });
        resetImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productImg = null;
                productThumbnail.setIcon(null);
                productThumbnail.revalidate();
                resetImg.setEnabled(false);
    }
        });
        newProd.add(Functions.alignHorizontal(new Component[]{
            productThumbnail,
            searchImg,
            resetImg
        }));
        if (app.getStock().size() == 0) {
            newProd.setVisible(true);
            setSize(400, 300);
        }
        productList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productList.getSelectedIndex() == productList.getItemCount()-1) {
                    newProd.setVisible(true);
                    setSize(400, 300);
                }
                else {
                    newProd.setVisible(false);
                    setSize(300, 150);
                }
            }
        });
        JComboBox<String> productType = new JComboBox<String>();
        productType.addItem(app.isCurrentFrench()?"BD":"Comic");
        productType.addItem(app.isCurrentFrench()?"Roman":"Novel");
        productType.addItem(app.isCurrentFrench()?"Livre scolaire":"School book");
        productType.addItem(app.isCurrentFrench()?"Dictionnaire":"Dictionary");
        productType.addItem("CD");
        productType.addItem("DVD");
        JPanel placeholder = new JPanel();
        placeholder.setLayout(new FlowLayout());
        newProd.add(productType);
        newProd.add(placeholder);
        JTextField productAuthor = new JTextField(20);
        JComboBox<ToStringOverrider<Locale>> productLang = new JComboBox<ToStringOverrider<Locale>>();
        java.util.List<Locale> availableLanguages = new ArrayList<Locale>();
        for (String lang : Locale.getISOLanguages())
            availableLanguages.add(new Locale(lang));
        availableLanguages.sort(new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
                return Functions.simplify(o1.getDisplayLanguage()).compareTo(Functions.simplify(o2.getDisplayLanguage()));
            }
        });
        for (Locale lang : availableLanguages) {
            ToStringOverrider<Locale> overrider = new ToStringOverrider<Locale>(lang, lang.getDisplayLanguage(currLanguage));
            productLang.addItem(overrider);
            if (lang.getISO3Language().equals(currLanguage.getISO3Language()))
                productLang.setSelectedItem(overrider);
        }
        productRelease = LocalDate.now();
        JLabel releaseDisplay = new JLabel();
        JButton newReleaseDate = new JButton(app.isCurrentFrench()?"Changer la date":"Change the date", new ImageIcon("images/cal.png"));
        newReleaseDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatePicker picker = new DatePicker(currLanguage);
                picker.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }
                    @Override
                    public void windowClosing(WindowEvent e) {
                        LocalDate date = picker.getResult();
                        if (date != null) {
                            productRelease = date;
                            releaseDisplay.setText((app.isCurrentFrench()?"Date de sortie : ":"Release date : ")+productRelease.toString()+" ");
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

        //by default
        releaseDisplay.setText((app.isCurrentFrench()?"Date de sortie : ":"Release date : ")+productRelease.toString()+" ");
        placeholder.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Auteur : ":"Author : "),
            productAuthor
        }));

        productType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeholder.removeAll();
                switch(productType.getSelectedIndex()) {
                    case 0:
                    case 1:
                    case 2:
                    placeholder.add(Functions.alignHorizontal(new Component[] {
                        new JLabel(app.isCurrentFrench()?"Auteur : ":"Author : "),
                        productAuthor
                    }));
                    break;
                    case 3:
                    placeholder.add(Functions.alignHorizontal(new Component[] {
                        new JLabel(app.isCurrentFrench()?"Langue : ":"Language : "),
                        productLang
                    }));
                    break;
                    case 4:
                    placeholder.add(Functions.alignHorizontal(new Component[] {
                        releaseDisplay,
                        newReleaseDate
                    }));
                    break;
                    case 5:
                    placeholder.add(Functions.alignHorizontal(new Component[] {
                        new JLabel(app.isCurrentFrench()?"Réalisateur : ":"Director : "),
                        productAuthor
                    }));
                    break;
                }
                revalidate();
            }
        });

        JButton validate = new JButton(app.isCurrentFrench()?"Ajouter":"Add");
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itself.quantity = (int)quantity.getValue();
                if (productList.getSelectedIndex() == productList.getItemCount()-1) {
                    try {
                        switch(productType.getSelectedIndex()) {
                            case 0:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new Comic(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new Comic(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), null);
                            }
                            break;
                            case 1:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new Novel(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new Novel(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), null);
                            }
                            break;
                            case 2:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new SchoolBook(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new SchoolBook(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), null);
                            }
                            break;
                            case 3:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new model.Dictionary(new Price(((Number)productPrice.getValue()).floatValue()), ((ToStringOverrider<Locale>)productLang.getSelectedItem()).getObject(), productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new model.Dictionary(new Price(((Number)productPrice.getValue()).floatValue()), ((ToStringOverrider<Locale>)productLang.getSelectedItem()).getObject(), productName.getText(), null);
                            }
                            break;
                            case 4:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new CD(new Price(((Number)productPrice.getValue()).floatValue()), productRelease, productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new CD(new Price(((Number)productPrice.getValue()).floatValue()), productRelease, productName.getText(), null);
                            }
                            break;
                            case 5:
                            if (productImg != null) {
                                InputStream img = new FileInputStream(productImg);
                                result = new DVD(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), img);
                                img.close();
                            }
                            else {
                                result = new DVD(new Price(((Number)productPrice.getValue()).floatValue()), productAuthor.getText(), productName.getText(), null);
                            }
                            break;
                        }
                    }catch (Exception exc) {}
                }
                else
                    result = (Product)productList.getSelectedItem();
                itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
            }
        });
        SwingUtilities.getRootPane(this).setDefaultButton(validate); 
        mainPanel.add(validate);
    }
    public int getQuantity() {
        return quantity;
    } 
    public Product getResult() {
        return result;
    }
}