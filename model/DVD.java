package model;

import java.io.*;

import utilities.Price;

/**
 * Defines a DVD
 */
public class DVD extends Numeric {

    private static final long serialVersionUID = 2574503530874367506L;
    /**
     * director of this DVD
     */
    protected String director;

    /**
     * Constructor.
     * @param price price of one day of rental
     * @param director director of this DVD
     * @param title title of the DVD
     * @param image image of this DVD
     */
    public DVD(Price price, String director, String title, InputStream image) throws IOException {
        super();
        setInitialPrice(price);
        setTitle(title);
        setDirector(director);
        setImage(image);
    }
    /**
     * Gets the director
     * @return the director of the DVD
     */
    public String getDirector() {
        return director;
    }
    /**
     * Changes the director of the DVD
     * @param dir the new director
     */
    public void setDirector(String dir) {
        director = dir;
    }
}