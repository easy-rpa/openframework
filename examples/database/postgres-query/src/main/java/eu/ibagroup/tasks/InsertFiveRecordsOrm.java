package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.entity.PostgresInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static eu.ibagroup.ap.Constants.DB_DATE_PATTERN;

@Slf4j
@ApTaskEntry(name = "Print POSTGRES table content sample task", description = "Print 'rpa.invoices' table content")
public class InsertFiveRecordsOrm extends ApTask {

    String q1 = "INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000001', '2008-7-04', 'AT&T', '5000.76');";
    String q2 = "INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000002', '2012-02-15', 'Apple', '12320.99');";
    String q3 = "INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000003', '2014-11-23', 'IBM', '600.00');";
    String q4 = "INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000004', '2011-1-04', 'Verizon', '138.50');";
    String q5 = "INSERT INTO rpa.invoices (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000005', '2021-9-01', 'HP', '25600.00');";

    @Inject
    PostgresService dbService;
    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {

        List<PostgresInvoice> invoicesToAdd = new ArrayList<>();

        invoicesToAdd.add(new PostgresInvoice(10001, new SimpleDateFormat(DB_DATE_PATTERN).parse("2016-01-22"), "IBA", 4500));
        invoicesToAdd.add(new PostgresInvoice(10002, new SimpleDateFormat(DB_DATE_PATTERN).parse("2014-04-03"), "ITPARK", 5400.87));
        invoicesToAdd.add(new PostgresInvoice(10003, new SimpleDateFormat(DB_DATE_PATTERN).parse("2020-12-08"), "SOFTCLUB", 1200.55));
        invoicesToAdd.add(new PostgresInvoice(10004, new SimpleDateFormat(DB_DATE_PATTERN).parse("2011-05-14"), "EPAM", 1000));
        invoicesToAdd.add(new PostgresInvoice(10005, new SimpleDateFormat(DB_DATE_PATTERN).parse("2018-02-06"), "WF", 2400.99));

        List<Integer> affectedRecords = dbService.withTransaction(PostgresInvoice.class, (ex) -> {
            List<Integer> res = new ArrayList();

            for (PostgresInvoice inv : invoicesToAdd) {
                res.add(ex.create(inv));
            }
            return res;
        });


    }
}
