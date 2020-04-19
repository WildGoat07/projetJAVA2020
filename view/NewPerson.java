package view;

import model.*;
import controller.*;
import utilities.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class NewPerson extends JDialog {
    private Person result;
    public NewPerson(Application app) {
        NewPerson itself = this;
        setSize(250, 170);
        setLocationRelativeTo(MainWindow.instance);
        try {
            setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (Exception e){}
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