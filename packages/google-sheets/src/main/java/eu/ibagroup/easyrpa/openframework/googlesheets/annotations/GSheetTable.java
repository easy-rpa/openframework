package eu.ibagroup.easyrpa.openframework.googlesheets.annotations;

import eu.ibagroup.easyrpa.openframework.googlesheets.function.TableFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetTable {

    GSheetCellStyle[] headerStyle() default {};

    GSheetCellStyle[] cellStyle() default {};

    Class<? extends TableFormatter> formatter() default TableFormatter.class;
}