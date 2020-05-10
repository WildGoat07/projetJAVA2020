package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import javax.swing.filechooser.FileFilter;

import controller.Application;
import model.*;
import utilities.*;

public class EditProduct extends JDialog {
    private static final long serialVersionUID = 1L;
    private int imageChanged;
    private File newImage;
    private LocalDate newPriceDate;
    private Object customElement;

    @SuppressWarnings("unchecked")
    public EditProduct(final Application app, final Product p, final Window caller) {
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
        setSize(500, 350);
        setLocationRelativeTo(caller);
        setTitle(app.isCurrentFrench()?"Éditer un produit":"Edit a product");
        final JLabel img = new JLabel();
        try {
            final InputStream stream = p.getImage();
            if (stream != null) {
                final BufferedImage thumbnail = ImageIO.read(stream);
                img.setIcon(new ImageIcon(Functions.resizeImage(50, 50, thumbnail)));
                stream.reset();
                setIconImage(Functions.resizeImage(16, 16, thumbnail));
                stream.close();
            }
            else
                setIconImage(ImageIO.read(new File("images/icon.png")));
        }
        catch (final IOException e) {}
        final JTextField productName = new JTextField();
        productName.setText(p.getTitle());
        mainPanel.add(Functions.alignHorizontal(new Component[]{
            new JLabel(app.isCurrentFrench()?"Nom :":"Name :"),
            productName
        }));
        newImage = null;
        imageChanged = 0;
        final JButton newProductImage = new JButton(app.isCurrentFrench()?"Changer l'image":"Change the image");
        final JButton resetProductImage = new JButton(app.isCurrentFrench()?"Retirer l'image":"Remove image");
        if (p.getImage() == null)
            resetProductImage.setEnabled(false);
        mainPanel.add(Functions.alignHorizontal(new Component[]{
            img,
            Functions.alignVertical(new Component[]{
                newProductImage,
                resetProductImage
            })
        }));
        newProductImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final JFileChooser openFile = new JFileChooser(app.isCurrentFrench() ? "Ajouter une image" : "Add an image");
                openFile.getActionMap().get("viewTypeDetails").actionPerformed(null);
                openFile.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(final File f) {
                        if (f.isDirectory())
                            return true;
                        final int i = f.getName().lastIndexOf('.');
                        if (i > 0) {
                            final String ext = f.getName().substring(i+1);
                            if(ext.equals("jpeg")
                                || ext.equals("jpg")
                                || ext.equals("png")
                                || ext.equals("bmp"))
                                return true;
                            else
                                return false;
                        }
                        else
                            return false;
                    }
                    @Override
                    public String getDescription() {
                        return "Images";
                    }
                });
                final int res = openFile.showOpenDialog(itself);
                try {
                    if (res == JFileChooser.APPROVE_OPTION) {
                        newImage = openFile.getSelectedFile();
                        img.setIcon(new ImageIcon(Functions.resizeImage(48, 48, ImageIO.read(newImage))));
                        img.revalidate();
                        resetProductImage.setEnabled(true);
                        imageChanged = 1;
                    }
                }
                catch(final IOException exc){}
            }
        });
        resetProductImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                newImage = null;
                img.setIcon(null);
                img.revalidate();
                resetProductImage.setEnabled(false);
                imageChanged = 2;
            }
        });
        final JCheckBox changePrice = new JCheckBox(app.isCurrentFrench()?"Nouveau prix":"New price");
        changePrice.setSelected(false);
        final JSpinner productNewPrice = new JSpinner(new SpinnerNumberModel(1, .01f, 9999, .1f));
        newPriceDate = LocalDate.now();
        final JButton changeDate = new JButton(newPriceDate.toString(), new ImageIcon("images/cal.png"));
        final JButton resetDate = new JButton(app.isCurrentFrench()?"Aujourd'hui":"Today");
        changeDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final DatePicker picker = new DatePicker(newPriceDate, Locale.getDefault(), itself);
                picker.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(final WindowEvent e) {
                    }
                    @Override
                    public void windowClosing(final WindowEvent e) {
                        final LocalDate date = picker.getResult();
                        if (date != null) {
                            newPriceDate = date;
                            changeDate.setText(newPriceDate.toString());
                            changeDate.revalidate();
                        }
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
                picker.setVisible(true);
            }
        });
        resetDate.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(final ActionEvent e) {
                newPriceDate = LocalDate.now();
                changeDate.setText(newPriceDate.toString());
                changeDate.revalidate();
}
        });
        {
            productNewPrice.setValue(p.getPrice(1, LocalDate.now()));
            productNewPrice.setEnabled(false);
            changeDate.setEnabled(false);
            resetDate.setEnabled(false);
            final JLabel tmp1 = new JLabel(app.isCurrentFrench()?"Nouveau montant :":"New amount :");
            final JLabel tmp2 = new JLabel(" €");
            tmp1.setEnabled(false);
            tmp2.setEnabled(false);
            mainPanel.add(changePrice);
            mainPanel.add(Functions.alignHorizontal(new Component[]{tmp1, productNewPrice, tmp2}));
            changePrice.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(final ActionEvent e) {
                    changeDate.setEnabled(changePrice.isSelected());
                    resetDate.setEnabled(changePrice.isSelected());
                    tmp1.setEnabled(changePrice.isSelected());
                    tmp2.setEnabled(changePrice.isSelected());
                    productNewPrice.setEnabled(changePrice.isSelected());
                }
            });
            mainPanel.add(Functions.alignHorizontal(new Component[]{changeDate, resetDate}));
            if (p instanceof Book) {
                customElement = new JTextField(((Book)p).getAuthor());
                mainPanel.add(Functions.alignHorizontal(new Component[]{
                    new JLabel(app.isCurrentFrench()?"Auteur :":"Author :"),
                    (JComponent)customElement
                }));
            }
            else if (p instanceof CD) {
                customElement = ((CD)p).getReleaseDate();
                final JButton changeReleaseDate = new JButton(customElement.toString(), new ImageIcon("images/cal.png"));
                final JButton resetReleaseDate = new JButton(app.isCurrentFrench()?"Aujourd'hui":"Today");
                changeReleaseDate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final DatePicker picker = new DatePicker((LocalDate)customElement, Locale.getDefault(), itself);
                        picker.addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(final WindowEvent e) {
                            }
                            @Override
                            public void windowClosing(final WindowEvent e) {
                                final LocalDate date = picker.getResult();
                                if (date != null) {
                                    customElement = date;
                                    changeReleaseDate.setText(customElement.toString());
                                    changeReleaseDate.revalidate();
                                }
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
                        picker.setVisible(true);
                    }
                });
                resetReleaseDate.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        customElement = LocalDate.now();
                        changeReleaseDate.setText(customElement.toString());
                        changeReleaseDate.revalidate();
                    }
                });
                mainPanel.add(new JLabel(app.isCurrentFrench()?"Date de sortie :":"Release date :"));
                mainPanel.add(Functions.alignHorizontal(new Component[]{changeReleaseDate, resetReleaseDate}));
            }
            else if (p instanceof DVD) {
                customElement = new JTextField(((DVD)p).getDirector());
                mainPanel.add(Functions.alignHorizontal(new Component[]{
                    new JLabel(app.isCurrentFrench()?"Réalisateur :":"Director :"),
                    (JComponent)customElement
                }));
            }
            else if (p instanceof Dictionary) {
                final JComboBox<ToStringOverrider<Locale>> productLang = new JComboBox<ToStringOverrider<Locale>>();
                customElement = productLang;
                final java.util.List<Locale> availableLanguages = new ArrayList<Locale>();
                for (final String lang : Locale.getISOLanguages())
                    availableLanguages.add(new Locale(lang));
                availableLanguages.sort(new Comparator<Locale>() {
                    @Override
                    public int compare(final Locale o1, final Locale o2) {
                        return Functions.simplify(o1.getDisplayLanguage()).compareTo(Functions.simplify(o2.getDisplayLanguage()));
                    }
                });
                for (final Locale lang : availableLanguages) {
                    final ToStringOverrider<Locale> overrider = new ToStringOverrider<Locale>(lang, lang.getDisplayLanguage(Locale.getDefault()));
                    productLang.addItem(overrider);
                    if (lang.getISO3Language().equals(((Dictionary)p).getLanguage().getISO3Language()))
                        productLang.setSelectedItem(overrider);
                }
                        mainPanel.add(Functions.alignHorizontal(new Component[]{
                    new JLabel(app.isCurrentFrench()?"Langue :":"Language :"),
                    (JComponent)customElement
                }));
            }
            final JButton save = new JButton(app.isCurrentFrench()?"Enregistrer":"Save");
            mainPanel.add(save);
            save.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    InputStream oldImage = p.getImage();
                    String oldTitle = p.getTitle();
                    Price oldPrice = p.getHistory().containsKey(newPriceDate)?p.getHistory().get(newPriceDate):null;
                    p.setTitle(productName.getText());
                    if (imageChanged == 1) {
                        try {
                            InputStream stream = new FileInputStream(newImage);
                            p.setImage(stream);
                            stream.close();
                        }
                        catch (FileNotFoundException exc){}
                        catch (IOException exc){}
                    }
                    if (imageChanged == 2) {
                        try {
                            p.setImage(null);
                        }
                        catch (IOException exc){}
                    }
                    if (changePrice.isSelected())
                        p.changePrice(new Price((double)productNewPrice.getValue()), newPriceDate);
                    if (p instanceof Book) {
                        Book product = (Book)p;
                        String oldCustomValue = product.getAuthor();
                        JTextField value = (JTextField)customElement;
                        product.setAuthor(value.getText());
                        MainWindow.instance.products.update();
                        MainWindow.instance.products.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                        MainWindow.addChange(new Change(){
                            @Override
                            public void undo() {
                                product.setTitle(oldTitle);
                                product.setAuthor(oldCustomValue);
                                try {
                                    if (imageChanged > 0)
                                        product.setImage(oldImage);
                                }
                                catch(IOException exc) {}
                                if (oldPrice == null)
                                    product.removePriceChange(newPriceDate);
                                else
                                    product.changePrice(oldPrice, newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                            @Override
                            public void redo() {
                                product.setTitle(productName.getText());
                                product.setAuthor(value.getText());
                                if (imageChanged == 1) {
                                    try {
                                        InputStream stream = new FileInputStream(newImage);
                                        p.setImage(stream);
                                        stream.close();
                                    }
                                    catch (FileNotFoundException exc){}
                                    catch (IOException exc){}
                                }
                                if (imageChanged == 2) {
                                    try {
                                        p.setImage(null);
                                    }
                                    catch (IOException exc){}
                                }
                                if (changePrice.isSelected())
                                    p.changePrice(new Price((double)productNewPrice.getValue()), newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                        });
                    }
                    else if (p instanceof Dictionary) {
                        Dictionary product = (Dictionary)p;
                        Locale oldCustomValue = product.getLanguage();
                        JComboBox<ToStringOverrider<Locale>> value = (JComboBox<ToStringOverrider<Locale>>)customElement;
                        product.setLanguage(((ToStringOverrider<Locale>)value.getSelectedItem()).getObject());
                        MainWindow.instance.products.update();
                        MainWindow.instance.products.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                        MainWindow.addChange(new Change(){
                            @Override
                            public void undo() {
                                product.setTitle(oldTitle);
                                product.setLanguage(oldCustomValue);
                                try {
                                    if (imageChanged > 0)
                                        product.setImage(oldImage);
                                }
                                catch(IOException exc) {}
                                if (oldPrice == null)
                                    product.removePriceChange(newPriceDate);
                                else
                                    product.changePrice(oldPrice, newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                            @Override
                            public void redo() {
                                product.setTitle(productName.getText());
                                product.setLanguage(((ToStringOverrider<Locale>)value.getSelectedItem()).getObject());
                                if (imageChanged == 1) {
                                    try {
                                        InputStream stream = new FileInputStream(newImage);
                                        p.setImage(stream);
                                        stream.close();
                                    }
                                    catch (FileNotFoundException exc){}
                                    catch (IOException exc){}
                                }
                                if (imageChanged == 2) {
                                    try {
                                        p.setImage(null);
                                    }
                                    catch (IOException exc){}
                                }
                                if (changePrice.isSelected())
                                    p.changePrice(new Price((double)productNewPrice.getValue()), newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                        });
                    }
                    else if (p instanceof CD) {
                        CD product = (CD)p;
                        LocalDate oldCustomValue = product.getReleaseDate();
                        product.setReleaseDate((LocalDate)customElement);
                        MainWindow.instance.products.update();
                        MainWindow.instance.products.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                        MainWindow.addChange(new Change(){
                            @Override
                            public void undo() {
                                product.setTitle(oldTitle);
                                product.setReleaseDate(oldCustomValue);
                                try {
                                    if (imageChanged > 0)
                                        product.setImage(oldImage);
                                }
                                catch(IOException exc) {}
                                if (oldPrice == null)
                                    product.removePriceChange(newPriceDate);
                                else
                                    product.changePrice(oldPrice, newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                            @Override
                            public void redo() {
                                product.setTitle(productName.getText());
                                product.setReleaseDate((LocalDate)customElement);
                                if (imageChanged == 1) {
                                    try {
                                        InputStream stream = new FileInputStream(newImage);
                                        p.setImage(stream);
                                        stream.close();
                                    }
                                    catch (FileNotFoundException exc){}
                                    catch (IOException exc){}
                                }
                                if (imageChanged == 2) {
                                    try {
                                        p.setImage(null);
                                    }
                                    catch (IOException exc){}
                                }
                                if (changePrice.isSelected())
                                    p.changePrice(new Price((double)productNewPrice.getValue()), newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                        });
                    }
                    else if (p instanceof DVD) {
                        DVD product = (DVD)p;
                        String oldCustomValue = product.getDirector();
                        JTextField value = (JTextField)customElement;
                        product.setDirector(value.getText());
                        MainWindow.instance.products.update();
                        MainWindow.instance.products.revalidate();
                        MainWindow.instance.orders.update();
                        MainWindow.instance.orders.revalidate();
                        MainWindow.addChange(new Change(){
                            @Override
                            public void undo() {
                                product.setTitle(oldTitle);
                                product.setDirector(oldCustomValue);
                                try {
                                    if (imageChanged > 0)
                                        product.setImage(oldImage);
                                }
                                catch(IOException exc) {}
                                if (oldPrice == null)
                                    product.removePriceChange(newPriceDate);
                                else
                                    product.changePrice(oldPrice, newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                            @Override
                            public void redo() {
                                product.setTitle(productName.getText());
                                product.setDirector(value.getText());
                                if (imageChanged == 1) {
                                    try {
                                        InputStream stream = new FileInputStream(newImage);
                                        p.setImage(stream);
                                        stream.close();
                                    }
                                    catch (FileNotFoundException exc){}
                                    catch (IOException exc){}
                                }
                                if (imageChanged == 2) {
                                    try {
                                        p.setImage(null);
                                    }
                                    catch (IOException exc){}
                                }
                                if (changePrice.isSelected())
                                    p.changePrice(new Price((double)productNewPrice.getValue()), newPriceDate);
                                MainWindow.instance.products.update();
                                MainWindow.instance.products.revalidate();
                                MainWindow.instance.orders.update();
                                MainWindow.instance.orders.revalidate();
                            }
                        });
                    }
                    itself.dispatchEvent(new WindowEvent(itself, WindowEvent.WINDOW_CLOSING));
                }
            });
        }
    }
}