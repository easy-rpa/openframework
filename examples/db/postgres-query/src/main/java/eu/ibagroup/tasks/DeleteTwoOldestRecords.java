package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.PostgreSqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Delete two Oldest Records", description = "Delete two oldest records from MySQL table")
public class DeleteTwoOldestRecords extends ApTask {
    String query = "DELETE FROM rpa.invoices WHERE invoice_date IN (\n" +
            "SELECT invoice_date FROM rpa.invoices ORDER BY invoice_date ASC Limit 2)";
    @Inject
    PostgreSqlService dbService;
    @Output
    int rowsDeleted = 0;
    @Override
    public void execute() throws Exception {
        rowsDeleted = dbService.executeUpdateStatement(query);
        dbService.closeConnection();
    }
}
