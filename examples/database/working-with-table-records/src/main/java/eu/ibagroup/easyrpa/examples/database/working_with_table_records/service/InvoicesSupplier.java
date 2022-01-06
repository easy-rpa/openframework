package eu.ibagroup.easyrpa.examples.database.working_with_table_records.service;

import eu.ibagroup.easyrpa.examples.database.working_with_table_records.entity.Invoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoicesSupplier {
    public static List<Invoice> provideSampleRecords() {
        List<Invoice> invoicesToAdd = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Invoice.DB_DATE_FORMAT);
        try {
            invoicesToAdd.add(new Invoice(10001, dateFormat.parse("2021-01-22"), "Sony", 4500));
            invoicesToAdd.add(new Invoice(10002, dateFormat.parse("2014-04-03"), "Lenovo", 5400.87));
            invoicesToAdd.add(new Invoice(10003, dateFormat.parse("2020-12-08"), "DELL", 1200.55));
            invoicesToAdd.add(new Invoice(10004, dateFormat.parse("2011-05-14"), "NEC", 1000));
            invoicesToAdd.add(new Invoice(10005, dateFormat.parse("2018-02-06"), "LG", 2400.99));
        } catch (Exception e) {
            //do nothing
        }
        return invoicesToAdd;
    }
}
