package utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Defines a price
 */
public class Price extends Number implements Comparable<Price> {
    /**
     * euros of this price
     */
    protected int euros;
    /**
     * cents of this price
     */
    protected int cents;
    /**
     * true if the price is negative
     */
    protected boolean negative;
    /**
     * Constructor.
     */
    public Price() {
        setEuros(0);
        setCents(0);
        negative = false;
    }
    /**
     * Copy constructor.
     * @param copy price to copy from
     */
    public Price(Price copy) {
        setEuros(copy.getEuros());
        setCents(copy.getCents());
        setNegative(copy.isNegative());
    }
    /**
     * Constructor.
     * @param euros euros of the price to create.
     */
    public Price(int euros) {
        this((float)euros);
    }
    /**
     * Constructor.
     * @param euros euros of the price to create.
     * @param cents cents of the price to create.
     */
    public Price(int euros, int cents) {
        this(euros + .01f * cents);
    }
    /**
     * Constructor.
     * @param euros euros of the price to create.
     */
    public Price(float euros) {
        setNegative(euros < 0);
        euros = Math.abs(euros);
        setEuros((int)euros);
        setCents((int)Math.round((euros-getEuros())*100));
    }
    /**
     * Constructor.
     * @param euros euros of the price to create.
     */
    public Price(double euros) {
        this((float)euros);
    }
    /**
     * Returns the euros of the price.
     * @return the euros without cents.
     */
    public int getEuros() {
        return euros;
    }
    /**
     * Returns the cents of the price.
     * @return the cents.
     */
    public int getCents() {
        return cents;
    }
    /**
     * Changes the cents
     * if the value is out of the [0-99] range, the euros will be changed too
     * @param cents new value of cents
     */
    public void setCents(int cents) {
        int euros = cents / 100;
        if (cents < 0)
            setEuros(getEuros()+euros - 1);
        else
            setEuros(getEuros()+euros);
        this.cents = ((cents%100)+100)%100;
    }
    /**
     * Changes the euros
     * if the value is negative, it will also change the sign of the price
     * @param euros new value of euros
     */
    public void setEuros(int euros) {
        if (euros >= 0) {
            setNegative(false);
            this.euros = euros;
        }
        else {
            setNegative(true);
            this.euros = -euros;
        }
    }
    /**
     * Returns the sign of the price.
     * @return true if the price is negative, false otherwise
     */
    public boolean isNegative() {
        return negative;
    }
    /**
     * Use this to change the sign of the price
     * @param negative true if the price have to be negative
     */
    protected void setNegative(boolean negative) {
        this.negative = negative;
    }
    /**
     * Inverts the sign of the price.
     */
    public void invert() {
        setNegative(!isNegative());
    }
    /**
     * Inverts the sign of the price.
     * @param price price to invert
     * @return a new price with its sign inverted
     */
    public static Price invert(Price price) {
        Price result = new Price(price);
        result.invert();
        return result;
    }
    /**
     * Adds another price to the current one
     * @param other other price to add from.
     */
    public void add(Price other) {
        System.out.println(this + ":" + other);
        float value = floatValue()+other.floatValue();
        setNegative(value < 0);
        value = Math.abs(value);
        setEuros((int)value);
        setCents((int)Math.round((value - getEuros())*100));
    }
    /**
     * Subtracts another price to the current one
     * @param other other price to subtract from.
     */
    public void sub(Price other) {
        add(invert(other));
    }
    /**
     * Create a new price that is the addition of two others
     * @param left left operand of the addition
     * @param right right operand of the addition
     * @return the new added price
     */
    public static Price add(Price left, Price right) {
        Price result = new Price(left);
        result.add(right);
        return result;
    }
    /**
     * Create a new price that is the subtraction of two others
     * @param left left operand of the subtraction
     * @param right right operand of the subtraction
     * @return the new subtraced price
     */
    public static Price sub(Price left, Price right) {
        Price result = new Price(left);
        result.sub(right);
        return result;
    }
    /**
     * Divide two prices
     * @param left price to divide
     * @param right relative price to divide from
     * @return the percentage of the left according to the right
     */
    public static float divide(Price left, Price right) {
        return left.floatValue() / right.floatValue();
    }
    /**
     * Multiplies the current price by a value
     * @param other value to multiply with
     */
    public void multiply(float other) {
        float value = floatValue()*other;
        setNegative(value < 0);
        value = Math.abs(value);
        setEuros((int)value);
        setCents((int)Math.round((value - getEuros())*100));
    }
    /**
     * Divides the current price by a value
     * @param other value to divide with
     */
    public void divide(float other) {
        multiply(1/other);
    }
    /**
     * Creates a new price that is the multiplication of one price and a value
     * @param left left operand of the multiplication
     * @param right right operand of the multiplication
     * @return the new multiplied price
     */
    public static Price multiply(Price left, float right) {
        Price result = new Price(left);
        result.multiply(right);
        return result;
    }
    /**
     * Creates a new price that is the multiplication of one price and a value
     * @param left left operand of the multiplication
     * @param right right operand of the multiplication
     * @return the new multiplied price
     */
    public static Price multiply(float left, Price right) {
        return multiply(right, left);
    }
    /**
     * Creates a new that is the division of one price by a value
     * @param left left operand of the division
     * @param right right operand of the division
     * @return the new divided price
     */
    public static Price divide(Price left, float right) {
        Price result = new Price(left);
        result.divide(right);
        return result;
    }
    @Override
    public int intValue() {
        return getEuros() * (isNegative()?-1:1);
    }

    @Override
    public long longValue() {
        return getEuros() * (isNegative()?-1:1);
    }

    @Override
    public float floatValue() {
        return (getEuros() + .01f * getCents())* (isNegative()?-1:1);
    }

    @Override
    public double doubleValue() {
        return (getCents() + .01 * getCents())* (isNegative()?-1:1);
    }
    @Override
    public String toString() {
        return toString("€");
    }
    /**
     * Converts the price to a string with a custom money icon
     * @param icon icon to use for the conversion (€, $, £, ...)
     * @return a string representing the price
     */
    public String toString(String icon) {
        NumberFormat formatter = new DecimalFormat("00");
        return (isNegative()?"-":"") + getEuros() + "." + formatter.format(getCents()) + " " + icon;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Price) {
            Price other = (Price)obj;
            return compareTo(other) == 0;
        }
        else
            return super.equals(obj);
    }
    @Override
    public int compareTo(Price other) {
        return Float.valueOf(floatValue()).compareTo(other.floatValue());
    }
}