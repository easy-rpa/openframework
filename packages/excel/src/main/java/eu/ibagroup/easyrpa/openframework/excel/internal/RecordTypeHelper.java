package eu.ibagroup.easyrpa.openframework.excel.internal;

import eu.ibagroup.easyrpa.openframework.excel.Cell;
import eu.ibagroup.easyrpa.openframework.excel.ExcelCellStyle;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelColumn;
import eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelTable;
import eu.ibagroup.easyrpa.openframework.excel.function.ColumnFormatter;
import eu.ibagroup.easyrpa.openframework.excel.function.FieldMapper;
import eu.ibagroup.easyrpa.openframework.excel.function.TableFormatter;
import eu.ibagroup.easyrpa.openframework.excel.utils.TypeUtils;

import java.lang.reflect.Field;
import java.util.*;

public class RecordTypeHelper<T> {

    private Class<T> recordType = null;
    private ExcelCellStyle tableHeaderCellStyle = null;
    private ExcelCellStyle tableCellStyle = null;
    private TableFormatter<T> tableFormatter = null;

    private List<String> fields = new ArrayList<>();
    private Map<String, String> fieldToColumnMap = new HashMap<>();
    private Map<String, Integer> columnNameToOrderMap = new HashMap<>();
    private Map<Integer, String> columnOrderToFieldMap = new HashMap<>();

    private Map<Integer, Integer> columnWidthMap = new HashMap<>();
    private Map<Integer, ExcelCellStyle> columnHeaderCellStyleMap = new HashMap<>();
    private Map<Integer, ExcelCellStyle> columnCellStyleMap = new HashMap<>();
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

    public void formatCell(Cell cell, String columnName, int recordIndex, List<T> records) {

        if (cell != null && columnName != null && !columnName.trim().isEmpty()) {

            Integer columnOrder = columnNameToOrderMap.get(columnName);
            if (columnOrder == null) {
                return;
            }

            ExcelCellStyle cellStyle = columnCellStyleMap.get(columnOrder);
            if (cellStyle != null) {
                cell.setStyle(cellStyle);
            } else if (tableCellStyle != null) {
                cell.setStyle(tableCellStyle);
            }

            if (tableFormatter != null) {
                tableFormatter.format(cell, columnName, recordIndex, records);
            }
            ColumnFormatter<T> formatter = columnFormatterMap.get(columnOrder);
            if (formatter != null) {
                T record = records != null && recordIndex >= 0 && recordIndex < records.size()
                        ? records.get(recordIndex)
                        : null;
                formatter.format(cell, columnName, record);
            }
        }
    }

    public void formatHeaderCell(Cell cell, String columnName) {

        if (cell != null && columnName != null && !columnName.trim().isEmpty()) {

            Integer columnOrder = columnNameToOrderMap.get(columnName);
            if (columnOrder == null) {
                return;
            }

            Integer width = columnWidthMap.get(columnOrder);
            if (width != null) {
                cell.getSheet().setColumnWidth(cell.getColumnIndex(), width);
            }

            ExcelCellStyle headerCellStyle = columnHeaderCellStyleMap.get(columnOrder);
            if (headerCellStyle != null) {
                cell.setStyle(headerCellStyle);
            } else if (tableHeaderCellStyle != null) {
                cell.setStyle(tableHeaderCellStyle);
            }

            if (tableFormatter != null) {
                tableFormatter.format(cell, columnName, -1, null);
            }
            ColumnFormatter<T> formatter = columnFormatterMap.get(columnOrder);
            if (formatter != null) {
                formatter.format(cell, columnName, null);
            }
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

            ExcelTable tableAnnotation = recordType.getAnnotation(ExcelTable.class);
            if (tableAnnotation != null) {
                eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelCellStyle[] styleAnnotations = tableAnnotation.cellStyle();
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
                ExcelColumn columnAnnotation = field.getAnnotation(ExcelColumn.class);
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

                    eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelCellStyle[] styleAnnotations = columnAnnotation.cellStyle();
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

    private static ExcelCellStyle getStyleForAnnotation(
            eu.ibagroup.easyrpa.openframework.excel.annotations.ExcelCellStyle styleAnnotation) {
        ExcelCellStyle style = new ExcelCellStyle();
        style.font(styleAnnotation.fontName());
        style.fontSize(styleAnnotation.fontSize());
        style.bold(styleAnnotation.bold());
        style.italic(styleAnnotation.italic());
        style.strikeout(styleAnnotation.strikeout());
        style.underline(styleAnnotation.underline());
        style.fontOffset(styleAnnotation.fontOffset());
        style.color(styleAnnotation.color().get());
        style.format(styleAnnotation.dataFormat().get());
        style.background(styleAnnotation.background().get());
        style.fill(styleAnnotation.fill());
        style.hAlign(styleAnnotation.hAlign());
        style.vAlign(styleAnnotation.vAlign());
        style.wrapText(styleAnnotation.wrapText());
        style.rotation(styleAnnotation.rotation());
        style.topBorder(styleAnnotation.topBorder(), styleAnnotation.topBorderColor().get());
        style.rightBorder(styleAnnotation.rightBorder(), styleAnnotation.rightBorderColor().get());
        style.bottomBorder(styleAnnotation.bottomBorder(), styleAnnotation.bottomBorderColor().get());
        style.leftBorder(styleAnnotation.leftBorder(), styleAnnotation.leftBorderColor().get());
        style.hidden(styleAnnotation.hidden());
        style.locked(styleAnnotation.locked());
        style.indention(styleAnnotation.indention());
        return style;
    }
}