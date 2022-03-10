package eu.ibagroup.easyrpa.openframework.excel.internal.poi;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface SheetRowsWriter {

    void writeRows(OutputStream out) throws IOException;
}
