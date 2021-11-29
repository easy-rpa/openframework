package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import static eu.ibagroup.constants.SampleQueries.DROP_TABLE_QUERY;

@Slf4j
@ApTaskEntry(name = "Drop Table", description = "Drop Postgres table")
public class DropTable extends ApTask {

    @Inject
    PostgresService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) -> {
            return ex.executeUpdate(DROP_TABLE_QUERY);
        });
    }
}
