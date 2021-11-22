package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Delete two Oldest Records", description = "Delete two oldest records from MySQL table")
public class DeleteTwoOldestRecords extends ApTask {
    String query = "DELETE FROM rpa.invoices WHERE invoice_date IS NOT NULL ORDER BY invoice_date ASC LIMIT 2;";
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection(() -> {
            return dbService.executeUpdate(query);
        });

    }
}
