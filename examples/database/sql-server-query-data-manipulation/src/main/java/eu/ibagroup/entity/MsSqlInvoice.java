package eu.ibagroup.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "\"msdb\".\"rpa\".\"invoices\"")
public class MsSqlInvoice {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(columnName = "invoice_number", canBeNull = false)
    private int invoiceNumber;

    @DatabaseField(columnName = "invoice_date", dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd")
    public Date invoiceDate;

    @DatabaseField(columnName = "customer_name", canBeNull = false)
    private String customerName;

    @DatabaseField(canBeNull = false)
    private double amount;

    public MsSqlInvoice() {
    }

    public MsSqlInvoice(int id, int invoiceNumber, Date invoiceDate, String customerName, double amount) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.amount = amount;
    }

    public MsSqlInvoice(int invoiceNumber, Date invoiceDate, String customerName, double amount) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
