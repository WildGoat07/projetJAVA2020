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
    transient private List<Product> products;
    private List<UUID> productIds;
    private UUID customerID;
    private Price reduction;
    private UUID id;
    /**
     * Constructor.
     * The order has no reduction.
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     */
    public Order(Person customer, LocalDate beginDate, LocalDate endDate) {
        this(customer, beginDate, endDate, new Price());
    }
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
        productIds = new ArrayList<UUID>();
        customerID = customer.getID();
        id = UUID.randomUUID();
    }
    /**
     * Gets the date of the beginning of the rental
     * @return date
     */
    public LocalDate getBeginningRental() {
        return beginDate;
    }
    /**
     * Gets the date of the end of the rental
     * @return date
     */
    public LocalDate getEndingRental() {
        return endDate;
    }
    /**
     * Returns the unique identifier of this order
     * @return UUID of the order
     */
    public UUID getID() {
        return id;
    }
    /**
     * Gets the price reduction.
     * @return the reduction
     */
    public Price getReduction() {
        return reduction;
    }
    /**
     * Gets the customer who made the order
     * @return the customer
     */
    public Person getCustomer() {
        return customer;
    }
    /**
     * Adds a new product to the order
     * @param product product to add
     */
    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
            productIds.add(product.getID());
        }
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
        productIds.remove(product.getID());
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
        if (total.compareTo(new Price()) == -1)
            return new Price();
        else
            return total;
    }
}