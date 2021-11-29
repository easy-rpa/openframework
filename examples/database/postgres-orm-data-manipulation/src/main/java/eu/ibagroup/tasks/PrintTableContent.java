package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.entity.PostgresInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Print Postgres table content", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    @Inject
    PostgresService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Print table content:");
        List<PostgresInvoice> allInvoices = dbService.withConnection(PostgresInvoice.class, (ex) -> {
            return ex.selectAll(PostgresInvoice.class);
        });
        allInvoices.forEach(a -> {
            log.info("id: {}; invoice#: {}; invoice_date: {}; customer_name: {}; amount: {}",
                    a.getId(), a.getInvoiceNumber(), a.getInvoiceDate(), a.getCustomerName(), a.getAmount());
        });
    }
}