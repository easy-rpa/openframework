package eu.easyrpa.examples.database.working_with_raw_sql.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Insert Invoice Records")
public class InsertInvoiceRecords extends ApTask {

    private final String INSERT_INVOICES_SQL = "INSERT INTO invoices (invoice_number, invoice_date, customer_name, amount) " +
            "VALUES " +
            "('000001', '2019-07-04', 'At&T', '5000.76'), " +
            "('000002', '2012-02-15', 'Apple', '12320.99'), " +
            "('000003', '2014-11-23', 'IBM', '600.00'), " +
            "('000004', '2011-01-04', 'Verizon', '138.50'), " +
            "('000005', '2021-09-01', 'HP', '25600.00');";

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {

        log.info("Adding of new invoice records using SQL.");
        int res = dbService.withConnection(invoicesDbParams, (c) -> {
            return c.executeInsert(INSERT_INVOICES_SQL);
        });

        log.info("'{}' records have been added successfully.", res);
    }
}
