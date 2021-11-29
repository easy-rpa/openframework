package eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.utils;

import eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.entity.PostgresInvoice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static eu.ibagroup.easyrpa.examples.database.postgres_orm_data_manipulation.constants.Constants.DB_DATE_PATTERN;

public class DataProvider {
    public static List<PostgresInvoice> provideSampleRecords() throws ParseException {
        List<PostgresInvoice> invoicesToAdd = new ArrayList<>();

        invoicesToAdd.add(new PostgresInvoice(10001, new SimpleDateFormat(DB_DATE_PATTERN).parse("2021-01-22"), "Sony", 4500));
        invoicesToAdd.add(new PostgresInvoice(10002, new SimpleDateFormat(DB_DATE_PATTERN).parse("2014-04-03"), "Lenovo", 5400.87));
        invoicesToAdd.add(new PostgresInvoice(10003, new SimpleDateFormat(DB_DATE_PATTERN).parse("2020-12-08"), "DELL", 1200.55));
        invoicesToAdd.add(new PostgresInvoice(10004, new SimpleDateFormat(DB_DATE_PATTERN).parse("2011-05-14"), "NEC", 1000));
        invoicesToAdd.add(new PostgresInvoice(10005, new SimpleDateFormat(DB_DATE_PATTERN).parse("2018-02-06"), "LG", 2400.99));
        return invoicesToAdd;
    }
}
