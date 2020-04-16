import java.util.UUID;
import utilities.Price;

/**
 * Defines a DVD
 */
public class DVD implements Numeric {

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
    transient protected Person director;
    /**
     * Id of the director (for serialisation purpose only)
     */
    protected UUID directorID;
    private UUID id;

    /**
     * Constructor.
     * @param price price of one day of rental
     * @param title title of the DVD
     * @param director director of this DVD
     */
    public DVD(Price price, String title, Person director) {
        pricePerDay = price;
        this.title = title;
        this.director = director;
        directorID = director.getID();
        id = UUID.randomUUID();
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
    public Person getDirector() {
        return director;
    }
}