package eu.ibagroup.tasks;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
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

        ConnectionSource connectionSource = dbService.initOrmConnection().getOrmConnectionSource();
        Dao<MsSqlInvoice, Integer> invoicesDao = DaoManager.createDao(connectionSource, MsSqlInvoice.class);

        QueryBuilder<MsSqlInvoice, Integer> queryBuilder = invoicesDao.queryBuilder();
        queryBuilder.where().eq("customer_name", "IBM");
        PreparedQuery<MsSqlInvoice> preparedQuery = queryBuilder.prepare();

        List<MsSqlInvoice> accountList = invoicesDao.query(preparedQuery);

    }
}
