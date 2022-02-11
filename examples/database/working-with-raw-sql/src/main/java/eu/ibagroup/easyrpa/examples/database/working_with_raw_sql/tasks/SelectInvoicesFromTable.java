package eu.ibagroup.easyrpa.examples.database.working_with_raw_sql.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.ResultSet;

@Slf4j
@ApTaskEntry(name = "Select Invoices from Table")
public class SelectInvoicesFromTable extends ApTask {

    private final String SELECT_INVOICES_SQL = "SELECT * FROM invoices WHERE invoice_date < '%s';";

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        String oldDate = DateTime.now().minusYears(3).toString("yyyy-MM-dd");

        log.info("Output invoices which are older than '{}'.", oldDate);
        dbService.withConnection(invoicesDbParams, (c) -> {
            ResultSet rs = c.executeQuery(String.format(SELECT_INVOICES_SQL, oldDate));
            while (rs.next()) {
                int id = rs.getInt("invoice_number");
                Date invoiceDate = rs.getDate("invoice_date");
                String customerName = rs.getString("customer_name");
                double amount = rs.getDouble("amount");
                log.info("invoice_number = {}, invoice_date = {}, customer_name = {}, amount = {}", id, invoiceDate, customerName, amount);
            }
        });
    }
}
