package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Drop Table Task", description = "Drop MS-SQL table")
public class DropTableTask extends ApTask {
    String query = "DROP TABLE rpa.invoices;";

    @Inject
    SQLServerService dbService;

    @AfterInit
    public void init() {
    }

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
