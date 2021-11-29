package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.PostgresService;
import eu.ibagroup.entity.PostgresInvoice;
import eu.ibagroup.utils.DataProvider;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
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
        List<Integer> affectedRecords = dbService.withTransaction(PostgresInvoice.class, (ex) -> {
            List<Integer> res = new ArrayList();

            for (PostgresInvoice inv : invoicesToAdd) {
                res.add(ex.create(inv));
            }
            return res;
        });
    }
}
