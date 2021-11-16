package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.easyrpa.openframework.database.common.DbSession;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Create POSTGRES table", description = "Creates rpa.invoices POSTGRES table")
public class CreateTable  extends ApTask {
    String query = "CREATE TABLE IF NOT EXISTS rpa.invoices\n" +
            "(\n" +
            "    id  SERIAL PRIMARY KEY,\n" +
            "    invoice_number integer NOT NULL,\n" +
            "    invoice_date date,\n" +
            "    customer_name character varying(45),\n" +
            "    amount double precision\n" +
            ")\n" +
            "\n" +
            "TABLESPACE pg_default;\n" +
            "\n" +
            "ALTER TABLE rpa.invoices\n" +
            "    OWNER to postgres;";
    @Inject
    PostgresService dbService;

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
