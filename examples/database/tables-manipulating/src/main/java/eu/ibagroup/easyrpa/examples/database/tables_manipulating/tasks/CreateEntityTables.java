package eu.ibagroup.easyrpa.examples.database.tables_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.entity.Invoice;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.entity.Product;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Create Entity Tables")
@Slf4j
public class CreateEntityTables extends ApTask {

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Creating of tables for entities '{}' and '{}'", Invoice.class.getName(), Product.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.createTableIfNotExists(Invoice.class);
            c.createTableIfNotExists(Product.class);
        });

        log.info("Tables are created successfully.");
    }
}
