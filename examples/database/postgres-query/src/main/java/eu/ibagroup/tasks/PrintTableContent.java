package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.PostgreSqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.text.MessageFormat;

@Slf4j
@ApTaskEntry(name = "Print MySQL table content sample task", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    PostgreSqlService dbService;
    @Output()
    private String out = "";
    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws SQLException, ClassNotFoundException {
        try {
            ResultSet rs = dbService.executePreparedStatement(query);
            while (rs.next()) {
                int id = rs.getInt("invoice_number");
                Date invoiceDate = rs.getDate("invoice_date");
                String customerName = rs.getString("customer_name");
                double amount = rs.getDouble("amount");

                log.info(MessageFormat.format("invoice_number = {0}, invoiceDate = {1}, customerName = {2}, amount = {3} ", id, invoiceDate, customerName, amount));
            }
        }
        catch(SQLSyntaxErrorException e){
            log.info("Can't execute query. Reason: "+ e.getMessage());
        }
        finally {
            dbService.closeConnection();
        }
    }
}
