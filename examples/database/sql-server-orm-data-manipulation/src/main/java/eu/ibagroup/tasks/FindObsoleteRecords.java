package eu.ibagroup.tasks;

import com.j256.ormlite.stmt.QueryBuilder;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.annotation.Configuration;
import eu.ibagroup.easyrpa.engine.annotation.Output;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Find obsolete records", description = "Find obsolete records in MS-SQL table and return them as task output")
public class FindObsoleteRecords extends ApTask {
    @Configuration(value = "ap.data.validity_years")
    private String yearsOfInterest;

    @Output()
    private List<MsSqlInvoice> outdatedInvoices;

    @Inject
    SQLServerService dbService;

    @Override
    public void execute() throws Exception {
        log.info("Select obsolete invoices");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, Integer.valueOf(yearsOfInterest) * -1);

        outdatedInvoices = dbService.withConnection(MsSqlInvoice.class, (ex) -> {
            QueryBuilder<MsSqlInvoice, Integer> queryBuilder = ex.getQueryBuilder(MsSqlInvoice.class);
            queryBuilder.where().le("invoice_date", c.getTime());
            return ex.query(queryBuilder, MsSqlInvoice.class);
        });
    }
}
