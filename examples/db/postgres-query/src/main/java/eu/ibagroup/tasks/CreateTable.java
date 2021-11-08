package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.PostgreSqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Create MySQL table", description = "Creates rpa.invoices MySQL table")
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
    PostgreSqlService dbService;
    @Output()
    private boolean isCreated = false;
    @Override
    public void execute() throws Exception {
        try{
            dbService.executeStatement(query);
            this.isCreated = true;
        }
        catch(SQLSyntaxErrorException e){
            log.info("Can't execute query. Reason: "+ e.getMessage());
        }
        dbService.closeConnection();
    }
}
