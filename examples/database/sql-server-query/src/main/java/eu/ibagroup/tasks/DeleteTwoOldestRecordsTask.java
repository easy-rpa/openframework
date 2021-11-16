package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Delete two Oldest Records", description = "Delete two oldest records from MS-SQL table")
public class DeleteTwoOldestRecordsTask extends ApTask {
    String query = "DELETE FROM rpa.invoices WHERE invoice_date IN (\n" +
            "SELECT TOP 2 invoice_date FROM rpa.invoices ORDER BY invoice_date ASC)";
    @Inject
    SQLServerService dbService;
    @Output
    int rowsDeleted = 0;
    @Override
    public void execute() throws Exception {
        try (DbSession session = dbService.initPureConnection().getSession()) {
            session.executeUpdate(query);
        }
        catch(SQLSyntaxErrorException e){
            log.info("Can't execute query. Reason: "+ e.getMessage());
        }
    }
}