package data;

import java.io.Serializable;

/**
 * Used to define which product is in stock and how much of it
 */
public class ProductInStock implements Serializable {
    public Product product;
    public int quantity;
    public ProductInStock(Product p, int q) {
        product = p;
        quantity = q;
    }
}