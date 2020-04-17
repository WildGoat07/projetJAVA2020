package data;

import java.util.*;
import java.io.*;

import utilities.*;

/**
 * Defines an actual dictionary
 */
public class Dictionary implements Document {
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
        return new ByteArrayInputStream(imageData);
    }
}