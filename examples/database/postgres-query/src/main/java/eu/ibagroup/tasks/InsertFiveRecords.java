package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.PostgreSqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@ApTaskEntry(name = "Insert Records Task", description = "Insert 5 records into DB table")
public class InsertFiveRecords extends ApTask {
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
    PostgreSqlService dbService;
    @Output
    int rowsInserted = 0;
    @Override
    public void execute() throws Exception {
        List<String> queries = new ArrayList();
        queries.add(q1);
        queries.add(q2);
        queries.add(q3);
        queries.add(q4);
        queries.add(q5);
        try {
            int[] res = dbService.executeBatch(queries);
            rowsInserted = IntStream.of(res).sum();
        }
        catch(BatchUpdateException e){
            log.info("Can't execute query. Reason: "+ e.getMessage());
        }
        finally {
            dbService.closeConnection();
        }
    }
}
