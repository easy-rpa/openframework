package eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.tasks;

import com.j256.ormlite.dao.Dao;
import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.constants.Constants;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.entity.PostgresInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Set current date as invoiceDate in the outdated invoices")
public class RenewObsoleteRecords extends ApTask {
    @Input
    private List<PostgresInvoice> outdatedInvoices;

    @Inject
    PostgresService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Renew obsolete invoices' invoice_date to current date");
        SimpleDateFormat f = new SimpleDateFormat(Constants.DB_DATE_PATTERN);
        Calendar c = Calendar.getInstance();
        for(PostgresInvoice invoice : outdatedInvoices){
            log.info("Invoice # {}: update invoice date {} -> {}", invoice.getInvoiceNumber(),
                    f.format(invoice.getInvoiceDate()), f.format(c.getTime()));
            invoice.setInvoiceDate(c.getTime());
        }
        dbService.withTransaction(PostgresInvoice.class, (ex) ->{
            List<Dao.CreateOrUpdateStatus> ret = new ArrayList<>();
            int linesChanged = 0;
            for(PostgresInvoice invoice : outdatedInvoices){
                linesChanged += ex.createOrUpdate(invoice).getNumLinesChanged();
            }
            return linesChanged;
        });

    }
}
