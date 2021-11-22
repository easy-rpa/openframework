package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.MessageFormat;

@Slf4j
@ApTaskEntry(name = "Print MySQL table content sample task", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    MySqlService dbService;
    @Output()
    private String out = "";

    @Override
    public void execute() throws Exception {

        dbService.withConnection(() -> {
            ResultSet rs = dbService.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("invoice_number");
                Date invoiceDate = rs.getDate("invoice_date");
                String customerName = rs.getString("customer_name");
                double amount = rs.getDouble("amount");
                log.info(MessageFormat.format("invoice_number = {0}, invoiceDate = {1}, customerName = {2}, amount = {3} ", id, invoiceDate, customerName, amount));
            }
            return null;
        });

    }
}
