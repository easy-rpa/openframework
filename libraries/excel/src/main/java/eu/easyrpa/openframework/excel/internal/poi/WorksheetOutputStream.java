package eu.easyrpa.openframework.excel.internal.poi;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class WorksheetOutputStream extends FilterOutputStream {

    private static final String ROWS_PLACEHOLDER = "<row/>";

    private StringBuffer buf;
    private char[] charsBuf;
    private byte[] byteBuf;

    private SheetRowsWriter rowsWriter;
    private boolean rowsWritten = false;

    public WorksheetOutputStream(OutputStream out, SheetRowsWriter rowsWriter) {
        this(out, rowsWriter, 8192);
    }

    public WorksheetOutputStream(OutputStream out, SheetRowsWriter rowsWriter, int capacity) {
        super(out);
        buf = new StringBuffer(capacity);
        charsBuf = new char[capacity];
        byteBuf = new byte[capacity];
        this.rowsWriter = rowsWriter;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (rowsWritten) {
            out.write(b, off, len);
        } else {
            buf.append(toChars(b, off, len), 0, len);

            if (buf.length() - ROWS_PLACEHOLDER.length() > 0) {
                int rowsPhIndex = buf.indexOf(ROWS_PLACEHOLDER);
                if (rowsPhIndex >= 0) {
                    byte[] bytes = toBytes(buf, 0, buf.length());
                    out.write(bytes, 0, rowsPhIndex);
                    rowsWriter.writeRows(out);
                    out.write(bytes, rowsPhIndex + ROWS_PLACEHOLDER.length(), len - rowsPhIndex - ROWS_PLACEHOLDER.length());
                    buf.setLength(0);
                    rowsWritten = true;

                } else {
                    int amount = buf.length() - ROWS_PLACEHOLDER.length();
                    byte[] bytes = toBytes(buf, 0, amount);
                    out.write(bytes, 0, amount);
                    buf.setLength(ROWS_PLACEHOLDER.length());
                }
            }
        }
    }

    @Override
    public String toString() {
        return out.toString();
    }

    private char[] toChars(byte[] bytes, int off, int len) {
        if (len > charsBuf.length) {
            charsBuf = new char[len];
            byteBuf = new byte[len];
        }
        for (int i = off, j = 0; j < len; i++, j++) {
            charsBuf[j] = (char) bytes[i];
        }
        return charsBuf;
    }

    private byte[] toBytes(StringBuffer buf, int off, int len) {
        buf.getChars(0, len, charsBuf, 0);
        for (int i = 0; i < len; i++) {
            byteBuf[i] = (byte) charsBuf[i];
        }
        return byteBuf;
    }
}
