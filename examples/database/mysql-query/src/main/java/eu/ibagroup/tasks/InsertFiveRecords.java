package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Insert Records Task", description = "Insert 5 records into DB table")
public class InsertFiveRecords extends ApTask {
    String q1 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000001', '2008-7-04', 'At&T', '5000.76');";
    String q2 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000002', '2012-02-15', 'Apple', '12320.99');";
    String q3 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000003', '2014-11-23', 'IBM', '600.00');";
    String q4 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000004', '2011-1-04', 'Verizon', '138.50');";
    String q5 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000005', '2021-9-01', 'HP', '25600.00');";

    @Inject
    MySqlService dbService;
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
        List<ResultSet> rs = new ArrayList<>();
        dbService.withConnection(() -> {
            for (String query : queries) {
                rs.add(dbService.executeInsert(query));
            }
            return rs;
        });

    }
}