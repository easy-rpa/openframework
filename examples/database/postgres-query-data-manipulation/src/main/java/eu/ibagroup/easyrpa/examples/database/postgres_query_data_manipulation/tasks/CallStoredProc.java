package eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.tasks;

import eu.ibagroup.easyrpa.examples.database.postgres_query_data_manipulation.constants.SampleQueries;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.MessageFormat;

@Slf4j
@ApTaskEntry(name = "Call Stored Procedure", description = "Call Stored Procedure which returns current date")
public class CallStoredProc extends ApTask {

    @Inject
    PostgresService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        final Date[] currentDate = {null};
        dbService.withConnection((ex) -> {
            ResultSet rs = ex.executeQuery(SampleQueries.CALL_PROC_QUERY);
            while (rs.next()) {
                currentDate[0] = rs.getDate("getdate");
                log.info(MessageFormat.format("Stored procedure returned Current Date = {0}", currentDate[0]));
            }
            return currentDate[0];
        });
    }
}
