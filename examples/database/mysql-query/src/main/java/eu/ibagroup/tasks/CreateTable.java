package eu.ibagroup.tasks;

import eu.ibagroup.easyrpa.engine.annotation.ApTaskEntry;
import eu.ibagroup.easyrpa.engine.apflow.ApTask;
import eu.ibagroup.easyrpa.openframework.database.service.MySqlService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
@ApTaskEntry(name = "Create MySQL table", description = "Creates rpa.invoices MySQL table")
public class CreateTable extends ApTask {
    String query = "CREATE TABLE IF NOT EXISTS rpa.invoices (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `invoice_number` INT NOT NULL,\n" +
            "  `invoice_date` DATE NULL,\n" +
            "  `customer_name` VARCHAR(45) NULL,\n" +
            "  `amount` DOUBLE NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);";
    @Inject
    MySqlService dbService;

    @Override
    public void execute() throws Exception {
        dbService.withConnection((ex) -> ex.executeUpdate(query));
    }
}
