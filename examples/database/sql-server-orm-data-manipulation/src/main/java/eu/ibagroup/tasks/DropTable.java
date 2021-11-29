package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop MS-SQL table rpa.invoices")
public class DropTable extends ApTask {
    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Drop table");
        dbService.withConnection(MsSqlInvoice.class,
                (ex) -> ex.dropTable(MsSqlInvoice.class));
    }
}
