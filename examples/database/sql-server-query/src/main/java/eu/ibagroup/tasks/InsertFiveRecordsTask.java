package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Insert Records Task", description = "Insert 5 records into DB table")
public class InsertFiveRecordsTask extends ApTask {
    @Inject
    SQLServerService dbService;

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
    @Output
    int rowsInserted = 0;

    @Override
    public void execute() throws Exception {
        dbService.withTransaction((ex) -> {
            ex.executeInsert(q1);
            ex.executeInsert(q2);
            ex.executeInsert(q3);
            ex.executeInsert(q4);
            ex.executeInsert(q5);
            return null;
        });
    }
}
