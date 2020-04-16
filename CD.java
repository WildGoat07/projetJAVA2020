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
    private UUID id;
    /**
     * Constructor.
     * @param price the price of one day of rental
     * @param title the title of the CD
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
     * @return date of the release
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}