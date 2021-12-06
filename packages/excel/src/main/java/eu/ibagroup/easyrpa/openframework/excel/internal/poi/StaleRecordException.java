package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

public class StaleRecordException extends RuntimeException {

    public StaleRecordException(int recordNum) {
        super(String.format("Record '%s' has stale. " +
                "It's necessary to get it from sheet again to continue the work with it.", recordNum));
    }
}
