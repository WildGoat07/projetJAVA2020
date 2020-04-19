package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.*;
import controller.*;

public class MainWindow extends JFrame {
    public static MainWindow instance;
    private JTabbedPane tab;
    public MainWindow(Application app) throws Exception {
        super();
        instance = this;
        setSize(1200, 700);
        setIconImage(ImageIO.read(new File("images/icon.png")));
        setLocationRelativeTo(null);
        setTitle("Videoworld");
        tab = new JTabbedPane();
        add(tab);
        tab.addTab(app.isCurrentFrench() ? "Produits" : "Products", new Products(app));
        tab.addTab(app.isCurrentFrench() ? "Clients" : "Customers", new Persons(app));
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