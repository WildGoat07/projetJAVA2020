package utilities;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.imageio.ImageIO;

import data.*;

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
     * Uses a function to convert every item in the list into another type
     * @param <T> initial type
     * @param <U> final type
     * @param list list to convert
     * @param fct function to use to convert
     * @return the list with converted items
     */
    public static <T, U> java.util.List<U> convert(Iterable<T> list, Function<T, U> fct) {
        final java.util.List<U> result = new ArrayList<U>();
        list.forEach((p) -> result.add(fct.apply(p)));
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
    public static String getProductType(Product product, boolean french) {
        if (product instanceof Comic)
            return french ? "BD":"Comic";
        if (product instanceof CD)
            return "CD";
        if (product instanceof data.Dictionary)
            return french ? "Dictionnaire":"Dictionary";
        if (product instanceof DVD)
            return "DVD";
        if (product instanceof Novel)
            return french ? "Roman":"Novel";
        if (product instanceof SchoolBook)
            return french ? "Livre scolaire":"School book";
        return null;
    }
}