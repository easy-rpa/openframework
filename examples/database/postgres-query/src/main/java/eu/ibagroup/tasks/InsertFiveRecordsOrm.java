package eu.ibagroup.tasks;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.entity.PostgresInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;

import static eu.ibagroup.ap.Constants.DB_DATE_PATTERN;

@Slf4j
@ApTaskEntry(name = "Print POSTGRES table content sample task", description = "Print 'rpa.invoices' table content")
public class InsertFiveRecordsOrm extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    PostgresService dbService;
    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws SQLException, ParseException {

        ConnectionSource connectionSource = dbService.initOrmConnection().getOrmConnectionSource();

        Dao<PostgresInvoice, Integer> invoicesDao = DaoManager.createDao(connectionSource, PostgresInvoice.class);

        List<PostgresInvoice> invoices = new ArrayList<>();
        invoices.add(new PostgresInvoice(10001, new SimpleDateFormat(DB_DATE_PATTERN).parse("2016-01-22"), "IBA", 4500));
        invoices.add(new PostgresInvoice(10002, new SimpleDateFormat(DB_DATE_PATTERN).parse("2014-04-03"), "ITPARK", 5400.87));
        invoices.add(new PostgresInvoice(10003, new SimpleDateFormat(DB_DATE_PATTERN).parse("2020-12-08"), "SOFTCLUB", 1200.55));
        invoices.add(new PostgresInvoice(10004, new SimpleDateFormat(DB_DATE_PATTERN).parse("2011-05-14"), "EPAM", 1000));
        invoices.add(new PostgresInvoice(10005, new SimpleDateFormat(DB_DATE_PATTERN).parse("2018-02-06"), "WF", 2400.99));
        for (PostgresInvoice invoice : invoices) {
            invoicesDao.create(invoice);
        }
    }
}
