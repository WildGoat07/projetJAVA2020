import java.io.File;
import java.io.IOException;

import java.awt.Color;

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
        //https://stackoverflow.com/a/39482204/13270517
        UIManager.put( "control", new Color(64,64,64) );
        UIManager.put( "info", new Color(64,64,64) );
        UIManager.put( "nimbusBase", new Color( 0, 0, 0) );
        UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
        UIManager.put( "nimbusDisabledText", new Color( 100, 100, 100) );
        UIManager.put( "nimbusFocus", new Color(64,64,64) );
        UIManager.put( "nimbusGreen", new Color(176,179,50) );
        UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
        UIManager.put( "nimbusLightBackground", new Color( 30, 30, 30) );
        UIManager.put( "nimbusOrange", new Color(191,98,4) );
        UIManager.put( "nimbusRed", new Color(169,46,34) );
        UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
        UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
        UIManager.put( "text", new Color( 220, 220, 220) );
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        new MainWindow(curr).setVisible(true);
    }
}