package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create MS-SQL SERVER table", description = "Creates rpa.invoices SQL SERVER table")
public class CreateTableTask extends ApTask {
    String query = "if not exists (select * from sysobjects where name='invoices' and xtype='U') \n" +
            "CREATE TABLE rpa.invoices(\n" +
            "id INT PRIMARY KEY IDENTITY,\n" +
            "invoice_number INT,\n" +
            "invoice_date DATE NOT NULL,\n" +
            "customer_name VARCHAR(45),\n" +
            "amount DECIMAL(10,2) NOT NULL\n" +
            ");";
    @Inject
    SQLServerService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        dbService.withConnection(() -> dbService.executeUpdate(query));
    }
}
