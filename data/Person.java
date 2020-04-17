package data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Defines a random person
 */
public class Person implements Serializable {
    private UUID id;
    protected String name;
    protected String surname;
    public Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
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
     * Gets the surname of the person
     * @return surname of the person
     */
    public String getSurname() {
        return surname;
    }
}