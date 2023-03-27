package eu.easyrpa.openframework.email.service.javax;

import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.search.ComparisonCondition;
import eu.easyrpa.openframework.email.search.LogicalCondition;
import eu.easyrpa.openframework.email.search.SearchCondition;
import eu.easyrpa.openframework.email.search.SearchQuery;
import eu.easyrpa.openframework.email.service.MessageConverter;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.search.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Converts of {@link SearchQuery} to {@link SearchTerm}.
 */
public class SearchTermConverter {

    MessageConverter<Message> messageConverter;

    public SearchTermConverter(MessageConverter<Message> messageConverter) {
        this.messageConverter = messageConverter;
    }

    public SearchTerm convert(SearchQuery query) {
        if (query.getComplexCondition() != null) {
            return new ComplexSearchTerm(query.getComplexCondition());
        }
        return convert(query.getCondition());
    }

    private SearchTerm convert(SearchCondition searchCondition) {
        SearchTerm searchTerm = null;
        if (searchCondition instanceof ComparisonCondition) {
            ComparisonCondition condition = (ComparisonCondition) searchCondition;

            if (condition.getValue() == null) {
                return null;
            }

            switch (condition.getField()) {
                case FROM:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, FromStringTerm::new);
                    break;
                case RECIPIENTS:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, v -> new RecipientStringTerm(Message.RecipientType.TO, v));
                    break;
                case CC_RECIPIENTS:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, v -> new RecipientStringTerm(Message.RecipientType.CC, v));
                    break;
                case BCC_RECIPIENTS:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, v -> new RecipientStringTerm(Message.RecipientType.BCC, v));
                    break;
                case HEADER:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, v -> new HeaderTerm(condition.getFieldName(), v));
                    break;
                case DATE:
                    searchTerm = getDateTerm(condition);
                    break;
                case SUBJECT:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, SubjectTerm::new);
                    break;
                case BODY:
                    searchTerm = getCaseInsensitiveContainsTerm(condition, BodyTerm::new);
                    break;
                case READ:
                    searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), (Boolean) condition.getValue());
                    break;
                case RECENT:
                    searchTerm = new FlagTerm(new Flags(Flags.Flag.RECENT), (Boolean) condition.getValue());
                    break;
            }

            if (condition.isNegative()) {
                searchTerm = new NotTerm(searchTerm);
            }

        } else if (searchCondition instanceof LogicalCondition) {
            LogicalCondition condition = (LogicalCondition) searchCondition;

            SearchTerm leftTerm = convert(condition.getLeft());
            SearchTerm rightTerm = convert(condition.getRight());

            switch (condition.getLogicalType()) {
                case AND:
                    searchTerm = new AndTerm(leftTerm, rightTerm);
                    break;
                case OR:
                    searchTerm = new OrTerm(leftTerm, rightTerm);
                    break;
            }
        }

        return searchTerm;
    }

    private SearchTerm getDateTerm(ComparisonCondition condition) {
        Date date = null;
        Object value = condition.getValue();

        if (value instanceof Date) {
            date = (Date) value;

        } else if (value instanceof LocalDate) {
            date = Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());

        } else if (value instanceof LocalDateTime) {
            date = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
        }

        int comparison = condition.getComparisonType() == ComparisonCondition.ComparisonType.BEFORE
                ? ReceivedDateTerm.LE
                : ReceivedDateTerm.GE;

        return new ReceivedDateTerm(comparison, date);
    }

    private SearchTerm getCaseInsensitiveContainsTerm(ComparisonCondition condition,
                                                      Function<String, SearchTerm> termConstructor) {
        try {
            SearchTerm searchTerm = null;
            if (condition.getComparisonType() == ComparisonCondition.ComparisonType.CONTAINS_ANY) {
                List<?> values = (List<?>) condition.getValue();
                for (Object value : values) {
                    if (searchTerm == null) {
                        searchTerm = termConstructor.apply(value.toString());
                    } else {
                        searchTerm = new OrTerm(searchTerm, termConstructor.apply(value.toString()));
                    }
                }
            } else if (condition.getComparisonType() == ComparisonCondition.ComparisonType.CONTAINS_ALL) {
                List<?> values = (List<?>) condition.getValue();
                for (Object value : values) {
                    if (searchTerm == null) {
                        searchTerm = termConstructor.apply(value.toString());
                    } else {
                        searchTerm = new AndTerm(searchTerm, termConstructor.apply(value.toString()));
                    }
                }
            } else {
                searchTerm = termConstructor.apply(condition.getValue().toString());
            }

            return searchTerm;
        } catch (Exception e) {
            return null;
        }
    }

    public class ComplexSearchTerm extends SearchTerm {

        private final Predicate<EmailMessage> condition;

        public ComplexSearchTerm(Predicate<EmailMessage> condition) {
            this.condition = condition;
        }

        @Override
        public boolean match(Message msg) {
            return condition.test(SearchTermConverter.this.messageConverter.convertToEmailMessage(msg));
        }
    }

}
