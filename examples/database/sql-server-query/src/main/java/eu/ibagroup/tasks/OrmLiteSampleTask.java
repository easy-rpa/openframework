package eu.ibagroup.tasks;

import com.j256.ormlite.stmt.QueryBuilder;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "ORM Lite sample", description = "ORM Lite implemetation sample")
public class OrmLiteSampleTask extends ApTask {
    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) ->{
            QueryBuilder<MsSqlInvoice, Integer> queryBuilder = ex.getQueryBuilder(MsSqlInvoice.class);
            queryBuilder.where().eq("customer_name", "IBM");

            List<MsSqlInvoice> accountList = ex.query(queryBuilder, MsSqlInvoice.class);
            accountList.forEach(a -> {
                log.info("id: {}; invoice#: {}; invoice_date: {}; customer_name: {}; amount: {}",
                        a.getId(), a.getInvoiceNumber(), a.getInvoiceDate(), a.getCustomerName(), a.getAmount());
            });
            return accountList;
        });
    }
}
