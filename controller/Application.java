package controller;

import model.*;
import utilities.Functions;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
/**
 * Defines the company that sells stuff
 */
public class Application {
    private List<ProductInStock> stock;
    private List<Order> orders;
    private List<Person> people;
    private boolean currentAppFrenchVersion;
    private boolean frenchVersion;
    /**
     * Changes the language to either french or english.
     * These changes only applies after creating a new Application (restart)
     * @param value true for french, false for english
     */
    public void setFrench(boolean value) {
        frenchVersion = value;
    }
    /**
     * Gets the current language of the application
     * @return true if french, false for english
     */
    public boolean isCurrentFrench() {
        return currentAppFrenchVersion;
    }
    /**
     * Gets the langage of the application for the next time it will be saved
     * @return true if french, false for english
     */
    public boolean isFrench() {
        return frenchVersion;
    }
    /**
     * Constructor
     */
    public Application() {
        stock = new ArrayList<ProductInStock>();
        orders = new ArrayList<Order>();
        people = new ArrayList<Person>();
        currentAppFrenchVersion = Locale.getDefault().getISO3Language().equals(Locale.FRENCH.getISO3Language());
        frenchVersion = currentAppFrenchVersion;
    }
    /**
     * Gets a list of every registered product
     * @return list of registered products
     */
    public List<Product> getStock() {
        return Functions.convert(stock, (p)->p.product);
    }
    /**
     * Gets the quantity of a single product in stock (not rented) at a specific time
     * @param p product to find
     * @param time the specific time
     * @return the number of products in stock
     */
    public int getProductCountInStock(Product p, LocalDate time) {
        int registered = 0;
        {
            ProductInStock inStock = productExistsInStock(p);
            if (inStock != null)
                registered = inStock.quantity;
        }
        if (registered == 0)
            return 0;
        for (Order order : orders)
            if ((order.getBeginningRental().isBefore(time) || order.getBeginningRental().isEqual(time)) && order.getEndingRental().isAfter(time))
                if (order.getProducts().contains(p))
                    registered--;
        return registered;
    }
    /**
     * Gets the quantity of a single product in stock (not rented) at the current time
     * @param p product to find
     * @return the number of products in stock
     */
    public int getProductCountInStock(Product p) {
        return getProductCountInStock(p, LocalDate.now());
    }
    /**
     * Gets the quantity of a single product (in stock and rented)
     * @param p product to find
     * @return the number of products in this small and decaying dead world
     */
    public int getRegisteredProductCount(Product p) {
        ProductInStock inStock = productExistsInStock(p);
        if (inStock != null)
            return inStock.quantity;
        else
            return 0;
    }
    /**
     * Gets the quantity of a single rented product (not in stock) at a specific time
     * @param p product to find
     * @param time specific time
     * @return the number of rented products
     */
    public int getRentedProductCount(Product p, LocalDate time) {
        return getRegisteredProductCount(p) - getProductCountInStock(p, time);
    }
    /**
     * Gets the quantity of a single rented product (not in stock) at the current time
     * @param p product to find
     * @return the number of rented products
     */
    public int getRentedProductCount(Product p) {
        return getRentedProductCount(p, LocalDate.now());
    }
    /**
     * Gets a list of orders
     * @return list of orders
     */
    public List<Order> getOrders() {
        return new ArrayList<Order>(orders);
    }
    /**
     * Gets a list of people in the world
     * @return a list of people
     */
    public List<Person> getPeople() {
        return new ArrayList<Person>(people);
    }
    /**
     * Adds a person to the world
     * @param p person to add
     */
    public void addPerson(Person p) {
        if (!people.contains(p))
            people.add(p);
    }
    /**
     * Returns true of the person can be removed safely, false otherwise
     * @param p person to try to remove
     * @return true if safe, false if not safe
     */
    public boolean canRemovePerson(Person p) {
        for (Order order : orders)
            if (order.getCustomer().equals(p))
                return false;
        return true;
    }
    /**
     * Sends a specific person in the world into the realms of death where they
     * will burn until they can no longer breath with their burnt lungs
     * @param p person to remove
     * @throws Exception this person is still required somewhere in the orders
     */
    public void removePerson(Person p) throws Exception {
        if (!canRemovePerson(p))
            throw new Exception("This person has already bought something !");
        people.remove(p);
    }
    /**
     * Returns true if the given person exists
     * @param p person to test
     * @return true if it exists, false otherwise
     */
    public boolean personExists(Person p) {
        return people.contains(p);
    }
    /**
     * Adds an order to the list of orders
     * @param o order to add
     * @throws Exception the customer is not registered or a product is either out of stock, or doesn't exist 
     */
    public void addOrder(Order o) throws Exception {
        if (!orders.contains(o)) {
            if (!personExists(o.getCustomer()))
                throw new Exception("The person is missing for this order");
            for (Product product : o.getProducts()) {
                if (productExistsInStock(product) == null)
                    throw new Exception("One of the products of the order doesn't exists");
                if (getLowestStockProduct(product, o.getBeginningRental(), o.getEndingRental()) == 0)
                    throw new Exception("There is no more product in stock");
            }
            orders.add(o);
        }
    }
    private class ProductMovement implements Comparable<ProductMovement> {
        public boolean productIn;
        public LocalDate when;
        public ProductMovement(boolean in, LocalDate date) {
            productIn = in;
            when = date;
        }
        @Override
        public int compareTo(ProductMovement o) {
            return when.compareTo(o.when);
        }
    }
    /**
     * Gets the lowest a product has been in stock, ever
     * @param p product to parse
     * @return the lowest value a product has ever been in stock
     */
    public int getLowestStockProduct(Product p) {
        List<ProductMovement> movements = new ArrayList<ProductMovement>();
        for (Order order : orders)
            if (order.getProducts().contains(p)) {
                movements.add(new ProductMovement(false, order.getBeginningRental()));
                movements.add(new ProductMovement(true, order.getEndingRental()));
            }
        Collections.sort(movements);
        int lowest = getRegisteredProductCount(p);
        int currState = lowest;
        for (ProductMovement productMovement : movements) {
            currState += productMovement.productIn?1:-1;
            if (lowest > currState)
                lowest = currState;
        }
        return lowest;
    }
    /**
     * Gets the lowest a product has been in stock, ever
     * @param p product to parse
     * @param beg the low bound
     * @param end the upper bound
     * @return the lowest value a product has ever been in stock
     */
    public int getLowestStockProduct(Product p, LocalDate beg, LocalDate end) {
        List<ProductMovement> movements = new ArrayList<ProductMovement>();
        for (Order order : orders)
            if (order.getProducts().contains(p)) {
                movements.add(new ProductMovement(false, order.getBeginningRental()));
                movements.add(new ProductMovement(true, order.getEndingRental()));
            }
        Collections.sort(movements);
        int lowest = getRegisteredProductCount(p);
        int currState = lowest;
        boolean entered = false;
        boolean inside = false;
        for (ProductMovement productMovement : movements) {
            if (productMovement.when.isAfter(beg) && !entered) {
                entered = true;
                inside = true;
            }
            if (inside && lowest > currState)
                lowest = currState;
            if (productMovement.when.isAfter(end) && inside)
                inside = false;
            currState += productMovement.productIn?1:-1;
        }
        return lowest;
    }
    /**
     * Removes an order
     * @param o order to remove
     */
    public void removeOrder(Order o) {
        orders.remove(o);
    }
    /**
     * Adds a new product in the stock
     * @param p product to add
     * @param count the quantity of this product
     */
    public void addProduct(Product p, int count) {
        ProductInStock inStock = productExistsInStock(p);
        if (inStock != null)
            inStock.quantity += count;
        else
            stock.add(new ProductInStock(p, count));
    }
    /**
     * Returns true if a product can be removed safely, false otherwise
     * @param p product to try to remove
     * @return true if safe, false if unsafe
     */
    public boolean canRemoveProduct(Product p) {
        return getLowestStockProduct(p) == getRegisteredProductCount(p);
    }
    /**
     * Returns true if a product can be removed safely, false otherwise
     * @param p product to try to remove
     * @param count quantity to remove
     * @return true if safe, false if unsafe
     */
    public boolean canRemoveProduct(Product p, int count) {
        return getLowestStockProduct(p) >= count;
    }
    /**
     * Removes a certain quantity of a product from the stock
     * @param p product to remove
     * @param count quantity to remove
     * @throws Exception there is no product in stock, or not enough
     */
    public void removeProduct(Product p, int count) throws Exception {
        if (canRemoveProduct(p, count))
            productExistsInStock(p).quantity -= count;
        else
            throw new Exception("Not enough product in stock to remove");
    }
    /**
     * Entirely removes a product from the registered products
     * @param p product to remove
     * @throws Exception the product is required in an order
     */
    public void removeProduct(Product p) throws Exception {
        ProductInStock currProduct;
        ListIterator<ProductInStock> it = stock.listIterator();
        while (it.hasNext()) {
            currProduct = it.next();
            if (currProduct.product.equals(p)) {
                if (canRemoveProduct(p)) {
                    it.remove();
                    return;
                }
                else
                    throw new Exception("This product is mentioned in an order");
            }
        }
    }
    private ProductInStock productExistsInStock(Product p) {
        for (ProductInStock inStock : stock)
            if (inStock.product.equals(p))
                return inStock;
        return null;
    }
    /**
     * Returns true if the product is registered
     * @param p product to find
     * @return true if found, false otherwise
     */
    public boolean productExists(Product p) {
        return productExistsInStock(p) != null;
    }
    public void saveToStream(OutputStream stream) throws IOException {
        ObjectOutputStream writer = new ObjectOutputStream(stream);
        writer.writeObject(people);
        writer.writeObject(stock);
        writer.writeObject(orders);
        writer.writeBoolean(frenchVersion);
        writer.flush();
    }
    /**
     * Loads an instance of an application from a stream where it was previously saved
     * @param stream the stream to read from
     * @return the loaded application
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static Application loadFromStream(InputStream stream) throws IOException, ClassNotFoundException {
        Application result = new Application();
        ObjectInputStream reader = new ObjectInputStream(stream);
        result.people = (List<Person>)reader.readObject();
        result.stock = (List<ProductInStock>)reader.readObject();
        result.orders = (List<Order>)reader.readObject();
        for (Order order : result.orders)
            order.linkData(result.people, result.getStock());
        result.currentAppFrenchVersion = reader.readBoolean();
        result.frenchVersion = result.currentAppFrenchVersion;
        return result;
    }
    /**
     * Gets a list of the products available for renting at the current time
     * @return list of available products
     */
    public List<Product> getAvailableProducts() {
        return getAvailableProducts(LocalDate.now());
    }
    /**
     * Gets a list of the products available for renting at a specific time
     * @param time time for the products availability
     * @return list of available products
     */
    public List<Product> getAvailableProducts(LocalDate time) {
        List<Product> result = new ArrayList<Product>();
        for (ProductInStock inStock : stock) {
            int rented = 0;
            for (Order order : orders)
                if (order.getProducts().contains(inStock.product) && time.isAfter(order.getBeginningRental()) && time.isBefore(order.getEndingRental()))
                    rented++;
            if (rented < inStock.quantity)
                result.add(inStock.product);
            }
        return result;
    }
    /**
     * Gets a list of all the rented products at the current time
     * @return list of rented products
     */
    public List<Product> getRentedProducts() {
        return getRentedProducts(LocalDate.now());
    }
    /**
     * Gets a list of all the rented products at a specific time
     * @param time time for the rented products
     * @return list of rented products
     */
    public List<Product> getRentedProducts(LocalDate time) {
        List<Product> result = new ArrayList<Product>();
        for (ProductInStock inStock : stock) {
            int rented = 0;
            for (Order order : orders)
                if (order.getProducts().contains(inStock.product) && (time.isAfter(order.getBeginningRental()) || time.isEqual(order.getBeginningRental())) && time.isBefore(order.getEndingRental()))
                    rented++;
            if (rented > 0)
                result.add(inStock.product);
            }
        return result;
    }
    /**
     * Gets a list of all the registered products that are out of stock at the current time
     * @return list of out of stock products
     */
    public List<Product> geUnavailableProducts() {
        return geUnavailableProducts(LocalDate.now());
    }
    /**
     * Gets a list of all the registered products that are out of stock at a specific time
     * @param time time for the out of stock products
     * @return list of out of stock products
     */
    public List<Product> geUnavailableProducts(LocalDate time) {
        List<Product> result = new ArrayList<Product>();
        for (ProductInStock inStock : stock) {
            int rented = 0;
            for (Order order : orders)
                if (order.getProducts().contains(inStock.product) && (time.isAfter(order.getBeginningRental()) || time.isEqual(order.getBeginningRental())) && time.isBefore(order.getEndingRental()))
                    rented++;
            if (rented == inStock.quantity)
                result.add(inStock.product);
        }
        return result;
    }
}