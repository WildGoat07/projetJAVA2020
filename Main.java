import java.io.*;
import java.util.Locale;

import data.*;
import gui.*;
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
        /*Application currApp = new Application();
        currApp.addProduct(new Comic(new Price(5), "Georges", "une bd", null), 2);
        currApp.addProduct(new Dictionary(new Price(2.5), Locale.ENGLISH, "un dico", null), 2);
        currApp.addProduct(new Novel(new Price(3.8), "Manuel", "un super roman", null), 2);
        currApp.addProduct(new DVD(new Price(9), "Henri", "compilation naze", null), 2);*/
        new MainWindow(currApp).setVisible(true);
    }
}