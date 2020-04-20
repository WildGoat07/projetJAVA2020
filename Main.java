import java.io.*;
import java.time.LocalDate;
import java.util.Locale;

import javax.swing.*;

import model.*;
import view.*;
import controller.*;
import utilities.*;

public class Main {
    public static void main(String[] args) throws Exception {
        File saveFile = new File("app.ser");
        if (!saveFile.exists()) {
            saveFile.createNewFile();
            OutputStream saveStream = new FileOutputStream(saveFile);
            new Application().saveToStream(saveStream);
            saveStream.close();
        }
        InputStream stream = new FileInputStream(saveFile);
        Application currApp = Application.loadFromStream(stream);
        stream.close();
        Locale.setDefault(currApp.isCurrentFrench()?Locale.FRENCH:Locale.ENGLISH);
        new MainWindow(currApp).setVisible(true);
    }
}