package eu.ibagroup.easyrpa.examples.database.sql_server_orm_data_manipulation.tasks;

import eu.ibagroup.easyrpa.engine.annotation.AfterInit;
import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.SQLServerService;
import eu.ibagroup.entity.MsSqlInvoice;
import eu.ibagroup.utils.DataProvider;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApTaskEntry(name = "Save records in MS-SQL table", description = "Save records in MS-SQL table")
public class SaveDataToDB extends ApTask {
    @Inject
    SQLServerService dbService;

    @AfterInit
    public void init() {
    }

    @Override
    public void execute() throws Exception {
        log.info("Get sample data");
        List<MsSqlInvoice> invoicesToAdd = DataProvider.provideSampleRecords();

        log.info("Insert records into DB table");
        List<Integer> affectedRecords = dbService.withTransaction(MsSqlInvoice.class, (ex) -> {
            List<Integer> res = new ArrayList();

            for (MsSqlInvoice inv : invoicesToAdd) {
                res.add(ex.create(inv));
            }
            return res;
        });
    }
}
