import java.io.Serializable;
import java.util.UUID;

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
}