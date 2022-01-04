package eu.ibagroup.easyrpa.examples.database.tables_manipulating.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

@Data
@DatabaseTable(tableName = "products")
public class Product {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;

    @DatabaseField(columnName = "product_name", canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private int count;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false,
            columnDefinition = "INTEGER REFERENCES invoices(id) ON DELETE CASCADE")
    private Invoice invoice;

    public Product() {
    }

    public Product(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
