import java.util.*;

import utilities.*;

/**
 * Defines an actual dictionnary
 */
public class Dictionnary implements Document {
    /**
     * the language of the dictionnary
     */
    protected Locale language;
    /**
     * the price of one day of rental
     */
    protected Price perDayPrice;
    private UUID id;
    /**
     * the title of the dictionnary
     */
    protected String title;
    /**
     * Constructor.
     * @param language language of this dictionnary
     * @param price price of a day of rental of this dictionnary
     * @param title title of the dictionnary
     */
    public Dictionnary(Locale language, Price price, String title) {
        this.language = language;
        perDayPrice = price;
        id = UUID.randomUUID();
        this.title = title;
    }
    /**
     * Gets the language
     * @return the language of this dictionnary
     */
    public Locale getLanguage() {
        return language;
    }
    @Override
    public Price getPrice(int days) {
        return Price.multiply(perDayPrice, days);
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
}