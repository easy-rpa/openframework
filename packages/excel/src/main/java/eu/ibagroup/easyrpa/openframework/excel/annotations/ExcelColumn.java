package eu.ibagroup.easyrpa.openframework.excel.annotations;

import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;
import eu.ibagroup.easyrpa.openframework.excel.function.FieldMapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    String[] name() default {};

    int width() default -1;

    int order() default -1;

    ExcelCellStyle[] headerStyle() default {};

    ExcelCellStyle[] cellStyle() default {};

    Class<? extends ColumnFormatter> formatter() default ColumnFormatter.class;

    Class<? extends FieldMapper> mapper() default FieldMapper.class;
}