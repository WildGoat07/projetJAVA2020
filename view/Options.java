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
        setSize(300, 150);
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
                MainWindow.instance.triggerChange();
            }
        });
    }
}