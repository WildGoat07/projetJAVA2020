package controller;

import model.Product;

import java.io.Serializable;

/**
 * Used to define which product is in stock and how much of it
 */
public class ProductInStock implements Serializable {
    private static final long serialVersionUID = 5925207580461978258L;
    public Product product;
    public int quantity;
    public ProductInStock(Product p, int q) {
        product = p;
        quantity = q;
    }
}