import utilities.Price;

/**
 * Defines a comic strip
 */
public class Comic extends Book{
    /**
     * The price of the comic for one day of rental
     */
    protected Price pricePerDay;
    /**
     * Constructor.
     * @param price price of one day of rental
     * @param author author of the comic
     */
    public Comic(Price price, String author) {
        super(author);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(int days) {
        return Price.multiply(pricePerDay, days);
    }
}