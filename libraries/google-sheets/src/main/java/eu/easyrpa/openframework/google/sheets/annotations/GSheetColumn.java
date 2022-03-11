package eu.easyrpa.openframework.google.sheets.annotations;

import eu.easyrpa.openframework.google.sheets.Table;
import eu.easyrpa.openframework.google.sheets.function.ColumnFormatter;
import eu.easyrpa.openframework.google.sheets.function.FieldMapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines relation between field of record class and corresponding column or data of table on the sheet.
 *
 * @see Table
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetColumn {

    /**
     * Specifies name or hierarchy of names that identifies specific column of related table on the sheet.
     * <p>
     * If table has <b>one-line header</b> then value should be simple name of column. E.g. "Person Name".
     * <p>
     * If table has <b>multi-line headers</b> and necessary column is grouped with other columns the value should
     * be array of names. This array includes names of all grouped columns in order from the top. E.g. lets take the
     * following table:
     * <table cellSpacing="0" cellPadding="10">
     *      <tr><th rowspan=2>Id</th><th colspan=2 align="center">Person</th></tr>
     *      <tr><th>Name</th><th>Age</th></tr>
     *      <tr><td>1</td><td>John</td><td>35</td></tr>
     *      <tr><td>2</td><td>Mike</td><td>50</td></tr>
     * </table>
     * For column "Name" the value should be ["Person", "Name"].
     * <p>
     * For column "Age" the value should be ["Person", "Age"].
     * <p>
     * But for column "Id" the value can be simple "Id".
     * <p>
     * <br>
     * If field of record class doesn't have any direct relation with table columns the value of name can be left
     * unspecified. In such case the value of such field should be populated via {@link #mapper()}.
     */
    String[] name() default {};

    /**
     * Specifies the width of related column in pixels. Used in case of creation of new table based on class of records.
     */
    int width() default -1;

    /**
     * Specifies the order index of related column. Used during creation of new table or insertion of new
     * records into existing table.
     * <p>
     * <br>
     * Default value: order index of the field definition in class of record
     */
    int order() default -1;

    /**
     * Specifies style that should be applied to a header cell of related column. Used in case of creation of new table
     * based on class of records.
     *
     * @see GSheetCellStyle
     */
    GSheetCellStyle[] headerStyle() default {};

    /**
     * Specifies style that should be applied to a row cell of related column. Used during creation of new table
     * or insertion of new records into existing table.
     *
     * @see GSheetCellStyle
     */
    GSheetCellStyle[] cellStyle() default {};

    /**
     * Specifies class of formatter that implements {@link ColumnFormatter} and performs dynamic styling or
     * value modification for related column cells.
     */
    Class<? extends ColumnFormatter> formatter() default ColumnFormatter.class;

    /**
     * Specifies class of mapper that implements {@link FieldMapper} and extracts value for this field from table
     * row data.
     */
    Class<? extends FieldMapper> mapper() default FieldMapper.class;
}