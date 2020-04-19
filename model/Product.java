package model;

import java.util.*;
import java.io.*;

/**
 * Basic product. Every product has a rental price, an unique identifier and a title
 */
public interface Product extends Serializable {
    /**
     * Returns the price for the rental for a certain amount of days
     * @param days number of days for the rental
     * @return The price
     */
    utilities.Price getPrice(long days);
    /**
     * Returns the unique identifier of this product
     * @return UUID of the product
     */
    UUID getID();
    /**
     * Returns the title of the product
     * @return The title of the product
     */
    String getTitle();
    /**
     * Gets a stream containing the image data of this product
     * @return the image data or null if no image is provided
     */
    InputStream getImage();
}