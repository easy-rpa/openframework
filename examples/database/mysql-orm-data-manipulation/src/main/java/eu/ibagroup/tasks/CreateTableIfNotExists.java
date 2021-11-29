package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import eu.ibagroup.entity.MySqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create MySQL table", description = "Create MySQL table 'rpa.invoices'")
public class CreateTableIfNotExists extends ApTask {
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Create table");
        dbService.withConnection(MySqlInvoice.class,
                (ex) -> ex.createTableIfNotExists(MySqlInvoice.class));
    }
}
