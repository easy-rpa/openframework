package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import eu.ibagroup.entity.MySqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static eu.ibagroup.ap.Constants.DB_DATE_PATTERN;

@Slf4j
@ApTaskEntry(name = "Print POSTGRES table content sample task", description = "Print 'rpa.invoices' table content")
public class InsertFiveRecordsOrm extends ApTask {
    @Inject
    MySqlService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        List<MySqlInvoice> invoicesToAdd = new ArrayList<>();

        invoicesToAdd.add(new MySqlInvoice(10001, new SimpleDateFormat(DB_DATE_PATTERN).parse("2016-01-22"), "IBA", 4500));
        invoicesToAdd.add(new MySqlInvoice(10002, new SimpleDateFormat(DB_DATE_PATTERN).parse("2014-04-03"), "ITPARK", 5400.87));
        invoicesToAdd.add(new MySqlInvoice(10003, new SimpleDateFormat(DB_DATE_PATTERN).parse("2020-12-08"), "SOFTCLUB", 1200.55));
        invoicesToAdd.add(new MySqlInvoice(10004, new SimpleDateFormat(DB_DATE_PATTERN).parse("2011-05-14"), "EPAM", 1000));
        invoicesToAdd.add(new MySqlInvoice(10005, new SimpleDateFormat(DB_DATE_PATTERN).parse("2018-02-06"), "WF", 2400.99));

        List<Integer> affectedRecords = dbService.withTransaction(MySqlInvoice.class, () -> {
            List<Integer> res = new ArrayList();

            for (MySqlInvoice inv : invoicesToAdd) {
                res.add(dbService.create(inv));
            }
            return res;
        });
    }
}
