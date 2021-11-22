package eu.ibagroup.easyrpa.examples.excel.sheet_data_reading.entities.mappers;

import eu.ibagroup.easyrpa.openframework.excel.function.FieldMapper;

import java.util.List;
import java.util.Map;

public class SurvivedFieldMapper implements FieldMapper {

    @Override
    public Object map(List<Object> values, String columnName, Map<String, Integer> columnsIndexMap) {
        int columnIndex = columnsIndexMap.get(columnName);
        if (columnIndex >= 0) {
            return "Yes".equals(values.get(columnIndex));
        }
        return false;
    }
}
