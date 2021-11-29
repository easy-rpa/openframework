package eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.utils.DbUtils;
import eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.constants.Constants;
import eu.ibagroup.easyrpa.examples.database.sql_server_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@ApTaskEntry(name = "Delete Obsolete Records", description = "Delete obsolete records from MS-SQL table")
public class DeleteObsoleteRecords extends ApTask {
    @Configuration(value = "ap.data.validity_years")
    private String yearsOfInterest;

    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, Integer.valueOf(yearsOfInterest) * -1);
        SimpleDateFormat f = new SimpleDateFormat(Constants.DB_DATE_PATTERN);
        dbService.withConnection((ex) ->
                ex.executeUpdate(DbUtils.formatQuery(SampleQueries.DELETE_OBSOLETE_RECORDS_QUERY, f.format(c.getTime())))
        );

    }
}
