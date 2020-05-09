package view;

import controller.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Options extends JDialog {
    private static final long serialVersionUID = 1L;

    public Options(Application app) {
        super(MainWindow.instance);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setSize(300, 150);
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
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch(IOException e){}
        setTitle(app.isCurrentFrench()?"Paramètres":"Options");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);
        JCheckBox lang = new JCheckBox(app.isCurrentFrench()?"Logiciel en français":"Software in french");
        lang.setSelected(app.isFrench());
        lang.setForeground(Color.red);
        mainPanel.add(lang);
        mainPanel.add(new JLabel(app.isCurrentFrench()?"Les modifications prendront":"The modifications will take"));
        mainPanel.add(new JLabel(app.isCurrentFrench()?"effet après un redémarrage":"effect after a restart"));
        lang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.setFrench(lang.isSelected());
                boolean curr = lang.isSelected();
                MainWindow.addChange(new Change(){
                    @Override
                    public void undo() {
                        app.setFrench(!curr);
                    }
                    @Override
                    public void redo() {
                        app.setFrench(curr);
                    }
                });
                MainWindow.instance.triggerChange();
            }
        });
    }
}