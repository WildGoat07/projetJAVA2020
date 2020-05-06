package view;

import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import model.*;
import controller.*;

import javax.swing.border.*;

import utilities.*;

public class People extends JPanel implements CanUpdate {
    private Application app;
    public JButton newPerson;
    private JComboBox<String> customTypes;
    private JPanel customersList;

    private Comparator<Person> currentComparator;
    private Comparator<Person> nameComparator = new Comparator<Person>(){
        @Override
        public int compare(Person o1, Person o2) {
            int res = Functions.simplify(o1.getName()).compareTo(Functions.simplify(o2.getName()));
            if (res == 0)
                return Functions.simplify(o1.getSurname()).compareTo(Functions.simplify(o2.getSurname()));
            else
                return res;
        }
    };
    private Comparator<Person> surnameComparator = new Comparator<Person>(){
        @Override
        public int compare(Person o1, Person o2) {
            int res = Functions.simplify(o1.getSurname()).compareTo(Functions.simplify(o2.getSurname()));
            if (res == 0)
                return Functions.simplify(o1.getName()).compareTo(Functions.simplify(o2.getName()));
            else
                return res;
        }
    };
    private Comparator<Person> loyalComparator = new Comparator<Person>(){
        @Override
        public int compare(Person o1, Person o2) {
            int res = Functions.simplify(o1.getSurname()).compareTo(Functions.simplify(o2.getSurname()));
            if (res == 0)
                return nameComparator.compare(o1, o2);
            else
                return res;
        }
    };
    private Comparator<Person> reverseNameComparator = nameComparator.reversed();
    private Comparator<Person> reverseSurnameComparator = surnameComparator.reversed();
    private Comparator<Person> reverseLoyalComparator = loyalComparator.reversed();
    
    public People(Application app) throws Exception {
        this.app = app;
        setLayout(new BorderLayout());
        currentComparator = nameComparator;
        {
            customTypes = new JComboBox<String>();
            add(customTypes, BorderLayout.NORTH);
            customTypes.addItem(app.isCurrentFrench()?"Tous les clients":"All customers");
            customTypes.addItem(app.isCurrentFrench()?"Clients fidèles":"Loyal customers");
            customTypes.addItem(app.isCurrentFrench()?"Clients occasionnels":"Occasional customers");
        }
        {
            JPanel dummy = new JPanel();
            dummy.setLayout(new FlowLayout());
            newPerson = new JButton(app.isCurrentFrench()?"Nouveau client":"New customer");
            newPerson.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NewPerson dialog = new NewPerson(app);
                    dialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            Person res = dialog.getResult();
                            if (res != null) {
                                app.addPerson(res);
                                update();
                                revalidate();
                            }
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
                    dialog.setVisible(true);
                }
            });
            add(dummy, BorderLayout.WEST);
            dummy.add(newPerson);
        }
        customTypes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                revalidate();
            }
        });

        {
            customersList = new JPanel();
            customersList.setLayout(new BorderLayout());
            add(customersList);
        }

        update();
    }
    @Override
    public void update() {
        java.util.List<Person> toDisplay = app.getPeople();
        switch (customTypes.getSelectedIndex()) {
            case 1:
            toDisplay = Functions.where(toDisplay, (p) -> p instanceof LoyalCustomer);
            break;
            case 2:
            toDisplay = Functions.where(toDisplay, (p) -> !(p instanceof LoyalCustomer));
            break;
        }
        Collections.sort(toDisplay, currentComparator);
        customersList.removeAll();
        JPanel customerData = new JPanel();
        JScrollPane scrollyBoi = new JScrollPane(customerData, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollyBoi.getVerticalScrollBar().setUnitIncrement(16);
        customersList.add(scrollyBoi);
        GridBagConstraints gbc = new GridBagConstraints();
        customerData.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        JButton nameField = new JButton(app.isCurrentFrench()?"Prénom":"Name");
        customerData.add(nameField, gbc);
        if (currentComparator == nameComparator)
            nameField.setText(nameField.getText()+" ↑");
        else if (currentComparator == reverseNameComparator)
            nameField.setText(nameField.getText()+" ↓");
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == nameComparator)
                    currentComparator = reverseNameComparator;
                else
                    currentComparator = nameComparator;
                update();
                revalidate();
            }
        });
        gbc.gridx++;
        JButton surnameField = new JButton(app.isCurrentFrench()?"Nom":"Surname");
        customerData.add(surnameField, gbc);
        if (currentComparator == surnameComparator)
            surnameField.setText(surnameField.getText()+" ↑");
        else if (currentComparator == reverseSurnameComparator)
            surnameField.setText(surnameField.getText()+" ↓");
        surnameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == surnameComparator)
                    currentComparator = reverseSurnameComparator;
                else
                    currentComparator = surnameComparator;
                update();
                revalidate();
            }
        });
        gbc.gridx++;
        gbc.weightx = 0;
        JButton loyalField = new JButton(app.isCurrentFrench()?"Fidèle":"Loyal");
        customerData.add(loyalField, gbc);
        if (currentComparator == loyalComparator)
            loyalField.setText(loyalField.getText()+" ↑");
        else if (currentComparator == reverseLoyalComparator)
            loyalField.setText(loyalField.getText()+" ↓");
        loyalField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentComparator == loyalComparator)
                    currentComparator = reverseLoyalComparator;
                else
                    currentComparator = loyalComparator;
                update();
                revalidate();
            }
        });
        gbc.gridy++;
        for (Person person : toDisplay) {
            gbc.gridx = 0;
            gbc.weightx = 1;
            final JLabel customerName = new JLabel(person.getName());
            customerName.setBorder(LineBorder.createGrayLineBorder());
            customerData.add(customerName, gbc);
            gbc.gridx++;
            final JLabel customerSurname = new JLabel(person.getSurname());
            customerSurname.setBorder(LineBorder.createGrayLineBorder());
            customerData.add(customerSurname, gbc);
            gbc.gridx++;
            gbc.weightx = 0;
            final JLabel loyalCustomer = new JLabel(person instanceof LoyalCustomer?(
                app.isCurrentFrench()?"Oui":"Yes"
            ): (
                app.isCurrentFrench()?"Non":"No"
            ));
            loyalCustomer.setBorder(LineBorder.createGrayLineBorder());
            customerData.add(loyalCustomer, gbc);
            MouseListener mouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ViewPerson dialog = new ViewPerson(app, person);
                    dialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                            update();
                            revalidate();
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
                    dialog.setVisible(true);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    customerName.setOpaque(true);
                    customerName.setBackground(new Color(200, 200, 200));
                    customerName.repaint();
                    customerSurname.setOpaque(true);
                    customerSurname.setBackground(new Color(200, 200, 200));
                    customerSurname.repaint();
                    loyalCustomer.setOpaque(true);
                    loyalCustomer.setBackground(new Color(200, 200, 200));
                    loyalCustomer.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    customerName.setOpaque(false);
                    customerName.repaint();
                    customerSurname.setOpaque(false);
                    customerSurname.repaint();
                    loyalCustomer.setOpaque(false);
                    loyalCustomer.repaint();
                }
            };
            customerName.addMouseListener(mouseListener);
            customerSurname.addMouseListener(mouseListener);
            loyalCustomer.addMouseListener(mouseListener);
            gbc.gridy++;
        }
        gbc.gridwidth = 3;
        gbc.weighty = 1;
        //dummy filling component
        customerData.add(new JLabel(), gbc);
    }
}