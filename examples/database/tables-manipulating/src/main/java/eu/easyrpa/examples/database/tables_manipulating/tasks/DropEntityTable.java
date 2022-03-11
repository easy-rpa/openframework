package eu.easyrpa.examples.database.tables_manipulating.tasks;

import eu.easyrpa.examples.database.tables_manipulating.entity.Invoice;
import eu.easyrpa.examples.database.tables_manipulating.entity.Product;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Drop Entity Table")
@Slf4j
public class DropEntityTable extends ApTask {

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Dropping of tables for '{}' and '{}'", Invoice.class.getName(), Product.class.getName());
        dbService.withConnection(invoicesDbParams, (c) -> {
            c.dropTable(Product.class);
            c.dropTable(Invoice.class);
        });

        log.info("Table is dropped successfully.");
    }
}
