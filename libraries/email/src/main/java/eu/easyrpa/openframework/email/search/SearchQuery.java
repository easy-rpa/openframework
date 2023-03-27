package eu.easyrpa.openframework.email.search;

import eu.easyrpa.openframework.email.EmailMessage;

import java.util.function.Predicate;

public class SearchQuery {

    public static ComparisonCondition from() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.FROM, null);
    }

    public static ComparisonCondition to() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.RECIPIENTS, null);
    }

    public static ComparisonCondition cc() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.CC_RECIPIENTS, null);
    }

    public static ComparisonCondition bcc() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.BCC_RECIPIENTS, null);
    }

    public static ComparisonCondition header(String headerName) {
        return new ComparisonCondition(new SearchQuery(), SearchableField.HEADER, headerName);
    }

    public static ComparisonCondition date() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.DATE, null);
    }

    public static ComparisonCondition subject() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.SUBJECT, null);
    }

    public static ComparisonCondition body() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.BODY, null);
    }

    public static SearchQuery read() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.READ, null).contains(true);
    }

    public static SearchQuery unread() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.READ, null).contains(false);
    }

    public static SearchQuery recent() {
        return new ComparisonCondition(new SearchQuery(), SearchableField.RECENT, null).contains(true);
    }

    public static SearchQuery complex(Predicate<EmailMessage> complexCondition) {
        return new SearchQuery(complexCondition);
    }

    private SearchCondition condition;
    private Predicate<EmailMessage> complexCondition;

    SearchQuery() {
    }

    SearchQuery(Predicate<EmailMessage> complexCondition) {
        this.complexCondition = complexCondition;
    }

    public LogicalCondition and() {
        return new LogicalCondition(this, LogicalCondition.LogicalType.AND);
    }

    public SearchQuery and(SearchQuery anotherQuery) {
        new LogicalCondition(this, LogicalCondition.LogicalType.AND, anotherQuery.getCondition());
        return this;
    }

    public LogicalCondition or() {
        return new LogicalCondition(this, LogicalCondition.LogicalType.OR);
    }

    public SearchQuery or(SearchQuery anotherQuery) {
        new LogicalCondition(this, LogicalCondition.LogicalType.OR, anotherQuery.getCondition());
        return this;
    }

    public SearchCondition getCondition() {
        return condition;
    }

    public Predicate<EmailMessage> getComplexCondition() {
        return complexCondition;
    }

    void setCondition(SearchCondition condition) {
        this.condition = condition;
    }
}
