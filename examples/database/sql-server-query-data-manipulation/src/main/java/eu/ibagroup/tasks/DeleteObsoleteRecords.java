package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.utils.DbUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static eu.ibagroup.constants.Constants.DB_DATE_PATTERN;
import static eu.ibagroup.constants.SampleQueries.DELETE_OBSOLETE_RECORDS_QUERY;

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
        SimpleDateFormat f = new SimpleDateFormat(DB_DATE_PATTERN);
        dbService.withConnection((ex) -> {
            return ex.executeUpdate(DbUtils.formatQuery(DELETE_OBSOLETE_RECORDS_QUERY, f.format(c.getTime())));
        });

    }
}
