package eu.easyrpa.examples.database.working_with_different_db.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.ResultSet;

@ApTaskEntry(name = "Work with Several Databases ")
@Slf4j
public class WorkWithSeveralDatabases extends ApTask {

    private static final String SELECT_ALL_INVOICES_SQL = "SELECT * FROM invoices";

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {

        log.info("Execute query to PostgreSQL database");
        dbService.withConnection("postgres.db", (c) -> {
            ResultSet results = c.executeQuery(SELECT_ALL_INVOICES_SQL);
            log.info("Fetched results:");
            while (results.next()) {
                log.info("Invoice Number: {}", results.getString("invoice_number"));
            }
        });

        log.info("Execute query to MySQL database");
        dbService.withConnection("mysql.db", (c) -> {
            ResultSet results = c.executeQuery(SELECT_ALL_INVOICES_SQL);
            log.info("Fetched results:");
            while (results.next()) {
                log.info("Invoice Number: {}", results.getString("invoice_number"));
            }
        });
    }
}
