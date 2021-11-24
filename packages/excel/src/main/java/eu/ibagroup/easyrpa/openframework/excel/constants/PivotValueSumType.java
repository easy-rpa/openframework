package eu.ibagroup.easyrpa.openframework.excel.constants;

public enum PivotValueSumType {
    SUM("Sum of"),
    COUNT("Count of"),
    AVERAGE("Average of"),
    MAX("Max of"),
    MIN("Min of"),
    PRODUCT("Product of"),
    COUNT_NUMBERS("Count of"),
    STDDEV("StdDev of"),
    STDDEVP("StdDevp of"),
    VAR("Var of"),
    VARP("Varp of");

    private String prefix;

    PivotValueSumType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
