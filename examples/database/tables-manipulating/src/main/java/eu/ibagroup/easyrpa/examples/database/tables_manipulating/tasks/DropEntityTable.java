package eu.ibagroup.easyrpa.examples.database.tables_manipulating.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.entity.Invoice;
import eu.ibagroup.easyrpa.examples.database.tables_manipulating.entity.Product;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@ApTaskEntry(name = "Drop Entity Table")
@Slf4j
public class DropEntityTable extends ApTask {

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        log.info("Dropping of tables for '{}' and '{}'", Invoice.class.getName(), Product.class.getName());
        dbService.withConnection("testdb", (c) -> {
            c.dropTable(Product.class);
            c.dropTable(Invoice.class);
        });

        log.info("Table is dropped successfully.");
    }
}
