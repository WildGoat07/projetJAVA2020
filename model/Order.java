package model;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import utilities.Functions;
import utilities.Price;

/**
 * Defines an order made by a customer
 */
public final class Order implements Serializable {
    private static final long serialVersionUID = 6157970630117489981L;
    transient private Person customer;
    private final LocalDate beginDate;
    private final LocalDate endDate;
    transient private HashMap<Product, Price> products;
    private final UUID customerID;
    private final Price reduction;
    private final UUID id;
    private final HashMap<UUID, Price> productIds;
    /**
     * Constructor.
     * The order has no reduction.
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     */
    public Order(final Person customer, final LocalDate beginDate, final LocalDate endDate) {
        this(customer, beginDate, endDate, new Price());
    }
    /**
     * Constructor
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     * @param reduction negative price for the reduction applied
     */
    public Order(final Person customer, final LocalDate beginDate, final LocalDate endDate, final Price reduction) {
        this.customer = customer;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.reduction = reduction;
        products = new HashMap<Product, Price>();
        productIds = new HashMap<UUID, Price>();
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
     * Use this to link data after deserialisation
     * @param people list of people to search into
     * @param products list of products to search into
     */
    public void linkData(final List<Person> people, final List<Product> products) {
        this.products = new HashMap<Product, Price>();
        for (final Map.Entry<UUID, Price> id : productIds.entrySet()) {
            for (final Product product : products) {
                if (id.getKey().equals(product.getID())) {
                    this.products.put(product, id.getValue());
                    break;
                }
            }
        }
        for (final Person person : people) {
            if (person.getID().equals(customerID)) {
                this.customer = person;
                break;
            }
        }
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
    public void addProduct(final Product product) {
        if (!products.containsKey(product)) {
            final long days = beginDate.until(endDate, ChronoUnit.DAYS);
            products.put(product, product.getPrice(days, getBeginningRental()));
            productIds.put(product.getID(), product.getPrice(days, getBeginningRental()));
        }
    }
    /**
     * Gets a read only list of all the products in the order
     * @return read only list of the products
     */
    public Collection<Product> getProducts() {
        return products.keySet();
    }
    /**
     * Gets a read only map of the price of every product at the time the order was done
     * @return the map of the prices
     */
    public Map<Product, Price> getPrices() {
        return Functions.convert(products, (item) -> new Price(item));
    }
    /**
     * Removes a product from the order
     * @param product product to remove
     */
    public void removeProduct(final Product product) {
        products.remove(product);
        productIds.remove(product.getID());
    }
    /**
     * Gets the final price of the order
     * @return price of the current order
     */
    public Price getCost() {
        final Price total = new Price();
        for (final Price price : products.values())
            total.add(price);
        if (customer.isLoyal())
            total.multiply(.9f);
        total.add(reduction);
        if (total.compareTo(new Price()) == -1)
            return new Price();
        else
            return total;
    }
}