package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.MsSqlServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.SQLSyntaxErrorException;

@Slf4j
@ApTaskEntry(name = "Create MS SQL SERVER table", description = "Creates rpa.invoices SQL SERVER table")
public class CreateTable  extends ApTask {
    String query = "if not exists (select * from sysobjects where name='invoices' and xtype='U') \n"+
            "CREATE TABLE rpa.invoices(\n" +
            "id INT PRIMARY KEY IDENTITY,\n" +
            "invoice_number INT,\n" +
            "invoice_date DATE NOT NULL,\n" +
            "customer_name VARCHAR(45),\n" +
            "amount DECIMAL(10,2) NOT NULL\n" +
            ");";

    @Inject
    MsSqlServerService dbService;
    @Output()
    private boolean isCreated = false;
    @Override
    public void execute() throws Exception {
        try{
            log.info("Driver version: " + dbService.getDriverVersion());
            dbService.executeStatement(query);
            this.isCreated = true;
        }
        catch(SQLSyntaxErrorException e){
            log.info("Can't execute query. Reason: "+ e.getMessage());
        }
        finally {
            dbService.closeConnection();
        }
    }
}

