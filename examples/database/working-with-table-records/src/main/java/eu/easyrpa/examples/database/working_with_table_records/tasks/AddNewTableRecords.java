package eu.easyrpa.examples.database.working_with_table_records.tasks;

import eu.easyrpa.examples.database.working_with_table_records.entity.Invoice;
import eu.easyrpa.examples.database.working_with_table_records.service.InvoicesSupplier;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@ApTaskEntry(name = "Add New Table Records")
@Slf4j
public class AddNewTableRecords extends ApTask {

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        List<Invoice> invoicesToAdd = InvoicesSupplier.provideSampleRecords();

        log.info("Adding of new records to the table that is used to store entity '{}'", Invoice.class.getName());
        dbService.withConnection(invoicesDbParams, (c) -> {
            c.create(invoicesToAdd);
        });

        log.info("Records added to the table successfully.");
    }
}
