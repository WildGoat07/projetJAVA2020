package data;

import java.util.*;

import java.io.*;

/**
 * Defines a book with an author
 */
public abstract class Book implements Document {
    private UUID id;
    /**
     * title of the book
     */
    protected String title;
    /**
     * author of the book
     */
    protected String author;
    /**
     * the byte array of the image
     */
    protected byte[] imageData;
    /**
     * Constructor.
     * @param author author of the book
     * @param title title of the book
     * @param image image of the book
     */
    protected Book(String author, String title, InputStream image) throws IOException {
        id = UUID.randomUUID();
        this.author = author;
        this.title = title;
        if (image == null)
            imageData = null;
        else
            imageData = image.readAllBytes();
    }
    @Override
    public UUID getID() {
        return id;
    }
    @Override
    public abstract utilities.Price getPrice(long days);
    @Override
    public String getTitle() {
        return title;
    }
    /**
     * Gets the author's name
     * @return the name of the author of this book
     */
    public String getAuthor() {
        return author;
    }

    @Override
    public InputStream getImage() {
        return new ByteArrayInputStream(imageData);
    }
    @Override
    public String toString() {
        return getTitle();
    }
}