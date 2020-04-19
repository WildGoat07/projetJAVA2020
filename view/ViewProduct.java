package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;

import model.*;
import utilities.Functions;
import controller.Application;

public class ViewProduct extends JDialog {

    public ViewProduct(Application app, Product p) {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        setLayout(new FlowLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(350, 250);
        setLocationRelativeTo(null);
        mainPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        setTitle(p.getTitle());
        JLabel img = new JLabel();
        try {
            InputStream stream = p.getImage();
            if (p != null) {
                img.setIcon(new ImageIcon(Functions.resizeImage(200, 200, ImageIO.read(stream))));
                stream.reset();
                setIconImage(Functions.resizeImage(16, 16, ImageIO.read(stream)));
                stream.close();
                setSize(400, 450);
            }
        }
        catch (Exception e) {}
        mainPanel.add(img);
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Catégorie : ":"Category : ")+Functions.getProductType(p, app.isCurrentFrench())));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Nom : ":"Name : ")+p.getTitle()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Prix pour un jour : ":"Price for one day : ")+p.getPrice(1).toString()));
        Component specificCompo = null;
        if (p instanceof Book) {
            Book prod = (Book)p;
            specificCompo = new JLabel((app.isCurrentFrench()?"Auteur : ":"Author : ")+prod.getAuthor());
        }
        else if (p instanceof Dictionary) {
            Dictionary prod = (Dictionary)p;
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
        mainPanel.add(new JLabel((app.isCurrentFrench()?"En stock (aujourd'hui) : ":"In stock (today) : ")+app.getProductCountInStock(p)));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Loués (aujourd'hui) : ":"Rented (today) : ")+app.getRentedProductCount(p)));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Enregistrés : ":"Registered : ")+app.getRegisteredProductCount(p)));
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
        final Window itself = this;
        removeNbProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.removeProduct(p, (int)nbToRemove.getValue());
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
                catch (Exception exc) {}
            }
        });
        deleteProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.removeProduct(p);
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
                catch (Exception exc) {}
            }
        });
    }
}