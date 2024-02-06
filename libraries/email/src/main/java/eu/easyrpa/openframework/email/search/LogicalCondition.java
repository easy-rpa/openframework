package eu.easyrpa.openframework.email.search;

public class LogicalCondition implements SearchCondition {

    private final SearchQuery query;
    private SearchCondition left;
    private SearchCondition right;
    private final LogicalType logicalType;

    public enum LogicalType {
        AND, OR
    }

    LogicalCondition(SearchQuery query, LogicalType logicalType) {
        this.query = query;
        this.logicalType = logicalType;
    }

    LogicalCondition(SearchQuery query, LogicalType logicalType, SearchCondition right) {
        this.query = query;
        this.logicalType = logicalType;
        setRight(right);
    }

    public ComparisonCondition from() {
        return new ComparisonCondition(this, SearchableField.FROM, null);
    }

    public ComparisonCondition to() {
        return new ComparisonCondition(this, SearchableField.RECIPIENTS, null);
    }

    public ComparisonCondition cc() {
        return new ComparisonCondition(this, SearchableField.CC_RECIPIENTS, null);
    }

    public ComparisonCondition bcc() {
        return new ComparisonCondition(this, SearchableField.BCC_RECIPIENTS, null);
    }

    public ComparisonCondition header(String headerName) {
        return new ComparisonCondition(this, SearchableField.HEADER, headerName);
    }

    public ComparisonCondition date() {
        return new ComparisonCondition(this, SearchableField.DATE, null);
    }

    public ComparisonCondition subject() {
        return new ComparisonCondition(this, SearchableField.SUBJECT, null);
    }

    public ComparisonCondition body() {
        return new ComparisonCondition(this, SearchableField.BODY, null);
    }

    public SearchQuery read() {
        return new ComparisonCondition(this, SearchableField.READ, null).contains(true);
    }

    public SearchQuery unread() {
        return new ComparisonCondition(this, SearchableField.READ, null).contains(false);
    }

    public SearchQuery resent() {
        return new ComparisonCondition(this, SearchableField.RECENT, null).contains(true);
    }

    public SearchCondition getLeft() {
        return left;
    }

    public SearchCondition getRight() {
        return right;
    }

    public LogicalType getLogicalType() {
        return logicalType;
    }

    void setRight(SearchCondition right) {
        this.left = query.getCondition();
        this.right = right;
        query.setCondition(this);
    }

    SearchQuery getQuery() {
        return query;
    }
}
