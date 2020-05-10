package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import controller.Application;
import model.*;
import utilities.*;

public class EditPerson extends JDialog {
    private static final long serialVersionUID = 1L;

    public EditPerson(Application app, Person p, Window caller) {
        super(caller);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        final JPanel mainPanel = new JPanel();
        final Window itself = this;
        MainWindow.instance.addNewSubWindow(this);
        addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(final WindowEvent e) {
            }
            @Override
            public void windowClosing(final WindowEvent e) {
                MainWindow.instance.removeSubWindow(itself);
            }
            @Override
            public void windowClosed(final WindowEvent e) {
            }
            @Override
            public void windowIconified(final WindowEvent e) {
            }
            @Override
            public void windowDeiconified(final WindowEvent e) {
            }
            @Override
            public void windowActivated(final WindowEvent e) {
            }
            @Override
            public void windowDeactivated(final WindowEvent e) {
            }
        });
        add(mainPanel);
        setLayout(new FlowLayout());
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setSize(250, 180);
        setLocationRelativeTo(caller);
        setTitle(app.isCurrentFrench()?"Éditer un client":"Edit a customer");
        JTextField name = new JTextField(p.getName());
        mainPanel.add(Functions.alignHorizontal(new Component[]{
            new JLabel(app.isCurrentFrench()?"Prénom :":"Name :"),
            name
        }));
        JTextField surname = new JTextField(p.getSurname());
        mainPanel.add(Functions.alignHorizontal(new Component[]{
            new JLabel(app.isCurrentFrench()?"Nom :":"Surname :"),
            surname
        }));
        JCheckBox loyalty = new JCheckBox(app.isCurrentFrench()?"Client fidèle":"Loyal customer");
        loyalty.setSelected(p.isLoyal());
        mainPanel.add(loyalty);

        JButton save = new JButton(app.isCurrentFrench()?"Enregistrer":"Save");
        mainPanel.add(save);
        save.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldName = p.getName();
                String oldSurname = p.getSurname();
                boolean oldLoyalty = p.isLoyal();
                p.setName(name.getText());
                p.setSurname(surname.getText());
                p.setLoyalty(loyalty.isSelected());
                MainWindow.instance.people.update();
                MainWindow.instance.people.revalidate();
                MainWindow.instance.orders.update();
                MainWindow.instance.orders.revalidate();
                MainWindow.addChange(new Change() {
                    @Override
                    public void undo() {
                        p.setName(oldName);
                        p.setSurname(oldSurname);
                        p.setLoyalty(oldLoyalty);
                        MainWindow.instance.people.update();
                        MainWindow.instance.people.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                    }
                    @Override
                    public void redo() {
                        p.setName(name.getText());
                        p.setSurname(surname.getText());
                        p.setLoyalty(loyalty.isSelected());
                        MainWindow.instance.people.update();
                        MainWindow.instance.people.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                    }
                });
                itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
            }
        });
    }
}