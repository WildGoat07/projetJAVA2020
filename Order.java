import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import utilities.Price;

/**
 * Defines an order made by a customer
 */
public final class Order implements Serializable {
    transient private Person customer;
    private LocalDate beginDate;
    private LocalDate endDate;
    private List<Product> products;
    private Price reduction;
    /**
     * Constructor
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     * @param reduction negative price for the reduction applied
     */
    public Order(Person customer, LocalDate beginDate, LocalDate endDate, Price reduction) {
        this.customer = customer;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.reduction = reduction;
        products = new ArrayList<Product>();
    }
    /**
     * Adds a new product to the order
     * @param product product to add
     */
    public void addProduct(Product product) {
        if (!products.contains(product))
            products.add(product);
    }
    /**
     * Gets a read only list of all the products in the order
     * @return read only list of the products
     */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }
    /**
     * Removes a product from the order
     * @param product product to remove
     */
    public void removeProduct(Product product) {
        products.remove(product);
    }
    /**
     * Gets the final price of the order
     * @return price of the current order
     */
    public Price getCost() {
        Price total = new Price();
        long days = beginDate.until(endDate, ChronoUnit.DAYS);
        for (Product product : products)
            total.add(product.getPrice(days));
        if (customer instanceof LoyalCustomer)
            total.multiply(.9f);
        total.add(reduction);
        return total;
    }
}