package eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create Postgres table", description = "Create Postgres table 'rpa.invoices'")
public class CreateTable extends ApTask {
    @Inject
    PostgresService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) -> ex.executeUpdate(SampleQueries.CREATE_TABLE));
    }
}
