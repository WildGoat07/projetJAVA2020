package model;

import java.io.*;
import java.time.LocalDate;
import utilities.*;

/**
 * Defines a CD
 */
public class CD extends Numeric {

    private static final long serialVersionUID = 5054418467274292449L;
    /**
     * the date of release
     */
    protected LocalDate releaseDate;

    /**
     * Constructor.
     * 
     * @param price   the price of one day of rental
     * @param release the day of the release
     * @param title   the title of the CD
     * @param image the image of the CD
     */
    public CD(Price price, LocalDate release, String title, InputStream image) throws IOException {
        super();
        setInitialPrice(price);
        setTitle(title);
        setReleaseDate(release);
        setImage(image);
    }

    /**
     * Gets the date the CD got released
     * 
     * @return date of the release
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    /**
     * Changes the release date of the CD
     * @param date when it is released
     */
    public void setReleaseDate(LocalDate date) {
        releaseDate = date;
    }
}