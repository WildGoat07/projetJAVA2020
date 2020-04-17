package utilities;

import java.util.*;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

/**
 * Utility functions
 */
public class Functions {
    private Functions(){}
    /**
     * Returns a new list containing only the items that check the predicate
     * @param <T> type of the items in the list
     * @param list list of items do check
     * @param test predicate to test every item
     * @return the new list after the predicate test
     */
    public static <T> java.util.List<T> where(Iterable<T> list, Predicate<T> test) {
        final java.util.List<T> result = new ArrayList<T>();
        list.forEach((p) -> {
            if (test.test(p))
                result.add(p);
        });
        return result;
    }
    /**
     * Resizes an image
     * @param x width of the image
     * @param y height of the image
     * @param imagePath path to the image
     * @return the scaled image
     * @throws IOException
     */
    public static Image resizeImage(int width, int height, String imagePath) throws IOException {
        InputStream stream = new FileInputStream(imagePath);
        Image result = resizeImage(width, height, stream);
        stream.close();
        return result;
    }
    /**
     * Resizes an image
     * @param x width of the image
     * @param y height of the image
     * @param imageData stream of the image
     * @return the scaled image
     * @throws IOException
     */
    public static Image resizeImage(int width, int height, InputStream imageData) throws IOException  {
        return resizeImage(width, height, ImageIO.read(imageData));
    }
    /**
     * Resizes an image
     * @param x width of the image
     * @param y height of the image
     * @param img the image
     * @return the scaled image
     * @throws IOException
     */
    public static Image resizeImage(int width, int height, Image img) {
        return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}