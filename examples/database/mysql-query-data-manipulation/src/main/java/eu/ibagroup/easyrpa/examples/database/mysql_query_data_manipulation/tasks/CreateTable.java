package eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

import static eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.constants.SampleQueries.CREATE_TABLE;

@Slf4j
@ApTaskEntry(name = "Create MySQL table", description = "Create MySQL table 'rpa.invoices'")
public class CreateTable extends ApTask {
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) -> ex.executeUpdate(CREATE_TABLE));
    }
}
