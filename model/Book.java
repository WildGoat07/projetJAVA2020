package model;

import java.io.*;

import utilities.*;

/**
 * Defines a book with an author
 */
public abstract class Book extends Document {
    private static final long serialVersionUID = -7216633920174513141L;
    /**
     * author of the book
     */
    protected String author;
    /**
     * Constructor.
     * @param price the initial price of the book
     * @param author author of the book
     * @param title title of the book
     * @param image image of the book
     */
    protected Book(Price price, String author, String title, InputStream image) throws IOException {
        super();
        setInitialPrice(price);
        setAuthor(author);
        setTitle(title);
        setImage(image);
    }
    /**
     * Gets the author's name
     * @return the name of the author of this book
     */
    public String getAuthor() {
        return author;
    }
    /**
     * Changes the author of the book
     * @param author the new author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

}