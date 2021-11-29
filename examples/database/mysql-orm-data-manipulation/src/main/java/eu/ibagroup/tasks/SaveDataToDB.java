package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import eu.ibagroup.utils.DataProvider;
import eu.ibagroup.entity.MySqlInvoice;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Save records in MySQL table", description = "Save records in MySQL table")
public class SaveDataToDB extends ApTask {
    @Inject
    MySqlService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Get sample data");
        List<MySqlInvoice> invoicesToAdd = DataProvider.provideSampleRecords();

        log.info("Insert records into DB table");
        List<Integer> affectedRecords = dbService.withTransaction(MySqlInvoice.class, (ex) -> {
            List<Integer> res = new ArrayList();

            for (MySqlInvoice inv : invoicesToAdd) {
                res.add(ex.create(inv));
            }
            return res;
        });
    }
}
