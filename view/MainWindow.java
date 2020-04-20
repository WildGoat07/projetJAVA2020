package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.*;
import controller.*;

public class MainWindow extends JFrame {
    public static MainWindow instance;
    private JTabbedPane tab;
    private int lastTabSelection;
    public MainWindow(Application app) throws Exception {
        super();
        instance = this;
        setSize(1200, 700);
        setIconImage(ImageIO.read(new File("images/icon.png")));
        setLocationRelativeTo(null);
        setTitle("Videoworld");
        tab = new JTabbedPane();
        lastTabSelection = 0;
        add(tab);
        tab.addTab(app.isCurrentFrench() ? "Produits" : "Products", new Products(app));
        tab.addTab(app.isCurrentFrench() ? "Clients" : "Customers", new Persons(app));
        tab.addTab(app.isCurrentFrench() ? "Commandes" : "Orders", new Orders(app));
        tab.addTab(app.isCurrentFrench()?"Paramètres":"Options", null);
        tab.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tab.getSelectedIndex() == 3) {
                    tab.setSelectedIndex(lastTabSelection);
                    new Options(app).setVisible(true);
                }
                else {
                    lastTabSelection = tab.getSelectedIndex();
                    ((CanUpdate)tab.getSelectedComponent()).update();
                    tab.getSelectedComponent().revalidate();
                }
            }
        });
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    File saveFile = new File("app.ser");
                    if (!saveFile.exists())
                        saveFile.createNewFile();
                    OutputStream stream = new FileOutputStream(saveFile);
                    app.saveToStream(stream);
                    stream.close();
                }
                catch (Exception exception) {}
                System.exit(0);
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
}