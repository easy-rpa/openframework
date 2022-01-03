package eu.ibagroup.easyrpa.examples.database.working_with_transaction.service;

import eu.ibagroup.easyrpa.examples.database.working_with_transaction.entity.Invoice;
import eu.ibagroup.easyrpa.examples.database.working_with_transaction.entity.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoicesSupplier {
    public static List<Invoice> provideSampleRecords() {
        List<Invoice> invoicesToAdd = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Invoice.DB_DATE_FORMAT);
        try {
            Invoice invoice = new Invoice(10001, dateFormat.parse("2021-01-22"), "Sony", 4500);
            invoice.addProduct(new Product("TV", 1));
            invoice.addProduct(new Product("Printer LX", 2));
            invoicesToAdd.add(invoice);

            invoice = new Invoice(10002, dateFormat.parse("2021-01-23"), "Lenovo", 5300);
            invoice.addProduct(new Product("Laptop X1", 1));
            invoicesToAdd.add(invoice);

            invoice = new Invoice(10003, dateFormat.parse("2018-02-06"), "LG", 2400.99);
            invoice.addProduct(new Product("Mobile Phone S100", 10));
            invoicesToAdd.add(invoice);
        } catch (Exception e) {
            //do nothing
        }
        return invoicesToAdd;
    }
}
