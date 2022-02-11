package eu.ibagroup.easyrpa.openframework.google.sheets.constants;

/**
 * Helps to identify way of matching value strings during comparing. E.g. within searching of rows on the sheet.
 */
public enum MatchMethod {

    /**
     * Means full matching of strings.
     */
    EXACT,

    /**
     * Means source string should start with compared string.
     */
    START_WITH,

    /**
     * Means source string should contains compared string.
     */
    CONTAINS,

    /**
     * Means source string should match to compared string as regexp.
     */
    REGEXP;

    /**
     * Performs matching of strings based on current method.
     *
     * @param sourceStr   source string.
     * @param comparedStr compared string.
     * @return <code>true</code> if strings are matching and <code>false</code> otherwise.
     */
    public boolean match(String sourceStr, String comparedStr) {
        if (sourceStr == null || comparedStr == null) return false;
        if (this == START_WITH) return sourceStr.startsWith(comparedStr);
        if (this == CONTAINS) return sourceStr.contains(comparedStr);
        if (this == REGEXP) return sourceStr.matches(comparedStr);
        return sourceStr.equals(comparedStr);
    }
}
