package eu.ibagroup.easyrpa.examples.database.sql_server_orm_data_manipulation.tasks;

import com.j256.ormlite.dao.Dao;
import eu.ibagroup.constants.Constants;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Input;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
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
    private List<MsSqlInvoice> outdatedInvoices;

    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Renew obsolete invoices' invoice_date to current date");
        SimpleDateFormat f = new SimpleDateFormat(Constants.DB_DATE_PATTERN);
        Calendar c = Calendar.getInstance();
        for(MsSqlInvoice invoice : outdatedInvoices){
            log.info("Invoice # {}: update invoice date {} -> {}", invoice.getInvoiceNumber(),
                    f.format(invoice.getInvoiceDate()), f.format(c.getTime()));
            invoice.setInvoiceDate(c.getTime());
        }
        Integer linesChanged = dbService.withTransaction(MsSqlInvoice.class, (ex) ->{
            List<Dao.CreateOrUpdateStatus> ret = new ArrayList<>();
            int recordsChanged = 0;
            for(MsSqlInvoice invoice : outdatedInvoices){
                recordsChanged += ex.createOrUpdate(invoice).getNumLinesChanged();
            }
            return recordsChanged;
        });
        log.info("{} lines changed",linesChanged);
    }
}
