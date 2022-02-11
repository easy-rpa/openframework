package eu.ibagroup.easyrpa.examples.google.sheets.spreadsheet_editing.entities.mappers;

import eu.ibagroup.easyrpa.openframework.google.sheets.function.FieldMapper;

import java.util.List;

public class SurvivedFieldMapper implements FieldMapper {

    @Override
    public Object map(String fieldName, List<Object> values, int valueIndex) {
        return "Yes".equals(values.get(valueIndex));
    }
}
