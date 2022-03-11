package eu.easyrpa.examples.database.working_with_table_records.tasks;

import eu.easyrpa.examples.database.working_with_table_records.entity.Invoice;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Delete Table Records")
@Slf4j
public class DeleteTableRecords extends ApTask {

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Find invoices marked as outdated in the table");
        dbService.withConnection(invoicesDbParams, (c) -> {
            List<Invoice> outdatedInvoices = c.queryBuilder(Invoice.class)
                    .where().eq("outdated", true).query();

            log.info("Amount of found invoices: {}", outdatedInvoices.size());

            log.info("Delete outdated invoices from the table");
            c.delete(outdatedInvoices);
        });

        log.info("Records deleted from the table successfully.");
    }
}
