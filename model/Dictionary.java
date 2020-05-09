package model;

import java.util.*;
import java.io.*;

import utilities.*;

/**
 * Defines an actual dictionary
 */
public class Dictionary extends Document {
    private static final long serialVersionUID = 1646734607289348321L;
    /**
     * the language of the dictionnary
     */
    protected Locale language;
    /**
     * Constructor.
     * @param price price of a day of rental of this dictionnary
     * @param language language of this dictionnary
     * @param title title of the dictionnary
     * @param image image of the dictionnary
     */
    public Dictionary(Price price, Locale language, String title, InputStream image) throws IOException {
        super();
        setLanguage(language);
        setInitialPrice(price);
        setTitle(title);
        setImage(image);
    }
    /**
     * Gets the language
     * @return the language of this dictionnary
     */
    public Locale getLanguage() {
        return language;
    }
    /**
     * Changes the language of the dictionnary
     * @param lang the new language
     */
    public void setLanguage(Locale lang) {
        language = lang;
    }
}