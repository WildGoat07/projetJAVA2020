import java.io.Serializable;
import java.util.*;

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
}