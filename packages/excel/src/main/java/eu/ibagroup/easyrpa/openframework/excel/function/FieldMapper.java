package eu.ibagroup.easyrpa.openframework.excel.function;

import java.util.List;

@FunctionalInterface
public interface FieldMapper {

    Object map(String fieldName, List<Object> values, int valueIndex);
}
