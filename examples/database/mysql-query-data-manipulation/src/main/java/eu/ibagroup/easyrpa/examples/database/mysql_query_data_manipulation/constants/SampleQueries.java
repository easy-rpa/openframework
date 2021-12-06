package eu.ibagroup.easyrpa.examples.database.mysql_query_data_manipulation.constants;

public interface SampleQueries {
    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS rpa.invoices (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `invoice_number` INT NOT NULL,\n" +
            "  `invoice_date` DATE NULL,\n" +
            "  `customer_name` VARCHAR(45) NULL,\n" +
            "  `amount` DOUBLE NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);";
    String INSERT_QUERY_1 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000001', '2019-7-04', 'At&T', '5000.76');";
    String INSERT_QUERY_2 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000002', '2012-02-15', 'Apple', '12320.99');";
    String INSERT_QUERY_3 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000003', '2014-11-23', 'IBM', '600.00');";
    String INSERT_QUERY_4 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000004', '2011-1-04', 'Verizon', '138.50');";
    String INSERT_QUERY_5 = "INSERT INTO `rpa`.`invoices` (invoice_number, invoice_date, customer_name, amount)\n" +
            "VALUES ('000005', '2021-9-01', 'HP', '25600.00');";
    String DELETE_OBSOLETE_RECORDS_QUERY = "DELETE FROM rpa.invoices WHERE invoice_date < ?";
    String SELECT_ALL_QUERY = "SELECT * FROM rpa.invoices";
    String DROP_TABLE_QUERY = "DROP TABLE rpa.invoices";

}
