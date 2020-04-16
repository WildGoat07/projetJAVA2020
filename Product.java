import java.io.Serializable;
import java.util.UUID;

/**
 * Basic product. Every product has a location price, an unique identifier and a title
 */
public interface Product extends Serializable {
    /**
     * Returns the price for the location for a certain amount of days
     * @param days number of days for the location
     * @return The price
     */
    utilities.Price getPrice(int days);
    /**
     * Returns the unique identifier of this product
     */
    UUID getID();
    /**
     * Returns the title of the product
     * @return The title of the product
     */
    String getTitle();

}