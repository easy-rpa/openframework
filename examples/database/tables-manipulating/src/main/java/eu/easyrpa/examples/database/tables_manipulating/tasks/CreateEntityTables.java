package eu.easyrpa.examples.database.tables_manipulating.tasks;

import eu.easyrpa.examples.database.tables_manipulating.entity.Invoice;
import eu.easyrpa.examples.database.tables_manipulating.entity.Product;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Create Entity Tables")
@Slf4j
public class CreateEntityTables extends ApTask {

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Creating of tables for entities '{}' and '{}'", Invoice.class.getName(), Product.class.getName());
        dbService.withConnection(invoicesDbParams, (c) -> {
            c.createTableIfNotExists(Invoice.class);
            c.createTableIfNotExists(Product.class);
        });

        log.info("Tables are created successfully.");
    }
}
