package model;

import java.util.*;
import java.io.*;

import utilities.Price;

/**
 * Defines a DVD
 */
public class DVD implements Numeric {

    private static final long serialVersionUID = 2574503530874367506L;
    /**
     * price of one day of rental
     */
    protected Price pricePerDay;
    /**
     * title of the DVD
     */
    protected String title;
    /**
     * director of this DVD
     */
    protected String director;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    private UUID id;

    /**
     * Constructor.
     * @param price price of one day of rental
     * @param director director of this DVD
     * @param title title of the DVD
     * @param image image of this DVD
     */
    public DVD(Price price, String director, String title, InputStream image) throws IOException {
        pricePerDay = price;
        this.title = title;
        this.director = director;
        id = UUID.randomUUID();
        if (image == null)
            imageData = null;
        else
            imageData = image.readAllBytes();
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(days, pricePerDay);
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
     * Gets the director
     * @return the director of the DVD
     */
    public String getDirector() {
        return director;
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