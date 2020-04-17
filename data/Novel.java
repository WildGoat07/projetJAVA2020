package data;

import utilities.Price;
import java.io.*;

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
     * @param title title of the novel
     * @param image of the novel
     */
    public Novel(Price price, String author, String title, InputStream image) throws IOException {
        super(author, title, image);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(pricePerDay, days);
    }
}