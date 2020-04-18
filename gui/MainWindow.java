package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import data.*;

public class MainWindow extends JFrame {
    private JTabbedPane tab;
    public MainWindow(data.Application app) throws Exception {
        super();
        setSize(1200, 700);
        setTitle("Videoworld");
        tab = new JTabbedPane();
        add(tab);
        tab.addTab(app.isCurrentFrench() ? "Produits" : "Products", new Products(app));
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