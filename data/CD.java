package data;

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
     * @param title   the title of the CD
     * @param release the day of the release
     */
    public CD(Price price, String title, LocalDate release) {
        pricePerDay = price;
        this.title = title;
        releaseDate = release;
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
        return new ByteArrayInputStream(imageData);
    }
    /**
     * Changes the image to another one. null to remove the image.
     * @param stream stream containing the image data
     * @throws IOException thrown by the input stream
     */
    public void setImage(InputStream stream) throws IOException {
        if (stream == null)
            imageData = null;
        else
            imageData = stream.readAllBytes();
    }
}