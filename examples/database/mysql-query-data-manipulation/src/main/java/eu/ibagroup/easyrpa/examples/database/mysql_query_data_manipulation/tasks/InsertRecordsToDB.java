package eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Save records in MySQL table", description = "Save records in MySQL table")
public class InsertRecordsToDB extends ApTask {
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        List<String> queries = new ArrayList();
        queries.add(SampleQueries.INSERT_QUERY_1);
        queries.add(SampleQueries.INSERT_QUERY_2);
        queries.add(SampleQueries.INSERT_QUERY_3);
        queries.add(SampleQueries.INSERT_QUERY_4);
        queries.add(SampleQueries.INSERT_QUERY_5);

        List<ResultSet> rs = new ArrayList<>();
        dbService.withTransaction((ex) -> {
            for (String query : queries) {
                rs.add(ex.executeInsert(query));
            }
            return null;
        });
    }
}
