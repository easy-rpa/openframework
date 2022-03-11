package eu.easyrpa.openframework.excel.constants;

/**
 * Represents type of calculation for value fields in Pivot Table.
 */
public enum PivotValueSumType {

    /**
     * Represents the pivot table's <code>Sum</code> function that totals all underlying values for each
     * item in the field.
     */
    SUM("Sum of"),

    /**
     * Represents the pivot table's <code>Count</code> function that counts all underlying values for each
     * item in the field.
     */
    COUNT("Count of"),

    /**
     * Represents the pivot table's <code>Average</code> function that totals all underlying values in
     * the Values area and divides by the number of values.
     */
    AVERAGE("Average of"),

    /**
     * Represents the pivot table's <code>Max</code> function that shows the maximum value from underlying
     * values in the Values area.
     */
    MAX("Max of"),

    /**
     * Represents the pivot table's <code>Min</code> function that shows the minimum value from underlying
     * values in the Values area.
     */
    MIN("Min of"),

    /**
     * Represents the pivot table's <code>Product</code> function that shows the result of multiplying all
     * underlying values in the Values area.
     */
    PRODUCT("Product of"),

    /**
     * Represents the pivot table's <code>Count Numbers</code> function that counts all the underlying numbers
     * in the Values area.
     */
    COUNT_NUMBERS("Count of"),

    /**
     * Represents the pivot table's <code>StdDev</code> function that calculate the standard deviation for
     * the underlying data in the Values area
     */
    STDDEV("StdDev of"),

    /**
     * Represents the pivot table's <code>StdDevp</code> function that calculate the standard deviation for
     * the underlying data in the Values area
     */
    STDDEVP("StdDevp of"),

    /**
     * Represents the pivot table's <code>Var</code> function that calculate the variance for the underlying
     * data in the Values area.
     */
    VAR("Var of"),

    /**
     * Represents the pivot table's <code>Varp</code> function that calculate the variance for the underlying
     * data in the Values area.
     */
    VARP("Varp of");

    /**
     * Prefix string that is used to get default display name of value field in Pivot Table.
     */
    private String prefix;

    PivotValueSumType(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets related prefix that is used to get default display name of value field in Pivot Table.
     *
     * @return prefix string.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets custom prefix for this summary type that is used for default display name of value field in Pivot Table.
     *
     * @param prefix custom prefix string.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
