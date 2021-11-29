package eu.ibagroup.easyrpa.examples.database.sql_server_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Print MS-SQL table content", description = "Print 'rpa.invoices' table content")
public class PrintTableContent extends ApTask {
    @Inject
    SQLServerService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Print table content:");
        List<MsSqlInvoice> allInvoices = dbService.withConnection(MsSqlInvoice.class, (ex) ->
                ex.selectAll(MsSqlInvoice.class)
        );
        allInvoices.forEach(a -> {
            log.info("id: {}; invoice#: {}; invoice_date: {}; customer_name: {}; amount: {}",
                    a.getId(), a.getInvoiceNumber(), a.getInvoiceDate(), a.getCustomerName(), a.getAmount());
        });
    }
}