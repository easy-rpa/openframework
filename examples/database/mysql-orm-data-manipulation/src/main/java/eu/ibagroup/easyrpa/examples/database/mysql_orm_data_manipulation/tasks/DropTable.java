package eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.mysql_orm_data_manipulation.entity.MySqlInvoice;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop MySQL table rpa.invoices")
public class DropTable extends ApTask {
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Drop table");
        dbService.withConnection(MySqlInvoice.class,
                (ex) -> ex.dropTable(MySqlInvoice.class));
    }
}
