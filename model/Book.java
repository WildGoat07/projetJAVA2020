package model;

import java.util.*;

import java.io.*;
import utilities.*;

/**
 * Defines a book with an author
 */
public abstract class Book implements Document {
    private static final long serialVersionUID = -7216633920174513141L;
    private UUID id;
    /**
     * The price of the comic for one day of rental
     */
    protected Price pricePerDay;
    /**
     * title of the book
     */
    protected String title;
    /**
     * author of the book
     */
    protected String author;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    /**
     * Constructor.
     * @param author author of the book
     * @param title title of the book
     * @param image image of the book
     */
    protected Book(Price price, String author, String title, InputStream image) throws IOException {
        id = UUID.randomUUID();
        pricePerDay = price;
        this.author = author;
        this.title = title;
        if (image == null)
            imageData = null;
        else
            imageData = image.readAllBytes();
    }
    @Override
    public UUID getID() {
        return id;
    }
    @Override
    public String getTitle() {
        return title;
    }
    /**
     * Gets the author's name
     * @return the name of the author of this book
     */
    public String getAuthor() {
        return author;
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
    public Price getPrice(long days) {
        return Price.multiply(pricePerDay, days);
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