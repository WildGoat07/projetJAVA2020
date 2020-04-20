package view;

import controller.Application;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Options extends JDialog {
    public Options(Application app) {
        setSize(300, 150);
        setLocationRelativeTo(MainWindow.instance);
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
            }
        });
    }
}