package controller;

import model.Product;
import java.time.LocalDate;
import java.util.*;

import java.io.Serializable;

/**
 * Used to define which product is in stock and how much of it
 */
public class ProductInStock implements Serializable {
    private static final long serialVersionUID = 5925207580461978258L;
    public Product product;
    public Map<LocalDate, Integer> quantities;
    public ProductInStock(Product p) {
        product = p;
        quantities = new TreeMap<LocalDate, Integer>();
    }
}