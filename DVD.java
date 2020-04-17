import java.util.*;
import java.io.*;

import utilities.Price;

/**
 * Defines a DVD
 */
public class DVD implements Numeric {

    /**
     * price of one day of rental
     */
    protected Price pricePerDay;
    /**
     * title of the DVD
     */
    protected String title;
    /**
     * director of this DVD
     */
    transient protected Person director;
    /**
     * Id of the director (for serialisation purpose only)
     */
    protected UUID directorID;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    private UUID id;

    /**
     * Constructor.
     * @param price price of one day of rental
     * @param title title of the DVD
     * @param director director of this DVD
     */
    public DVD(Price price, String title, Person director) {
        pricePerDay = price;
        this.title = title;
        this.director = director;
        directorID = director.getID();
        id = UUID.randomUUID();
    }
    /**
     * Finds the director by its ID in a list of people (use after deserialisation)
     * @param people list of people to search into
     */
    public void linkDirector(List<Person> people) {
        for (Person person : people) {
            if (person.getID().equals(directorID)) {
                director = person;
            }
        }
    }
    @Override
    public Price getPrice(long days) {
        return Price.multiply(days, pricePerDay);
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }
    /**
     * Gets the director
     * @return the director of the DVD
     */
    public Person getDirector() {
        return director;
    }

    @Override
    public InputStream getImage() {
        return new ByteArrayInputStream(imageData);
    }
    /**
     * Changes the image to another one. null to remove the image.
     * @param stream stream containing the image data
     * @throws IOException thrown by the input stream
     */
    public void setImage(InputStream stream) throws IOException {
        if (stream == null)
            imageData = null;
        else
            imageData = stream.readAllBytes();
    }
}