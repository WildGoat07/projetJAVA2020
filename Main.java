import java.io.File;
import view.MainWindow;

public class Main {
    public static void main(String[] args) throws Exception {
        File curr = null;
        if (args.length > 0) {
            curr = new File(args[0]);
            if (!curr.exists())
                curr = null;
        }
        new MainWindow(curr).setVisible(true);
    }
}