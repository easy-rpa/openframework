package eu.ibagroup.easyrpa.examples.excel.excel_file_editing.entities.mappers;

import eu.ibagroup.easyrpa.openframework.excel.function.FieldMapper;

import java.util.List;

public class SurvivedFieldMapper implements FieldMapper {

    @Override
    public Object map(String fieldName, List<Object> values, int valueIndex) {
        return "Yes".equals(values.get(valueIndex));
    }
}
