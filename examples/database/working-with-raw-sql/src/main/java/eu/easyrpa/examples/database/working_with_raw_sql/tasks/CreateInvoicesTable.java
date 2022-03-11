package eu.easyrpa.examples.database.working_with_raw_sql.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create Invoices Table")
public class CreateInvoicesTable extends ApTask {

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS invoices(" +
            "id  SERIAL PRIMARY KEY, " +
            "invoice_number integer NOT NULL, " +
            "invoice_date date, " +
            "customer_name character varying(45), " +
            "amount double precision, " +
            "outdated boolean default FALSE" +
            ");";

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {

        log.info("Creating of new table using SQL.");
        dbService.withConnection(invoicesDbParams, (c) -> {
            c.executeUpdate(CREATE_TABLE);
        });

        log.info("Table is created successfully.");
    }
}
