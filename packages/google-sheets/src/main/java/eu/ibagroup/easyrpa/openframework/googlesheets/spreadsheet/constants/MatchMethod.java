package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet.constants;

public enum MatchMethod {

    EXACT,
    START_WITH,
    CONTAINS,
    REGEXP;

    public boolean match(String sourceStr, String comparedStr) {
        if (sourceStr == null || comparedStr == null) return false;
        if (this == START_WITH) return sourceStr.startsWith(comparedStr);
        if (this == CONTAINS) return sourceStr.contains(comparedStr);
        if (this == REGEXP) return sourceStr.matches(comparedStr);
        return sourceStr.equals(comparedStr);
    }
}
