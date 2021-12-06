package eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.entity.PostgresInvoice;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.utils.DataProvider;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Save records in Postgres table", description = "Save records in Postgres table")
public class SaveDataToDB extends ApTask {
    @Inject
    PostgresService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Get sample data");
        List<PostgresInvoice> invoicesToAdd = DataProvider.provideSampleRecords();

        log.info("Insert records into DB table");
        Integer linesChanged = dbService.withTransaction(PostgresInvoice.class, (ex) -> {
            int recordsChanged = 0;
            for (PostgresInvoice invoice : invoicesToAdd) {
                recordsChanged += ex.createOrUpdate(invoice).getNumLinesChanged();
            }
            return recordsChanged;
        });
        log.info("{} lines changed",linesChanged);
    }
}
