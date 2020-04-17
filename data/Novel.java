package data;

import utilities.Price;

/**
 * Defines a novel
 */
public class Novel extends Book {

    /**
     * The price of the novel for one day of rental
     */
    protected Price pricePerDay;
    /**
     * Constructor.
     * @param price price of one day of rental
     * @param author author of the novel
     */
    public Novel(Price price, String author) {
        super(author);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(pricePerDay, days);
    }
}