package eu.ibagroup.easyrpa.openframework.excel;


import java.util.ArrayList;
import java.util.List;

public class ExcelRowStyle {

    private List<ExcelCellStyle> cellStyles;


    public ExcelRowStyle() {
    }

    protected ExcelRowStyle(Row row) {
        cellStyles = new ArrayList<>();
        for (Cell cell : row) {
            cellStyles.add(cell != null ? cell.getStyle() : null);
        }
    }
}
