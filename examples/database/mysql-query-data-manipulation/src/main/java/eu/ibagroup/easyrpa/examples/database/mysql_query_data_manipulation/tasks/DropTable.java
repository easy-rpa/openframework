package eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop MySQL table")
public class DropTable extends ApTask {

    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) -> ex.executeUpdate(SampleQueries.DROP_TABLE_QUERY));
    }
}
