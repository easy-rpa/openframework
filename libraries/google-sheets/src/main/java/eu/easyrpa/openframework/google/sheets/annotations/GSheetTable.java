package eu.easyrpa.openframework.google.sheets.annotations;

import eu.easyrpa.openframework.google.sheets.Table;
import eu.easyrpa.openframework.google.sheets.function.TableFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines common formatting and styling for all cells of table on the sheet.
 *
 * @see Table
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetTable {

    /**
     * Specifies style that should be applied to all header cells of the table. Used during creation of new table.
     *
     * @see GSheetCellStyle
     */
    GSheetCellStyle[] headerStyle() default {};

    /**
     * Specifies style that should be applied to all row cell of the table. Used during creation of new table
     * or insertion of new records into existing table.
     *
     * @see GSheetCellStyle
     */
    GSheetCellStyle[] cellStyle() default {};

    /**
     * Specifies class of formatter that implements {@link TableFormatter} and performs dynamic styling or
     * value modification for all cells of the table.
     */
    Class<? extends TableFormatter> formatter() default TableFormatter.class;
}