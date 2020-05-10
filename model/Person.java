package model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Defines a random person
 */
public class Person implements Serializable {
    private static final long serialVersionUID = -6740881342355238333L;
    private UUID id;
    protected String name;
    protected String surname;
    protected boolean loyal;
    /**
     * Constructor.
     * @param name name of the customer
     * @param surname surname of the customer
     * @param loyal true if the customer is loyal
     */
    public Person(String name, String surname, boolean loyal) {
        this.name = name;
        this.surname = surname;
        this.loyal = loyal;
        id = UUID.randomUUID();
    }
    /**
     * Returns the unique identifier of this person
     * @return UUID of the person
     */
    public UUID getID() {
        return id;
    }
    /**
     * Gets the name of the person
     * @return name of the person
     */
    public String getName() {
        return name;
    }
    /**
     * Returns true if the customer is loyal
     * @return true if loyal, else otherwise
     */
    public boolean isLoyal() {
        return loyal;
    }
    /**
     * Changes the loyalty of a customer
     * @param loyal true if loyal, false otherwise
     */
    public void setLoyal(boolean loyal) {
        this.loyal = loyal;
    }
    /**
     * Gets the surname of the person
     * @return surname of the person
     */
    public String getSurname() {
        return surname;
    }
    @Override
    public String toString() {
        return name + " " + surname;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person)
            return id.equals(((Person)obj).id);
        else
            return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}