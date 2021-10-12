package eu.ibagroup.easyrpa.openframework.excel.function;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * Represents a function that maps row data to a record of specified type <T>.
 *
 * @param <T> the type of the record
 */
@FunctionalInterface
public interface RecordMapper<T> {

    /**
     * Applies this mapper to the given arguments.
     *
     * @param row       - instance of POI row.
     * @param columns   - ordered list of column headers. It's useful to get index
     *                  of necessary column by it's header.
     * @param evaluator - instance of formula evaluator that is configured for
     *                  current workbook.
     * @return instance of related record
     */
    T apply(Row row, List<String> columns, FormulaEvaluator evaluator);
}
