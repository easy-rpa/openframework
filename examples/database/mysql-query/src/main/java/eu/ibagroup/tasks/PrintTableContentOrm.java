package eu.ibagroup.tasks;

import com.j256.ormlite.stmt.QueryBuilder;
import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import eu.ibagroup.entity.MySqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static eu.ibagroup.constants.Constants.DB_DATE_PATTERN;


@Slf4j
@ApTaskEntry(name = "Print table records with the invoice_date newer than 2016-01-01", description = "Print 'rpa.invoices' table content")
public class PrintTableContentOrm extends ApTask {
    String query = "SELECT * FROM rpa.invoices";
    @Inject
    MySqlService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {

        List<MySqlInvoice> allInvoices = dbService.withConnection(MySqlInvoice.class, () -> {
            return dbService.selectAll(MySqlInvoice.class);
        });

        dbService.withConnection(MySqlInvoice.class, () -> {
            QueryBuilder<MySqlInvoice, Integer> queryBuilder = dbService.getQueryBuilder(MySqlInvoice.class);
            queryBuilder.where().ge("invoice_date", new SimpleDateFormat(DB_DATE_PATTERN).parse("2016-01-01"));

            List<MySqlInvoice> accountList = dbService.query(queryBuilder, MySqlInvoice.class);
            accountList.forEach(a -> {
                log.info("id: {}; invoice#: {}; invoice_date: {}; customer_name: {}; amount: {}",
                        a.getId(), a.getInvoiceNumber(), a.getInvoiceDate(), a.getCustomerName(), a.getAmount());
            });
            return null;
        });
    }
}