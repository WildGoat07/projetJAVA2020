package model;

import java.io.Serializable;
import java.time.*;
import java.util.*;

import utilities.*;

/**
 * Defines an order made by a customer
 */
public final class Order implements Serializable {
    /**
     * Defines a borrowing which is required for an order
     */
    public class Borrowing implements Serializable {
        private static final long serialVersionUID = 1346907893311513483L;
        public Borrowing(Product prod, Price cash$$, long numberOfDays) {
            product = prod;
            price = cash$$;
            days = numberOfDays;
            productId = prod.getID();
        }
        /**
         * Returns the product borrowed
         * @return
         */
        public Product getProduct() {
            return product;
        }
        /**
         * Returns the price of the product at the order time
         * @return
         */
        public Price getPrice() {
            return price;
        }
        /**
         * Returns the number of days of the borrowing
         * @return
         */
        public long getDays() {
            return days;
        }
        transient private Product product;
        private UUID productId;
        private Price price;
        private long days;
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Borrowing)
                return product.equals(((Borrowing)obj).product);
            else
                return false;
        }
    }
    private static final long serialVersionUID = 6157970630117489981L;
    transient private Person customer;
    private final LocalDate beginDate;
    private List<Borrowing> products;
    private final UUID customerID;
    private final Price reduction;
    private final UUID id;
    private boolean loyalReduction;
    /**
     * Constructor.
     * The order has no reduction.
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     */
    public Order(final Person customer, final LocalDate beginDate) {
        this(customer, beginDate, new Price());
    }
    /**
     * Constructor
     * @param customer person who made this order
     * @param beginDate beginning of the rental
     * @param endDate ending of the rental
     * @param reduction negative price for the reduction applied
     */
    public Order(final Person customer, final LocalDate beginDate, final Price reduction) {
        this.customer = customer;
        this.beginDate = beginDate;
        this.reduction = reduction;
        products = new ArrayList<Borrowing>();
        customerID = customer.getID();
        id = UUID.randomUUID();
        loyalReduction = customer.isLoyal();
    }
    /**
     * Returns true if this order has the -10% reduction from the loyalty of the customer
     * @return true if the reduction applies, else otherwise
     */
    public boolean loyalReductionApplied() {
        return loyalReduction;
    }
    /**
     * Gets the date of the beginning of the rental
     * @return date
     */
    public LocalDate getBeginningRental() {
        return beginDate;
    }
    /**
     * Gets the date of the end of the rental for a product
     * @param p product to test
     * @return the ending of the rental
     */
    public LocalDate getEndingBorrowing(Product p) {
        return getBeginningRental().plusDays(Functions.find(products, (b) -> b.getProduct().equals(p)).getDays());
    }
    /**
     * Gets the last date of ending of borrowings for the order
     * @return the last date
     */
    public LocalDate getLastBorrowingDate() {
        return Collections.max(Functions.convert(products, (b) -> getBeginningRental().plusDays(b.getDays())));
    }
    /**
     * Use this to link data after deserialisation
     * @param people list of people to search into
     * @param products list of products to search into
     */
    public void linkData(final List<Person> people, final List<Product> products) {
        for (Borrowing borr : this.products) {
            for (final Product product : products) {
                if (borr.productId.equals(product.getID())) {
                    borr.product = product;
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
    public void addProduct(final Product product, long days) {
        if (!Functions.checkOne(products, (p) -> p.getProduct().equals(product)))
            products.add(new Borrowing(product, product.getPrice(days, getBeginningRental()), days));
    }
    /**
     * Gets a read only list of all the products in the order
     * @return read only list of the products
     */
    public List<Product> getProducts() {
        return Functions.convert(products, (b) -> b.getProduct());
    }
    /**
     * Gets a read only map of the price of every product at the time the order was done
     * @return the map of the prices
     */
    public Map<Product, Price> getPrices() {
        return Functions.toMap(products, (item) -> new Couple<Product, Price>(item.getProduct(), item.getPrice()));
    }
    /**
     * Gets the duration and price of a product
     * @param p product to look for
     * @return the data, or null otherwise
     */
    public Borrowing getProductData(Product p) {
        return Functions.find(products, (b) -> b.getProduct().equals(p));
    }
    /**
     * Gets a read only map of the number of days of every product
     * @return the map of the days
     */
    public Map<Product, Long> getDurations() {
        return Functions.toMap(products, (item) -> new Couple<Product, Long>(item.getProduct(), item.getDays()));
    }
    /**
     * Removes a product from the order
     * @param product product to remove
     */
    public void removeProduct(final Product product) {
        products.removeIf((b) -> b.getProduct().equals(product));
    }
    /**
     * Gets the final price of the order
     * @return price of the current order
     */
    public Price getCost() {
        final Price total = new Price();
        for (final Borrowing borr : products)
            total.add(borr.getPrice());
        if (loyalReduction)
            total.multiply(.9f);
        total.add(reduction);
        if (total.compareTo(new Price()) == -1)
            return new Price();
        else
            return total;
    }
}