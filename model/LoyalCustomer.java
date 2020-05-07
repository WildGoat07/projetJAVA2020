package model;

/**
 * A loyal customer has reductions
 */
public class LoyalCustomer extends Person {

    private static final long serialVersionUID = 7618202633648838159L;

    /**
     * Constructor
     * 
     * @param name    name of the customer
     * @param surname surname of the customer
     */
    public LoyalCustomer(String name, String surname) {
        super(name, surname);
    }
}