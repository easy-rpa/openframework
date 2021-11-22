package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.easyrpa.openframework.database.common.DatabaseSession;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.MessageFormat;

@Slf4j
@ApTaskEntry(name = "Print MS-SQL table content sample task", description = "Print 'rpa.invoices' table content")
public class PrintTableContentTask extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    SQLServerService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        dbService.withConnection(() ->{
            String outStr = "";
            ResultSet rs = dbService.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("invoice_number");
                Date invoiceDate = rs.getDate("invoice_date");
                String customerName = rs.getString("customer_name");
                double amount = rs.getDouble("amount");
                outStr = MessageFormat.format("invoice_number = {0}, invoiceDate = {1}, customerName = {2}, amount = {3} ", id, invoiceDate, customerName, amount);
                log.info(outStr);
            }
            return outStr;
        });
    }
}