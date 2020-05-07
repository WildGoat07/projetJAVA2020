package model;

import utilities.Price;
import java.io.*;

/**
 * Defines a book used in schools
 */
public class SchoolBook extends Book {
    private static final long serialVersionUID = -7029890188875756311L;

    /**
     * Constructor.
     * 
     * @param price  price of one day of rental
     * @param author author of the school book
     * @param title  title of the school book
     * @param image  image of the school book
     */
    public SchoolBook(Price price, String author, String title, InputStream image) throws IOException {
        super(price, author, title, image);
    }
}