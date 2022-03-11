package eu.easyrpa.examples.database.working_with_raw_sql.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.easyrpa.openframework.database.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.inject.Inject;

@ApTaskEntry(name = "Update Outdated Invoices")
@Slf4j
public class UpdateOutdatedInvoices extends ApTask {

    private final String UPDATE_INVOICES_SQL = "UPDATE invoices SET outdated='TRUE' WHERE invoice_date < '%s';";

    @Configuration(value = "invoices.db.params")
    private String invoicesDbParams;

    @Inject
    private DatabaseService dbService;

    @Override
    public void execute() {
        String oldDate = DateTime.now().minusYears(3).toString("yyyy-MM-dd");

        log.info("Mark outdated invoices using SQL.");
        int res = dbService.withConnection(invoicesDbParams, (c) -> {
            return c.executeUpdate(String.format(UPDATE_INVOICES_SQL, oldDate));
        });

        log.info("'{}' records have been updated.", res);
    }
}
