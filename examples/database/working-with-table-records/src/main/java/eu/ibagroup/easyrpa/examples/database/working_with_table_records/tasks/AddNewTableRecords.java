package eu.ibagroup.easyrpa.examples.database.working_with_table_records.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.working_with_table_records.entity.Invoice;
import eu.ibagroup.easyrpa.examples.database.working_with_table_records.service.InvoicesSupplier;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Add New Table Records")
@Slf4j
public class AddNewTableRecords extends ApTask {

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        List<Invoice> invoicesToAdd = InvoicesSupplier.provideSampleRecords();

        log.info("Adding of new records to the table that is used to store entity '{}'", Invoice.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.create(invoicesToAdd);
        });

        log.info("Records added to the table successfully.");
    }
}
