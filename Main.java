import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import view.MainWindow;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        File curr = null;
        if (args.length > 0) {
            curr = new File(args[0]);
            if (!curr.exists())
                curr = null;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (InstantiationException e){}
        catch (IllegalAccessException e){}
        catch (UnsupportedLookAndFeelException e){}
        new MainWindow(curr).setVisible(true);
    }
}