package view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.*;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public static MainWindow instance;
    private JTabbedPane tab;
    private File currentFile;
    private Application app;
    private JMenuBar menu;
    public People people;
    public Products products;
    public Orders orders;
    private java.util.List<Change> changes;
    private int changeSelection;
    private JMenuItem redo;
    private JMenuItem undo;
    private boolean fileChanged;
    private Change savedChange;
    private java.util.List<Window> subWindows;
    public MainWindow() throws Exception {
        this(null);
    }
    public MainWindow(File open) throws Exception {
        super();
        instance = this;
        subWindows = new ArrayList<Window>();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        changes = new ArrayList<Change>();
        changeSelection = -1;
        setSize(1200, 700);
        setIconImage(ImageIO.read(new File("images/icon.png")));
        setLocationRelativeTo(null);
        setTitle("Videoworld");
        tab = new JTabbedPane();
        tab.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                ((CanUpdate)tab.getSelectedComponent()).update();
                tab.getSelectedComponent().revalidate();
            }
        });
        menu = new JMenuBar();
        setLayout(new BorderLayout());
        OpenFile(open);
        add(menu, BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (checkFileChanged())
                        System.exit(0);
                }
                catch(Exception exc){}
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
    public boolean Save() throws IOException {
        if (currentFile != null) {
            OutputStream stream = new FileOutputStream(currentFile);
            app.saveToStream(stream);
            stream.close();
            setTitle("Videoworld*");
            fileChanged = false;
            savedChange = changeSelection>-1?changes.get(changeSelection):null;
            triggerChange();
            return true;
        }
        else
            return SaveAs();
    }
    public boolean SaveAs() throws IOException {
        JFileChooser saveFile = new JFileChooser(app.isCurrentFrench() ? "Sauvegarder sous" : "Save as");
        if (currentFile == null)
            saveFile.setCurrentDirectory(new File(System.getProperty("user.dir")));
        else
            saveFile.setCurrentDirectory(currentFile);
        saveFile.getActionMap().get("viewTypeDetails").actionPerformed(null);
        saveFile.setAcceptAllFileFilterUsed(false);
        saveFile.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                int i = f.getName().lastIndexOf('.');
                if (i > 0) {
                    String ext = f.getName().substring(i+1);
                    if(ext.equals("ser"))
                        return true;
                    else
                        return false;
                }
                else
                    return false;
            }
            @Override
            public String getDescription() {
                return app.isCurrentFrench()?"Données sérialisées":"Serialized data";
            }
        });
        int res = saveFile.showSaveDialog(instance);
        try {
            if (res == JFileChooser.APPROVE_OPTION) {
                currentFile = saveFile.getSelectedFile();
                int i = currentFile.getName().lastIndexOf('.');
                boolean requiresExt = true;
                if (i > 0) {
                    String ext = currentFile.getName().substring(i+1);
                    if(ext.equals("ser"))
                        requiresExt = false;
                }
                if (requiresExt)
                    currentFile = new File(currentFile.getAbsolutePath()+".ser");
                return Save();
            }
        }
        catch(Exception exc){}
        return false;
    }
    public void Open() throws Exception {
        if (checkFileChanged()) {
            JFileChooser openFile = new JFileChooser(app.isCurrentFrench() ? "Ouvrir un fichier" : "Open file");
            if (currentFile == null)
                openFile.setCurrentDirectory(new File(System.getProperty("user.dir")));
            else
                openFile.setCurrentDirectory(currentFile);
            openFile.getActionMap().get("viewTypeDetails").actionPerformed(null);
            openFile.setAcceptAllFileFilterUsed(false);
            openFile.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    int i = f.getName().lastIndexOf('.');
                    if (i >= 0) {
                        String ext = f.getName().substring(i+1);
                        if(ext.equals("ser"))
                            return true;
                        else
                            return false;
                    }
                    else
                        return false;
                }
                @Override
                public String getDescription() {
                    return app.isCurrentFrench()?"Données sérialisées":"Serialized data";
                }
            });
            int res = openFile.showOpenDialog(instance);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = openFile.getSelectedFile();
                OpenFile(f);
            }
        }
    }
    public void OpenFile(File toOpen) throws Exception {
        if (toOpen != null) {
            if (toOpen.exists()) {
                InputStream stream = new FileInputStream(toOpen);
                app = Application.loadFromStream(stream);
                stream.close();
                currentFile = toOpen;
            }
            else {
                app = new Application();
                currentFile = null;
            }
        }
        else {
            app = new Application();
            currentFile = null;
        }
        Locale.setDefault(app.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
        setLocale(Locale.getDefault());
        remove(tab);
        tab = new JTabbedPane();
        add(tab, BorderLayout.CENTER);
        tab.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                ((CanUpdate)tab.getSelectedComponent()).update();
                tab.getSelectedComponent().revalidate();
            }
        });
        people = new People(app);
        products = new Products(app);
        orders = new Orders(app);
        tab.addTab(app.isCurrentFrench() ? "Produits" : "Products", new ImageIcon("images/prod.png"), products);
        tab.addTab(app.isCurrentFrench() ? "Clients" : "Customers", new ImageIcon("images/customer.png"), people);
        tab.addTab(app.isCurrentFrench() ? "Commandes" : "Orders", new ImageIcon("images/order.png"), orders);
        menu.removeAll();
        {
            JMenu fileMenu = new JMenu(app.isCurrentFrench()?"Fichier":"File");
            menu.add(fileMenu);
            {
                JMenuItem newFile = new JMenuItem(app.isCurrentFrench()?"Nouveau fichier":"New file");
                newFile.setIcon(new ImageIcon("images/new.png"));
                fileMenu.add(newFile);
                newFile.setMnemonic(KeyEvent.VK_N);
                newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
                newFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            OpenFile(null);
                        }
                        catch (Exception exc){}
                    }
                });
                JMenuItem openFile = new JMenuItem(app.isCurrentFrench()?"Ouvrir un fichier":"Open file");
                openFile.setIcon(new ImageIcon("images/open.png"));
                fileMenu.add(openFile);
                openFile.setMnemonic(KeyEvent.VK_O);
                openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
                openFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Open();
                        }
                        catch (Exception exc){}
                    }
                });
                JMenuItem saveFile = new JMenuItem(app.isCurrentFrench()?"Sauvegarder":"Save");
                saveFile.setIcon(new ImageIcon("images/save.png"));
                fileMenu.add(saveFile);
                saveFile.setMnemonic(KeyEvent.VK_S);
                saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
                saveFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Save();
                        }
                        catch (Exception exc){}
                    }
                });
                JMenuItem saveAsFile = new JMenuItem(app.isCurrentFrench()?"Sauvegarder sous...":"Save as...");
                saveAsFile.setIcon(new ImageIcon("images/save-as.png"));
                fileMenu.add(saveAsFile);
                saveAsFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
                saveAsFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            SaveAs();
                        }
                        catch (Exception exc){}
                    }
                });
                JMenuItem exit = new JMenuItem(app.isCurrentFrench()?"Quitter":"Exit");
                exit.setIcon(new ImageIcon("images/exit.png"));
                fileMenu.add(exit);
                saveFile.setMnemonic(app.isCurrentFrench()?KeyEvent.VK_Q:KeyEvent.VK_X);
                exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
                exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        instance.dispatchEvent(new WindowEvent(instance, WindowEvent.WINDOW_CLOSING));
                    }
                });
            }
            JMenu editMenu = new JMenu(app.isCurrentFrench()?"Edition":"Edit");
            menu.add(editMenu);
            {
                undo = new JMenuItem(app.isCurrentFrench()?"Annuler":"Undo");
                undo.setIcon(new ImageIcon("images/undo.png"));
                editMenu.add(undo);
                undo.setEnabled(false);
                undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
                undo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        undoChange();
                    }
                });
                redo = new JMenuItem(app.isCurrentFrench()?"Rétablir":"Redo");
                redo.setIcon(new ImageIcon("images/redo.png"));
                editMenu.add(redo);
                redo.setEnabled(false);
                redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
                redo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        redoChange();
                    }
                });
                JMenuItem addProduct = new JMenuItem(app.isCurrentFrench()?"Ajouter produit":"Add product");
                addProduct.setIcon(new ImageIcon("images/add-prod.png"));
                editMenu.add(addProduct);
                addProduct.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        products.newProduct.doClick();
                    }
                });
                JMenuItem addPerson = new JMenuItem(app.isCurrentFrench()?"Ajouter client":"Add customer");
                addPerson.setIcon(new ImageIcon("images/add-customer.png"));
                editMenu.add(addPerson);
                addPerson.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        people.newPerson.doClick();
                    }
                });
                JMenuItem addOrder = new JMenuItem(app.isCurrentFrench()?"Ajouter commande":"Add order");
                addOrder.setIcon(new ImageIcon("images/add-order.png"));
                editMenu.add(addOrder);
                addOrder.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        orders.newOrder.doClick();
                    }
                });
                JMenuItem options = new JMenuItem(app.isCurrentFrench()?"Préférences":"Preferences");
                options.setIcon(new ImageIcon("images/preferences.png"));
                editMenu.add(options);
                options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
                options.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Options(app).setVisible(true);
                    }
                });
            }
        }
        setTitle("Videoworld");
        fileChanged = false;
        changes.clear();
        changeSelection = -1;
        savedChange = null;
        for (Window window : subWindows)
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        subWindows.clear();
        triggerChange();
        menu.repaint();
    }
    public static void addChange(Change c) {

        while (instance.changes.size()-1 > instance.changeSelection)
            instance.changes.remove(instance.changes.size()-1);
        instance.changes.add(c);
        instance.changeSelection++;
        updateChange();
        instance.triggerChange();
    }
    public static void updateChange() {
        instance.undo.setEnabled(instance.changeSelection > -1);
        instance.redo.setEnabled(instance.changeSelection < instance.changes.size()-1);
    }
    public static void undoChange() {
        instance.changes.get(instance.changeSelection).undo();
        instance.changeSelection--;
        updateChange();
        instance.triggerChange();
    }
    public static void redoChange() {
        instance.changeSelection++;
        instance.changes.get(instance.changeSelection).redo();
        updateChange();
        instance.triggerChange();
    }
    public boolean checkFileChanged() throws Exception {
        if (fileChanged) {
            int res = JOptionPane.showConfirmDialog(this,
            app.isCurrentFrench()?"Le fichier actuel a été modifié, voulez-vous le sauvegarder ?":"The current file has been modified, do you want to save it ?",
            app.isCurrentFrench()?"Modifications en attente":"Pending modifications",
            JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.YES_OPTION)
                return Save();
            return res != JOptionPane.CANCEL_OPTION;
        }
        else
            return true;
    }
    public void triggerChange() {
        if (savedChange != (changeSelection>-1?changes.get(changeSelection):null)) {
            fileChanged = true;
            setTitle("Videoworld*");
        }
        else {
            fileChanged = false;
            setTitle("Videoworld");
        }
    }
    public void removeSubWindow(Window window) {
        subWindows.remove(window);
    }
    public void addNewSubWindow(Window window) {
        subWindows.add(window);
    }
}