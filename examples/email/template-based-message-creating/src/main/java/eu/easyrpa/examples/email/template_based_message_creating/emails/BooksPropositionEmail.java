package eu.easyrpa.examples.email.template_based_message_creating.emails;

import eu.easyrpa.examples.email.template_based_message_creating.entities.Book;
import eu.easyrpa.openframework.email.EmailMessage;
import eu.easyrpa.openframework.email.EmailSender;

import javax.inject.Inject;
import java.util.List;

/**
 * Example of custom Email class that keeps information about available in stock books.
 *
 * @see EmailMessage
 */
public class BooksPropositionEmail extends EmailMessage {

    private static final String TYPE_NAME = "books.proposition.email";

    private static final String TEMPLATE_FILE_PATH = "/email_templates/books_proposition.ftl";

    private static final String SUBJECT = "Books In Stock";

    private List<Book> books;

    /**
     * Construct a new instance of this email.
     * <p>
     * All custom emails have to specify <code>typeName</code> to be possible to  make a difference between
     * configuration parameters related to different emails. Type name is used as prefix for all process
     * configuration parameters that are related only to this email.
     */
    public BooksPropositionEmail() {
        super(TYPE_NAME);
    }

    /**
     * Construct a new instance of this email.
     *
     * @param sender - email sender that used to provide some configuration parameters and send this email.
     */
    @Inject
    public BooksPropositionEmail(EmailSender sender) {
        super(TYPE_NAME, sender);
    }

    /**
     * Used to provide information about available books to this email
     *
     * @param books - information about available books.
     * @return this instance of the class to combine methods calls into chain.
     */
    public BooksPropositionEmail setBooksInfo(List<Book> books) {
        this.books = books;
        return this;
    }

    @Override
    protected void beforeSend() {
        // Specifying the email subject.
        // Here can be any logic based on which the subject can be changed.
        subject(SUBJECT);

        // In case when FTL file has different from UTF-8 encoding here
        // corresponding encoding can be specified.
        charset("UTF-8");

        // Path to corresponding FTL file as body.
        html(TEMPLATE_FILE_PATH);

        // Specifying of properties that are used as inputs for FTL file.
        property("books", books);
    }
}
