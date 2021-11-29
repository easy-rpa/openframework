package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.entity.PostgresInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop Postgres table rpa.invoices")
public class DropTable extends ApTask {
    @Inject
    PostgresService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Drop table");
        dbService.withConnection(PostgresInvoice.class,
                (ex) -> ex.dropTable(PostgresInvoice.class));
    }
}
