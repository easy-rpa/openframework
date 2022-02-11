package eu.ibagroup.easyrpa.examples.database.working_with_table_records.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.working_with_table_records.entity.Invoice;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@ApTaskEntry(name = "Update Table Records")
@Slf4j
public class UpdateTableRecords extends ApTask {

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        Date oldDate = DateTime.now().minusYears(3).toDate();

        log.info("Check and find outdated invoices in the table");
        dbService.withConnection(invoicesDbParams, (c) -> {
            List<Invoice> outdatedInvoices = c.queryBuilder(Invoice.class)
                    .where().le("invoice_date", oldDate).query();

            log.info("Amount of found invoices: {}", outdatedInvoices.size());

            log.info("Change outdated flag for found invoices");
            for (Invoice outdatedInvoice : outdatedInvoices) {
                outdatedInvoice.setOutdated(true);
            }

            log.info("Update changed invoices in the table");
            c.update(outdatedInvoices);
        });

        log.info("Records in the table updated successfully.");
    }
}
