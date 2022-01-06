package eu.ibagroup.easyrpa.examples.database.working_with_raw_sql.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Delete Outdated Invoices")
@Slf4j
public class DeleteOutdatedInvoices extends ApTask {

    private final String DELETE_INVOICES_SQL = "DELETE FROM invoices WHERE outdated='TRUE';";

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Delete invoices marked as outdated using SQL statement.");
        int res = dbService.withConnection("testdb", (c) -> {
            return c.executeDelete(DELETE_INVOICES_SQL);
        });

        log.info("'{}' records have been deleted.", res);
    }
}
