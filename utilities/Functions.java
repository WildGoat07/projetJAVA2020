package utilities;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import model.*;

import java.awt.*;
import java.io.*;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.LocalDate;

/**
 * Utility functions
 */
public class Functions {
    private Functions(){}
    /**
     * Returns a new list containing only the items that check the predicate
     * @param <T> type of the items in the list
     * @param list list of items to check
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
     * Finds the first item in a list based on the predicate
     * @param <T> type of the items in the list
     * @param list list of items to check
     * @param test predicate to test the items
     * @return the found item, or null otherwise
     */
    public static <T> T find(Iterable<T> list, Predicate<T> test) {
        for (T item : list)
            if (test.test(item))
                return item;
        return null;
    }
    /**
     * Check if one of the items is valid.
     * @param <T> type of the items in the list
     * @param list list of items to check
     * @param pred predicate to check every item
     * @return true if any item is valid.
     */
    public static <T> boolean checkOne(Iterable<T> list, Predicate<T> pred) {
        for (T item : list)
            if (pred.test(item))
                return true;
        return false;
    }
    /**
     * Check if all of the items are valid.
     * @param <T> type of the items in the list
     * @param list list of items to check
     * @param pred predicate to check every item
     * @return true if all items are valid.
     */
    public static <T> boolean checkAll(Iterable<T> list, Predicate<T> pred) {
        for (T item : list)
            if (!pred.test(item))
                return false;
        return true;
    }
    /**
     * Returns a new list containing all the items of the first list except for the one in the other
     * @param <T> type of the items in the lists
     * @param list initial data
     * @param toRemove data to remove
     * @return the new shortened list
     */
    public static <T> java.util.List<T> except(Iterable<T> list, Iterable<T> toRemove) {
        final java.util.List<T> result = new ArrayList<T>();
        list.forEach((p) -> {
            boolean contained = false;
            for (T t : toRemove)
                if (t.equals(p)) {
                    contained = true;
                    break;
                }
            if (!contained)
                result.add(p);
        });
        return result;
    }
    /**
     * Returns the black and white version of a color
     * @param c color to convert
     * @return black and white version
     */
    public static Color getBlackAndWhite(Color c) {
        int level = (int)(c.getRed()*.3f + c.getGreen()*.59f + c.getBlue() * .11f);
        return new ColorUIResource(level, level, level);
    }
    /**
     * Returns a color that is on the opposite on the hue circle
     * @param c color to convert
     * @return inverse of the color
     */
    public static Color getInverse(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float hue = hsb[0]+.5f;
        if (hue >= 1)
            hue -= 1;
        return Color.getHSBColor(hue, hsb[1], hsb[2]);
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
     * Uses a function to convert every item in the map into a list
     * @param <T> key type
     * @param <U> value type
     * @param <V> output type
     * @param map map to convert
     * @param fct function used to convert
     * @return the new list
     */
    public static <T, U, V> java.util.List<V> toList(Map<T, U> map, Function<Map.Entry<T, U>,V> fct) {
        java.util.List<V> result = new ArrayList<V>();
        for (Entry<T, U> entry : map.entrySet())
            result.add(fct.apply(entry));
        return result;
    }
    /**
     * Uses a function to convert every item in the list into a map
     * @param <T> item type
     * @param <U> key type
     * @param <V> value type
     * @param list list to convert
     * @param fct function used to convert
     * @return the new map
     */
    public static <T, U, V> Map<U, V> toMap(Iterable<T> list, Function<T, Couple<U, V>> fct) {
        Map<U, V> result = new HashMap<U, V>();
        for (T item : list) {
            Couple<U, V> val = fct.apply(item);
            result.put(val.getKey(), val.getValue());
        }
        return result;
    }
    /**
     * Uses a function to convert every item in the map into another type
     * @param <T> key type
     * @param <U> initial type
     * @param <V> final type
     * @param list list to convert
     * @param fct function to use to convert
     * @return the map with converted items
     */
    public static <T, U, V> Map<T, V> convert(Map<T, U> map, Function<U, V> fct) {
        Map<T, V> result = new HashMap<T, V>();
        for (Entry<T, U> entry : map.entrySet())
            result.put(entry.getKey(), fct.apply(entry.getValue()));
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
        if (product instanceof model.Dictionary)
            return french ? "Dictionnaire":"Dictionary";
        if (product instanceof DVD)
            return "DVD";
        if (product instanceof Novel)
            return french ? "Roman":"Novel";
        if (product instanceof SchoolBook)
            return french ? "Livre scolaire":"School book";
        return null;
    }
    /**
     * Organise vertically the given components
     * @param compos components to organise
     * @return a vertical presentation of the components
     */
    public static JComponent alignVertical(Iterable<Component> compos) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        for (Component jComponent : compos)
            result.add(jComponent);
        return result;
    }
    /**
     * Organise vertically the given components
     * @param compos components to organise
     * @return a vertical presentation of the components
     */
    public static JComponent alignVertical(Component[] compos) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        for (Component jComponent : compos)
            result.add(jComponent);
        return result;
    }
    /**
     * Organise horizontally the given components
     * @param compos components to organise
     * @return an horizontal presentation of the components
     */
    public static JComponent alignHorizontal(Component[] compos) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        for (Component jComponent : compos)
            result.add(jComponent);
        return result;
    }
    /**
     * Organise horizontally the given components
     * @param compos components to organise
     * @return an horizontal presentation of the components
     */
    public static JComponent alignHorizontal(Iterable<Component> compos) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        for (Component jComponent : compos)
            result.add(jComponent);
        return result;
    }
    /**
     * Simplifies a string to make it easily comparable
     * @param input text to parse
     * @return the simplified text
     */
    public static String simplify(String input) {
        return removeDiacritics(input).toLowerCase().replaceAll("[\\s|-|'|_]", "");
    }
    /**
     * Removes any diacritic in the string to normalize it for a comparaison
     * @param input text to parse
     * @return the normalized text
     */
    public static String removeDiacritics(String input) {
        //https://stackoverflow.com/a/3322174/13270517
        return Normalizer.normalize(input, Form.NFD).replaceAll("\\p{M}", "");
    }
    /**
     * Returns the value from a chronological sorted map. It returns null if the date is before any entry or if there are no entry.
     * @param <T> type of the value
     * @param map the sorted map
     * @param when the date to look for
     * @return the value corresponding to the date
     */
    public static <T> T findValue(Map<LocalDate, T> map, LocalDate when) {
        Set<Entry<LocalDate, T>> values = map.entrySet();
        T curr = null;
        for (Entry<LocalDate,T> entry : values) {
            if (entry.getKey().isAfter(when)) {
                return curr;
            }
            else
                curr = entry.getValue();
        }
        return curr;
    }
}