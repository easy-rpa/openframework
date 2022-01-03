package eu.ibagroup.easyrpa.examples.database.working_with_transaction.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.working_with_transaction.entity.Invoice;
import eu.ibagroup.easyrpa.examples.database.working_with_transaction.service.InvoicesSupplier;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Add Records Using Transaction")
@Slf4j
public class AddRecordsUsingTransaction extends ApTask {

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        List<Invoice> invoicesToAdd = InvoicesSupplier.provideSampleRecords();

        log.info("Adding of new records to the table within one transaction");
        dbService.withTransaction("testdb", (c) -> {
            for (Invoice invoice : invoicesToAdd) {
                c.create(invoice);
                c.create(invoice.getProducts());
            }
        });

        log.info("Records added to the table successfully.");
    }
}
