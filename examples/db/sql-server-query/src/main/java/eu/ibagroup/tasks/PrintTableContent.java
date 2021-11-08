package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.db.service.MsSqlServerService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@ApTaskEntry(name = "Print MySQL table content sample task", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    MsSqlServerService dbService;
    @Output()
    private String out = "";
    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws SQLException, ClassNotFoundException {

        ResultSet rs = dbService.executePreparedStatement(query);
        while ( rs.next() ) {
            int id = rs.getInt("invoice_number");
            Date invoiceDate = rs.getDate("invoice_date");
            String  customerName = rs.getString("customer_name");
            double amount = rs.getDouble("amount");

            System.out.printf( "invoice_number = %s , invoiceDate = %s, customerName = %s, amount = %s ", id, invoiceDate, customerName, amount );
            System.out.println();
        }
        dbService.closeConnection();
    }

}
