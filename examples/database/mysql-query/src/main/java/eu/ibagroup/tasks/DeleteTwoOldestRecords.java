package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Delete two Oldest Records", description = "Delete two oldest records from MySQL table")
public class DeleteTwoOldestRecords extends ApTask {
    String query = "DELETE FROM rpa.invoices WHERE invoice_date IS NOT NULL ORDER BY invoice_date ASC LIMIT 2;";
    @Inject
    MySqlService dbService;

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
