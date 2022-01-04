package eu.ibagroup.easyrpa.openframework.googlesheets.internal;

import eu.ibagroup.easyrpa.openframework.core.utils.TypeUtils;
import eu.ibagroup.easyrpa.openframework.googlesheets.Cell;
import eu.ibagroup.easyrpa.openframework.googlesheets.CellRef;
import eu.ibagroup.easyrpa.openframework.googlesheets.GSheetCellStyle;
import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetColumn;
import eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetTable;
import eu.ibagroup.easyrpa.openframework.googlesheets.function.ColumnFormatter;
import eu.ibagroup.easyrpa.openframework.googlesheets.function.FieldMapper;
import eu.ibagroup.easyrpa.openframework.googlesheets.function.TableFormatter;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.StyleAnnotationUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class RecordTypeHelper<T> {

    private Class<T> recordType = null;
    private GSheetCellStyle tableHeaderCellStyle = null;
    private GSheetCellStyle tableCellStyle = null;
    private TableFormatter<T> tableFormatter = null;

    private List<String> fields = new ArrayList<>();
    private Map<String, String> fieldToColumnMap = new HashMap<>();
    private Map<String, Integer> columnNameToOrderMap = new HashMap<>();
    private Map<Integer, String> columnOrderToFieldMap = new HashMap<>();

    private Map<Integer, Integer> columnWidthMap = new HashMap<>();
    private Map<Integer, GSheetCellStyle> columnHeaderCellStyleMap = new HashMap<>();
    private Map<Integer, GSheetCellStyle> columnCellStyleMap = new HashMap<>();
    private Map<Integer, ColumnFormatter<T>> columnFormatterMap = new HashMap<>();
    private Map<String, FieldMapper> fieldMapperMap = new HashMap<>();


    private RecordTypeHelper() {
    }

    public List<String> getColumnNames() {
        int columnsCount = columnNameToOrderMap.values().stream()
                .max(Comparator.comparingInt(v -> v)).orElse(-1) + 1;
        String[] columnNames = new String[columnsCount];
        for (int i = 0; i < columnsCount; i++) {
            String fieldName = columnOrderToFieldMap.get(i);
            if (fieldName != null) {
                String columnName = fieldToColumnMap.get(fieldName);
                columnNames[i] = columnName != null ? columnName : "";
            } else {
                columnNames[i] = "";
            }
        }
        return Arrays.asList(columnNames);
    }

    public T mapToRecord(List<Object> values, Map<String, Integer> columnNameToValueIndexMap) {
        try {
            if (values == null) {
                return null;
            }
            if (columnNameToValueIndexMap == null) {
                columnNameToValueIndexMap = columnNameToOrderMap;
            }
            T record = recordType.getDeclaredConstructor().newInstance();
            for (String fieldName : fields) {
                String columnName = fieldToColumnMap.get(fieldName);
                Integer valueIndex = columnName != null ? columnNameToValueIndexMap.get(columnName) : null;

                Object value = null;
                FieldMapper mapper = fieldMapperMap.get(fieldName);
                if (mapper != null) {
                    value = mapper.map(fieldName, values, valueIndex != null ? valueIndex : -1);

                } else if (valueIndex != null && valueIndex >= 0 && valueIndex < values.size()) {
                    value = values.get(valueIndex);
                }

                if (value != null) {
                    TypeUtils.setFieldValue(record, fieldName, value);
                }
            }
            return record;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> mapToValues(T record, Map<String, Integer> columnNameToValueIndexMap) {
        try {
            if (record == null) {
                return null;
            }
            if (columnNameToValueIndexMap == null) {
                columnNameToValueIndexMap = columnNameToOrderMap;
            }
            int valuesCount = columnNameToValueIndexMap.values().stream()
                    .max(Comparator.comparingInt(v -> v)).orElse(-1) + 1;
            List<Object> values = new ArrayList<>(Collections.nCopies(valuesCount, null));
            for (String columnName : columnNameToValueIndexMap.keySet()) {
                Integer valueIndex = columnNameToValueIndexMap.get(columnName);
                if (valueIndex == null || valueIndex < 0) {
                    continue;
                }
                Integer columnOrder = columnNameToOrderMap.get(columnName);
                if (columnOrder == null) {
                    continue;
                }
                String fieldName = columnOrderToFieldMap.get(columnOrder);
                if (fieldName != null) {
                    values.set(valueIndex, TypeUtils.getFieldValue(record, fieldName));
                }
            }
            return values;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void formatCell(Cell cell, String columnName, int recordIndex, List<T> records) throws IOException {

        if (cell != null && columnName != null && !columnName.trim().isEmpty()) {

            Integer columnOrder = columnNameToOrderMap.get(columnName);
            if (columnOrder == null) {
                return;
            }

            GSheetCellStyle cellStyle = columnCellStyleMap.get(columnOrder);
            if (cellStyle != null) {
                cell.setStyle(cellStyle);
            } else if (tableCellStyle != null) {
                cell.setStyle(tableCellStyle);
            }

            if (tableFormatter != null) {
//                tableFormatter.format(cell, columnName, recordIndex, records);
            }
            ColumnFormatter<T> formatter = columnFormatterMap.get(columnOrder);
            if (formatter != null) {
                T record = records != null && recordIndex >= 0 && recordIndex < records.size()
                        ? records.get(recordIndex)
                        : null;
//                formatter.format(cell, columnName, record);
            }
        }
    }

    public void formatHeaderCell(Cell cell, String columnName) throws IOException {
        if (cell != null && columnName != null && !columnName.trim().isEmpty()) {

            Integer columnOrder = columnNameToOrderMap.get(columnName);
            if (columnOrder == null) {
                return;
            }

            Integer width = columnWidthMap.get(columnOrder);
            if (width != null) {
//                cell.getSheet().setColumnWidth(cell.getColumnIndex(), width);
            }

            GSheetCellStyle headerCellStyle = columnHeaderCellStyleMap.get(columnOrder);
            if (headerCellStyle != null) {
                cell.setStyle(headerCellStyle);
            } else if (tableHeaderCellStyle != null) {
                cell.setStyle(tableHeaderCellStyle);
            }

//            if (tableFormatter != null) {
//                tableFormatter.format(cell, columnName, -1, null);
//            }
//            ColumnFormatter<T> formatter = columnFormatterMap.get(columnOrder);
//            if (formatter != null) {
//                formatter.format(cell, columnName, null);
//            }
        }
    }

    private static final Map<String, RecordTypeHelper<?>> HELPERS_CACHE = new HashMap<>();

    @SuppressWarnings({"unchecked"})
    public static <T> RecordTypeHelper<T> getFor(T record) {
        return getFor((Class<T>) record.getClass());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> RecordTypeHelper<T> getFor(Class<T> recordType) {
        RecordTypeHelper<T> typeInfo = (RecordTypeHelper<T>) HELPERS_CACHE.get(recordType.getName());
        if (typeInfo != null) {
            return typeInfo;
        }
        try {
            typeInfo = new RecordTypeHelper<>();
            typeInfo.recordType = recordType;

            GSheetTable tableAnnotation = recordType.getAnnotation(GSheetTable.class);
            if (tableAnnotation != null) {
                eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetCellStyle[] styleAnnotations = tableAnnotation.cellStyle();
                if (styleAnnotations.length > 0) {
                    typeInfo.tableCellStyle = getStyleForAnnotation(styleAnnotations[0]);
                }
                styleAnnotations = tableAnnotation.headerStyle();
                if (styleAnnotations.length > 0) {
                    typeInfo.tableHeaderCellStyle = getStyleForAnnotation(styleAnnotations[0]);
                }
                Class<? extends TableFormatter> formatterClass = tableAnnotation.formatter();
                if (formatterClass != TableFormatter.class) {
                    typeInfo.tableFormatter = formatterClass.getDeclaredConstructor().newInstance();
                }
            }

            int index = 0;
            for (Field field : recordType.getDeclaredFields()) {
                GSheetColumn columnAnnotation = field.getAnnotation(GSheetColumn.class);
                if (columnAnnotation != null) {
                    int order = columnAnnotation.order();
                    String[] nameHierarchy = columnAnnotation.name();

                    String fieldName = field.getName();
                    int columnOrder = order > 0 ? order : index++;
                    String columnName = nameHierarchy.length > 0 ? nameHierarchy[0] : null;

                    typeInfo.fields.add(fieldName);

                    if (columnName != null) {
                        typeInfo.fieldToColumnMap.put(fieldName, columnName);
                        typeInfo.columnNameToOrderMap.put(columnName, columnOrder);
                        typeInfo.columnOrderToFieldMap.put(columnOrder, fieldName);
                    }

                    int width = columnAnnotation.width();
                    if (width >= 0) {
                        typeInfo.columnWidthMap.put(columnOrder, width);
                    }

                    eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetCellStyle[] styleAnnotations = columnAnnotation.cellStyle();
                    if (styleAnnotations.length > 0) {
                        typeInfo.columnCellStyleMap.put(columnOrder, getStyleForAnnotation(styleAnnotations[0]));
                    }

                    styleAnnotations = columnAnnotation.headerStyle();
                    if (styleAnnotations.length > 0) {
                        typeInfo.columnHeaderCellStyleMap.put(columnOrder, getStyleForAnnotation(styleAnnotations[0]));
                    }

                    Class<? extends ColumnFormatter> formatterClass = columnAnnotation.formatter();
                    if (formatterClass != ColumnFormatter.class) {
                        typeInfo.columnFormatterMap.put(columnOrder, formatterClass.getDeclaredConstructor().newInstance());
                    }

                    Class<? extends FieldMapper> mapperClass = columnAnnotation.mapper();
                    if (mapperClass != FieldMapper.class) {
                        typeInfo.fieldMapperMap.put(fieldName, mapperClass.getDeclaredConstructor().newInstance());
                    }
                }
            }

            HELPERS_CACHE.put(recordType.getName(), typeInfo);

            return typeInfo;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static GSheetCellStyle getStyleForAnnotation(
            eu.ibagroup.easyrpa.openframework.googlesheets.annotations.GSheetCellStyle styleAnnotation) {
        GSheetCellStyle style = new GSheetCellStyle();

        style.setTextRotation(StyleAnnotationUtils.getRotation(styleAnnotation));

        style.setBackgroundColor(StyleAnnotationUtils.getBackgroundColor(styleAnnotation));

        style.setTextFormat(StyleAnnotationUtils.getTextFormat(styleAnnotation));

        style.setBorders(StyleAnnotationUtils.getBorders(styleAnnotation));

        style.setHorizontalAlignment(StyleAnnotationUtils.getHorizontalAlignment(styleAnnotation));

        style.setVerticalAlignment(StyleAnnotationUtils.getVerticalAlignment(styleAnnotation));

        style.setWrapStrategy(StyleAnnotationUtils.getWrapStrategy(styleAnnotation));

        style.setPadding(StyleAnnotationUtils.getPadding(styleAnnotation));

        style.setTextDirection(StyleAnnotationUtils.getTextDirection(styleAnnotation));

        return style;
    }
}
