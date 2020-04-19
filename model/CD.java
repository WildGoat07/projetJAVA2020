package model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import utilities.*;

/**
 * Defines a CD
 */
public class CD implements Numeric {

    /**
     * the price of one day of rental
     */
    protected Price pricePerDay;
    /**
     * the title of the CD
     */
    protected String title;
    /**
     * the date of release
     */
    protected LocalDate releaseDate;
    /**
     * the data array of the image used
     */
    protected byte[] imageData;
    private UUID id;

    /**
     * Constructor.
     * 
     * @param price   the price of one day of rental
     * @param release the day of the release
     * @param title   the title of the CD
     * @param image the image of the CD
     */
    public CD(Price price, LocalDate release, String title, InputStream image) throws IOException {
        pricePerDay = price;
        this.title = title;
        releaseDate = release;
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
     * Gets the date the CD got released
     * 
     * @return date of the release
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
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