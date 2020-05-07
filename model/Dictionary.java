package model;

import java.util.*;
import java.io.*;

import utilities.*;

/**
 * Defines an actual dictionary
 */
public class Dictionary implements Document {
    private static final long serialVersionUID = 1646734607289348321L;
    /**
     * the language of the dictionnary
     */
    protected Locale language;
    /**
     * the price of one day of rental
     */
    protected Price perDayPrice;
    private UUID id;
    /**
     * the title of the dictionnary
     */
    protected String title;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    /**
     * Constructor.
     * @param price price of a day of rental of this dictionnary
     * @param language language of this dictionnary
     * @param title title of the dictionnary
     * @param image image of the dictionnary
     */
    public Dictionary(Price price, Locale language, String title, InputStream image) throws IOException {
        this.language = language;
        perDayPrice = price;
        id = UUID.randomUUID();
        this.title = title;
        if (image == null)
            imageData = null;
        else
            imageData = image.readAllBytes();
    }
    /**
     * Gets the language
     * @return the language of this dictionnary
     */
    public Locale getLanguage() {
        return language;
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(perDayPrice, days);
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public InputStream getImage() {
        return imageData != null ? new ByteArrayInputStream(imageData):null;
    }
    @Override
    public String toString() {
        return getTitle();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product)
            return id.equals(((Product)obj).getID());
        else
            return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}