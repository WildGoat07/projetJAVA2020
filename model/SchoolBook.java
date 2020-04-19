package model;

import utilities.Price;
import java.io.*;

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
     * @param title title of the school book
     * @param image image of the school book
     */
    public SchoolBook(Price price, String author, String title, InputStream image) throws IOException {
        super(author, title, image);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(pricePerDay, days);
    }
}