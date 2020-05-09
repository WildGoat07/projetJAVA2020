package model;

import java.util.*;

import utilities.*;

import java.io.*;
import java.time.LocalDate;

/**
 * Basic product. Every product has a rental price, an unique identifier and a title
 */
public abstract class Product implements Serializable {
    private static final long serialVersionUID = 8035929775110649027L;
    private UUID id;
    private Map<LocalDate, Price> history;
    /**
     * Constructor.
     */
    protected Product() {
        id = UUID.randomUUID();
        history = new TreeMap<LocalDate,Price>();
    }
    /**
     * title of the book
     */
    protected String title;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    /**
     * The price of the comic for one day of rental
     */
    protected Price initialPrice;
    /**
     * Returns the price for the rental for a certain amount of days
     * @param days number of days for the rental
     * @param when the date at which the price is to look for
     * @return The price
     */
    public Price getPrice(long days, LocalDate when) {
        Price found = Functions.findValue(getHistory(), when);
        if (found == null)
            found = initialPrice;
        return Price.multiply(found, days);
    }
    /**
     * Changes the initial price of the product
     * @param newPrice the new price to set
     */
    public void setInitialPrice(Price newPrice) {
        initialPrice = newPrice;
    }
    /**
     * Change the price at a given time. Will change the history.
     * @param newPrice the new price to set
     * @param when when the change will take effect
     */
    public void changePrice(Price newPrice, LocalDate when) {
        history.put(when, newPrice);
    }
    /**
     * Returns a sorted map by the date of the history of the changes that happened
     * @return the sorted map of the changes
     */
    public Map<LocalDate, Price> getHistory() {
        return new HashMap<LocalDate, Price>(history);
    }
    /**
     * Returns the unique identifier of this product
     * @return UUID of the product
     */
    public UUID getID() {
        return id;
    }
    /**
     * Returns the title of the product
     * @return The title of the product
     */
    public String getTitle() {
        return title;
    }
    /**
     * Changes the image of the product
     * @param image the new image
     * @throws IOException
     */
    public void setImage(InputStream image) throws IOException {
        if (image == null)
            imageData = null;
        else
            imageData = image.readAllBytes();
    }
    /**
     * Changes the title of the product
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Gets a stream containing the image data of this product
     * @return the image data or null if no image is provided
     */
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