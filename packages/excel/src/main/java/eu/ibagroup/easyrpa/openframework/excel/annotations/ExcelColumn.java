package eu.ibagroup.easyrpa.openframework.excel.annotations;

import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    String[] name();

    boolean identifier() default false;

    ExcelCellStyle[] headerStyle() default {};

    ExcelCellStyle[] cellStyle() default {};

    Class<? extends ColumnFormatter> formatter() default ColumnFormatter.class;

    int order() default -1;
}