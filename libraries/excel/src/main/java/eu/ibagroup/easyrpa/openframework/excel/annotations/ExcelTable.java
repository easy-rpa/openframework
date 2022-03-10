package eu.ibagroup.easyrpa.openframework.excel.annotations;

import eu.ibagroup.easyrpa.openframework.excel.function.TableFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines common formatting and styling for all cells of table on the sheet.
 *
 * @see eu.ibagroup.easyrpa.openframework.excel.Table
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {

    /**
     * Specifies style that should be applied to all header cells of the table. Used during creation of new table.
     *
     * @see ExcelCellStyle
     */
    ExcelCellStyle[] headerStyle() default {};

    /**
     * Specifies style that should be applied to all row cell of the table. Used during creation of new table
     * or insertion of new records into existing table.
     *
     * @see ExcelCellStyle
     */
    ExcelCellStyle[] cellStyle() default {};

    /**
     * Specifies class of formatter that implements {@link TableFormatter} and performs dynamic styling or
     * value modification for all cells of the table.
     */
    Class<? extends TableFormatter> formatter() default TableFormatter.class;
}