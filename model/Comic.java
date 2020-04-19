package model;

import java.io.*;

import utilities.Price;

/**
 * Defines a comic strip
 */
public class Comic extends Book {
    /**
     * Constructor.
     * @param price price of one day of rental
     * @param author author of the comic
     * @param title title of the comic
     * @param image image of the comic
     */
    public Comic(Price price, String author, String title, InputStream image) throws IOException {
        super(price, author, title, image);
    }
}