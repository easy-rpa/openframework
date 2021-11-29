package eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import static eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.constants.SampleQueries.DROP_TABLE_QUERY;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop MS-SQL table")
public class DropTable extends ApTask {

    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) ->
                ex.executeUpdate(DROP_TABLE_QUERY)
        );
    }
}
