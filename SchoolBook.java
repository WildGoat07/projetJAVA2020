import utilities.Price;

/**
 * Defines a book used in schools
 */
public class SchoolBook extends Book {
    /**
     * The price of the school book for one day of rental
     */
    protected Price pricePerDay;
    /**
     * Constructor.
     * @param price price of one day of rental
     * @param author author of the school book
     */
    public SchoolBook(Price price, String author) {
        super(author);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(int days) {
        return Price.multiply(pricePerDay, days);
    }
}