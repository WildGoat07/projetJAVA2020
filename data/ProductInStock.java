package data;

import java.io.Serializable;

public class ProductInStock implements Serializable {
    public Product product;
    public int quantity;
    public ProductInStock(Product p, int q) {
        product = p;
        quantity = q;
    }
}