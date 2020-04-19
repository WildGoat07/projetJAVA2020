package model;

import java.io.*;

import utilities.Price;

/**
 * Defines a comic strip
 */
public class Comic extends Book {
    /**
     * The price of the comic for one day of rental
     */
    protected Price pricePerDay;
    /**
     * Constructor.
     * @param price price of one day of rental
     * @param author author of the comic
     * @param title title of the comic
     * @param image image of the comic
     */
    public Comic(Price price, String author, String title, InputStream image) throws IOException {
        super(author, title, image);
        pricePerDay = price;
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(pricePerDay, days);
    }
}