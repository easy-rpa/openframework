package eu.easyrpa.openframework.email.search;

import java.util.List;

public class ComparisonCondition implements SearchCondition {

    private SearchQuery query;
    private LogicalCondition logicalCondition;
    private final SearchableField field;

    private final String fieldName;
    private ComparisonType comparisonType;
    private Object value;

    private boolean negative;

    public enum ComparisonType {
        CONTAINS, CONTAINS_ANY, CONTAINS_ALL, BEFORE, AFTER
    }

    ComparisonCondition(SearchQuery query, SearchableField field, String fieldName) {
        this.query = query;
        this.field = field;
        this.fieldName = fieldName;
    }

    ComparisonCondition(LogicalCondition logicalCondition, SearchableField field, String fieldName) {
        this.logicalCondition = logicalCondition;
        this.field = field;
        this.fieldName = fieldName;
    }

    public SearchQuery contains(Object value) {
        this.comparisonType = ComparisonType.CONTAINS;
        this.value = value;
        return completeSearchQuery();
    }

    public SearchQuery containsAny(List<?> values) {
        this.comparisonType = ComparisonType.CONTAINS_ANY;
        this.value = values;
        return completeSearchQuery();
    }

    public SearchQuery containsAll(List<?> values) {
        this.comparisonType = ComparisonType.CONTAINS_ALL;
        this.value = values;
        return completeSearchQuery();
    }

    public SearchQuery before(Object value) {
        this.comparisonType = ComparisonType.BEFORE;
        this.value = value;
        return completeSearchQuery();
    }

    public SearchQuery after(Object value) {
        this.comparisonType = ComparisonType.AFTER;
        this.value = value;
        return completeSearchQuery();
    }

    public ComparisonCondition not() {
        negative = true;
        return this;
    }

    public SearchableField getField() {
        return field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public Object getValue() {
        return value;
    }

    public boolean isNegative() {
        return negative;
    }

    private SearchQuery completeSearchQuery() {
        if (query != null) {
            query.setCondition(this);
            return query;
        }
        logicalCondition.setRight(this);
        return logicalCondition.getQuery();
    }

}
