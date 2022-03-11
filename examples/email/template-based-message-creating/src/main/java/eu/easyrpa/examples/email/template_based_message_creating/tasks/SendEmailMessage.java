package eu.easyrpa.examples.email.template_based_message_creating.tasks;

import eu.easyrpa.examples.email.template_based_message_creating.emails.BooksPropositionEmail;
import eu.easyrpa.examples.email.template_based_message_creating.entities.Book;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Send Email Message")
@Slf4j
public class SendEmailMessage extends ApTask {

    @Configuration(value = "outbound.email.server")
    private String outboundEmailServer;

    @Configuration(value = "outbound.email.protocol")
    private String outboundEmailProtocol;

    @Configuration(value = "books.proposition.email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private BooksPropositionEmail booksPropositionEmail;

    @Override
    public void execute() {

        log.info("Collect books info");
        List<Book> books = getBooks();

        log.info("Send books proposition email to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, outboundEmailServer, outboundEmailProtocol, emailUserCredentials.getUser());
        booksPropositionEmail.setBooksInfo(books).send();

        log.info("Messages have been sent successfully");
    }

    private List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Thinking in Java", "Bruce Eckel"));
        books.add(new Book("Le avventure di Cipollino", "Giovanni Francesco Rodari"));
        books.add(new Book("War and Peace", "Lev Tolstoy"));
        return books;
    }
}
