package eu.ibagroup.easyrpa.openframework.excel.annotations;

import eu.ibagroup.easyrpa.openframework.excel.function.TableFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable {

    ExcelCellStyle[] headerStyle() default {};

    ExcelCellStyle[] cellStyle() default {};

    Class<? extends TableFormatter> formatter() default TableFormatter.class;
}