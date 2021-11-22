package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Drop Table Task", description = "Drop MySQL table")
public class DropTable extends ApTask {
    String query = "DROP TABLE rpa.invoices;";
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection(() -> {
            return dbService.executeUpdate(query);
        });

    }
}
