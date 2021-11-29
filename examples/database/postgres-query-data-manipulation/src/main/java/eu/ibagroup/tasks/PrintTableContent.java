package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.MessageFormat;

import static eu.ibagroup.constants.SampleQueries.SELECT_ALL_QUERY;

@Slf4j
@ApTaskEntry(name = "Print Postgres table content sample", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    @Inject
    PostgresService dbService;
    @Output()
    private String out = "";

    @Override
    public void execute() throws Exception {

        dbService.withConnection((ex) -> {
            ResultSet rs = ex.executeQuery(SELECT_ALL_QUERY);
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
