package eu.ibagroup.easyrpa.openframework.googlesheets.annotations;

import eu.ibagroup.easyrpa.openframework.googlesheets.function.ColumnFormatter;
import eu.ibagroup.easyrpa.openframework.googlesheets.function.FieldMapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GSheetColumn {

    String[] name() default {};

    int width() default -1;

    int order() default -1;

    GSheetCellStyle[] headerStyle() default {};

    GSheetCellStyle[] cellStyle() default {};

    Class<? extends ColumnFormatter> formatter() default ColumnFormatter.class;

    Class<? extends FieldMapper> mapper() default FieldMapper.class;
}