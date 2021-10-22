package eu.ibagroup.easyrpa.examples.email.template_based_message_creating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.engine.model.SecretCredentials;
import eu.ibagroup.easyrpa.examples.email.template_based_message_creating.emails.BooksInStockEmail;
import eu.ibagroup.easyrpa.examples.email.template_based_message_creating.entities.Book;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApTaskEntry(name = "Send Email Message")
@Slf4j
public class SendEmailMessage extends ApTask {

    @Configuration(value = "email.service")
    private String emailService;

    @Configuration(value = "email.service.protocol")
    private String emailServiceProtocol;

    @Configuration(value = "books.in.stock.email.recipients")
    private String emailRecipients;

    @Configuration(value = "email.user")
    private SecretCredentials emailUserCredentials;

    @Inject
    private RPAServicesAccessor rpaServices;

    @Override
    public void execute() {

        log.info("Collect books info");
        List<Book> books = getBooks();

        log.info("Send books in stock email to '{}' using service '{}', protocol '{}' and mailbox '{}'.",
                emailRecipients, emailService, emailServiceProtocol, emailUserCredentials.getUser());

        new BooksInStockEmail().setBooksInfo(books)
                .service(emailService).serviceProtocol(emailServiceProtocol)
                .credentials(emailUserCredentials.getUser(), emailUserCredentials.getPassword())
                .recipients(emailRecipients)
                .send();

        log.info("Send the same message using RPA services accessor.");

        new BooksInStockEmail(rpaServices).setBooksInfo(books).send();

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
