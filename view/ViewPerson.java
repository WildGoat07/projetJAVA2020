package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import controller.Application;
import model.*;

public class ViewPerson extends JDialog {
    public ViewPerson(Application app, Person p) {
        JPanel mainPanel = new JPanel();
        final Window itself = this;
        add(mainPanel);
        setLayout(new FlowLayout());
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (Exception e){}
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(350, 200);
        setLocationRelativeTo(MainWindow.instance);
        mainPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        setTitle(p.getName() + " " + p.getSurname());
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Prénom : ":"Name : ")+p.getName()));
        mainPanel.add(new JLabel((app.isCurrentFrench()?"Nom : ":"Surname : ")+p.getSurname()));
        mainPanel.add(new JLabel("ID : "+p.getID().toString()));
        JCheckBox isLoyal = new JCheckBox(app.isCurrentFrench()?"Client fidèle":"Loyal customer");
        isLoyal.setSelected(p instanceof LoyalCustomer);
        mainPanel.add(isLoyal);
        isLoyal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLoyal.setSelected(p instanceof LoyalCustomer);
                isLoyal.revalidate();
            }
        });
        JButton delete = new JButton(app.isCurrentFrench()?"Supprimer":"Delete", new ImageIcon("images/trash.png"));
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.removePerson(p);
                    MainWindow.addChange(new Change(){
                        @Override
                        public void undo() {
                            app.addPerson(p);
                            MainWindow.instance.people.update();
                            MainWindow.instance.people.revalidate();
                        }
                        @Override
                        public void redo() {
                            try {
                                app.removePerson(p);
                                MainWindow.instance.people.update();
                                MainWindow.instance.people.revalidate();
                            }
                            catch (Exception e) {}
                        }
                    });
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
                catch(Exception exc) {}
            }
        });
        if (!app.canRemovePerson(p)) {
            delete.setEnabled(false);
            delete.setToolTipText(app.isCurrentFrench()?"Ce client a passé une commande":"This customer made an order");
        }
        mainPanel.add(delete);
    }
}