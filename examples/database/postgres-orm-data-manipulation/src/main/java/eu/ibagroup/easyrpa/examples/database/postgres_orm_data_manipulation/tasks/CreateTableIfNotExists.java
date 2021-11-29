package eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.entity.PostgresInvoice;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create Postgres table", description = "Create Postgres table 'rpa.invoices'")
public class CreateTableIfNotExists extends ApTask {
    @Inject
    PostgresService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Create table");
        dbService.withConnection(PostgresInvoice.class,
                (ex) -> ex.createTableIfNotExists(PostgresInvoice.class));
    }
}
