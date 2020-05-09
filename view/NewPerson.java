package view;

import model.*;
import controller.*;
import utilities.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class NewPerson extends JDialog {
    private static final long serialVersionUID = 1L;
    private Person result;
    public NewPerson(Application app) {
        NewPerson itself = this;
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
        setSize(250, 170);
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (IOException e){}
        setLayout(new FlowLayout());
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setTitle(app.isCurrentFrench()?"Ajouter un client":"Add a customer");

        JTextField personName = new JTextField(10);
        JTextField personSurname = new JTextField(10);
        JCheckBox loyalCustomer = new JCheckBox(app.isCurrentFrench()?"Client fidèle":"Loyal customer");
        JButton validate = new JButton(app.isCurrentFrench()?"Ajouter":"Add");
        mainPanel.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Prénom : ":"Name : "),
            personName
        }));
        mainPanel.add(Functions.alignHorizontal(new Component[] {
            new JLabel(app.isCurrentFrench()?"Nom : ":"Surname : "),
            personSurname
        }));
        mainPanel.add(loyalCustomer);
        mainPanel.add(validate);
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loyalCustomer.isSelected())
                    result = new LoyalCustomer(personName.getText(), personSurname.getText());
                else
                    result = new Person(personName.getText(), personSurname.getText());
                itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
            }
        });
        SwingUtilities.getRootPane(this).setDefaultButton(validate); 
    }

    public Person getResult() {
        return result;
    }
}