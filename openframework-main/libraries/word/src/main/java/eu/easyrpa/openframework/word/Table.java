package eu.easyrpa.openframework.word;

import eu.easyrpa.openframework.word.constants.CellColor;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {

    /**
     * Unique id of parent Excel document.
     */
    private int documentId;

    /**
     * Reference to parent document part.
     */

    private WordprocessingMLPackage wordprocessingMLPackage;

    private Tbl doxcTable;


    protected Table(int id, WordprocessingMLPackage wordprocessingMLPackage) {
        this.wordprocessingMLPackage = wordprocessingMLPackage;
    }

    public Table() {
    }

    /**
     * Gets parent Word document.
     *
     * @return parent Word document.
     */

    public WordDocumentElement getDocumentElement() {
        return null;
    }


    /**
     * Create a new table. You can choose column and row count and set
     * <code>isTabbed</code> {@code true} or {@code false} to in order to put a tab after the table, if it will precede.
     * Or not to put a tab, in this case, if the table precedes, then the new table will be compatible with the previous one.
     *
     * @param mlPackage   the base instance needed to add width twips and save table element to hierarchy.
     * @param columnCount numeric value of the number of columns.
     * @param rowCount    numeric value of the number of rows.
     * @param isTabbed    true or false value. If you want to add tab after previous element use {@code true} if not {@code false}
     *                    E.g. If previous element was table, and you set {@code true} new table will be compatible with the previous one.
     */
    public void createTable(WordprocessingMLPackage mlPackage, int columnCount, int rowCount, boolean isTabbed) {
        int writableWidthTwips = mlPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
        if (isTabbed) {
            mlPackage.getMainDocumentPart().getJaxbElement().getBody().getContent()
                    .add(mlPackage.getMainDocumentPart().createParagraphOfText(""));
        }
        this.doxcTable = TblFactory.createTable(rowCount, columnCount, writableWidthTwips / columnCount);  //with adding to document
        mlPackage.getMainDocumentPart().addObject(doxcTable);
    }


    /**
     * Replaces the specified text in cells by provided table element.
     *
     * @param table         docx4j element which represents the table in the hierarchy.
     * @param textToReplace key-value map where "key" is original string significance and "value" is the significance you want to replace.
     */
    public void replaceCellTextContent(Tbl table, List<Map<String, String>> textToReplace) {
        ArrayListWml<?> rowsList = (ArrayListWml<?>) table.getContent();
        for (Object row : rowsList) {
            Tr templateRow = (Tr) row;
            List<Object> rowsContent = templateRow.getContent();
            for (Object rowContent : rowsContent) {
                ArrayListWml<?> rList = (ArrayListWml<?>) ((P) ((Tc) ((JAXBElement<?>) rowContent).getValue()).getContent().get(0)).getContent();
                if (rList.isEmpty()) {
                    continue;
                }
                Text text = (Text) ((JAXBElement<?>) ((ArrayListWml<?>) ((R) rList.get(0)).getContent()).get(0)).getValue();
                for (Map<String, String> replacements : textToReplace) {
                    String replacementValue = replacements.get(text.getValue());
                    if (replacementValue != null) {
                        text.setValue(replacementValue);
                    }
                }
            }
        }
        this.doxcTable = table;
    }

    /**
     * Add selected rows and for it's - cells, by provided table element.
     *
     * @param table      docx4j element which represents the table in the hierarchy.
     * @param cellsCount numeric value of the number of cells.
     * @param rowsCount  numeric value of the number of rows.
     *                   E.g. For creating rows necessarily creating the cells. The cells will be blank.
     */
    public void addRows(Tbl table, int rowsCount, int cellsCount) {
        for (int j = 1; j <= rowsCount; j++) {
            Tr tr = Context.getWmlObjectFactory().createTr();
            table.getContent().add(tr);
            // The cells
            for (int i = 1; i <= cellsCount; i++) {
                Tc tc = Context.getWmlObjectFactory().createTc();
                tr.getContent().add(tc);
                TcPr tcPr = Context.getWmlObjectFactory().createTcPr();
                tc.setTcPr(tcPr);
                // <w:tcW w:w="4788" w:type="dxa"/>
                TblWidth cellWidth = Context.getWmlObjectFactory().createTblWidth();
                tcPr.setTcW(cellWidth);
                cellWidth.setType("dxa");
                // Cell content - an empty <w:p/>
                tc.getContent().add(Context.getWmlObjectFactory().createP());
            }
        }
        this.doxcTable = table;
    }

    /**
     * Get row by table docx4j element by index.
     *
     * @param table docx4j element which represents the table in the hierarchy.
     * @param index numeric index value.
     *              E.g. After manipulations (add row, delete row, etc.) index may be changed.
     * @return tr docx4j element which represents the row in the docx4j hierarchy.
     */
    public Tr getRowByIndex(Tbl table, int index) {
        return (Tr) ((ArrayListWml<?>) table.getContent()).get(index);
    }

    /**
     * Get row by tbl docx4j element by index.
     *
     * @param row   docx4j element which represents the table in the hierarchy.
     * @param index numeric index value.
     *              E.g. After manipulations (add cells, delete cells, etc.) index may be changed.
     * @return tc docx4j element which represents the cell in the docx4j hierarchy.
     */
    public Tc getCellByIndex(Tr row, int index) {
        return (Tc) ((JAXBElement<?>) row.getContent().get(index)).getValue();
    }

    /**
     * Set color of cell by provided tc docx4j object.
     * @param cell  docx4j element which represents the cell in the docx4j hierarchy.
     * @param color enum color (<code>{CellColor.RED, CellColor.BLUE, etc.}</code>.
     * @see CellColor
     */
    public void setCellColor(Tc cell, CellColor color) {
        if (color != null) {
            TcPr tableCellProperties = cell.getTcPr();
            if (tableCellProperties == null) {
                tableCellProperties = new TcPr();
                cell.setTcPr(tableCellProperties);
            }
            CTShd shd = new CTShd();
            shd.setFill(color.toString());
            tableCellProperties.setShd(shd);
        }
    }

    /**
     * Helper method to change default cell color.
     * @param cell  docx4j element which represents the cell in the docx4j hierarchy.
     */
    public void removeCellColor(Tc cell) {
        setCellColor(cell, CellColor.WHITE);
    }

    /**
     * Set inch cell width .
     * @param cell  docx4j element which represents the cell in the docx4j hierarchy.
     * @param inchWidth a floating value that specifies how many inches you want to resize the cell.
     * E.g. Шn docx4j jaxb element which present "width" using dxa system to set width value.
     */
    public void setCellWidth(Tc cell, float inchWidth) {
        if (inchWidth > 0) {
            float floatWidth = inchWidth * 10;
            int width = (int) floatWidth  * 144;   // by dxa type system
            TcPr tableCellProperties = cell.getTcPr();
            if (tableCellProperties == null) {
                tableCellProperties = new TcPr();
                cell.setTcPr(tableCellProperties);
            }
            TblWidth tableWidth = new TblWidth();
            tableWidth.setType("dxa");
            tableWidth.setW(BigInteger.valueOf(width));
            tableCellProperties.setTcW(tableWidth);
        }
    }

    /**
     * Merging cells horizontally.
     * @param table docx4j element which represents the table in the hierarchy.
     * @param row numeric index of row (start from 0).
     *            E.g. It's means that if you want to choose 1-st row you should set 0-value.
     * @param startCell numeric value where the merge starts.
     * @param endCell numeric value where the merge ends.
     */
    public void mergeCellsHorizontal(Tbl table, int row, int startCell, int endCell) {
        if (row < 0 || startCell < 0 || endCell < 0) {
            return;
        }
        List<Tr> trList = getTblAllTr(table);
        if (row > trList.size()) {
            return;
        }
        Tr tr = trList.get(row);
        List<Tc> tcList = getTrAllCell(tr);
        for (int cellIndex = startCell, len = Math.min(tcList.size() - 1, endCell); cellIndex <= len; cellIndex++) {
            Tc tc = tcList.get(cellIndex);
            TcPr tcPr = getTcPr(tc);
            TcPrInner.HMerge hMerge = tcPr.getHMerge();
            if (hMerge == null) {
                hMerge = new TcPrInner.HMerge();
                tcPr.setHMerge(hMerge);
            }
            if (cellIndex == startCell) {
                hMerge.setVal("restart");
            } else {
                hMerge.setVal("continue");
            }
        }
    }

    /**
     * Merging cells horizontally.
     * @param table docx4j element which represents the table in the hierarchy.
     * @param col numeric index of column (start from 0).
     *            E.g. It's means that if you want to choose 1-st column you should set 0-value.
     * @param startRow numeric value where the row merge starts.
     * @param endRow numeric value where the row merge ends.
     */
    public void mergeCellsVertically(Tbl table, int col, int startRow, int endRow) {
        if (col < 0 || startRow < 0 || endRow < 0) {
            return;
        }
        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Tc tc = getTc(table, rowIndex, col);
            if (tc == null) {
                break;
            }
            TcPr tcPr = getTcPr(tc);
            TcPrInner.VMerge vMerge = tcPr.getVMerge();
            if (vMerge == null) {
                vMerge = new TcPrInner.VMerge();
                tcPr.setVMerge(vMerge);
            }
            if (rowIndex == startRow) {
                vMerge.setVal("restart");
            } else {
                vMerge.setVal("continue");
            }
        }
    }

    /**
     * Helper method to get cell from Tbl docx4j element.
     * @param table docx4j element which represents the table in the hierarchy.
     * @param row numeric index of row.
     * @param cell numeric index of cell.
     * For example <code>{row = 2, cell = 3}</code> it will return a docx4j cell by the 1st row index and the 2nd cell index.
     * @return tc docx4j element by provided table element, row and cell index.
     */
    private Tc getTc(Tbl table, int row, int cell) {
        if (row < 0 || cell < 0) {
            return null;
        }
        List<Tr> trList = getTblAllTr(table);
        if (row >= trList.size()) {
            return null;
        }
        List<Tc> tcList = getTrAllCell(trList.get(row));
        if (cell >= tcList.size()) {
            return null;
        }
        return tcList.get(cell);
    }

    /**
     * Helper method to all cells by provided docx4j row element.
     * @param row docx4j element which represents the row in the table docx4j hierarchy.
     * @return the List of cells docx4j element by provided row element.
     * @throws ClassCastException if cell element missing or damaged.
     */
    private List<Tc> getTrAllCell(Tr row) {
        ArrayListWml<?> objects = (ArrayListWml<?>) row.getContent();
        List<Tc> tcList = new ArrayList<>();
        if (objects.isEmpty()) {
            return tcList;
        }
        for (Object tcObj : objects) {
            try {
                JAXBElement<?> jaxbElement = (JAXBElement<?>) tcObj;
                if (jaxbElement.getValue() instanceof Tc) {
                    Tc objTc = (Tc) jaxbElement.getValue();
                    tcList.add(objTc);
                }
            } catch (ClassCastException e) {
                return tcList;
            }
        }
        return tcList;
    }

    /**
     * Helper method to all row by provided docx4j table element.
     * @param table docx4j element which represents the table in the docx4j hierarchy.
     * @return the List of rows docx4j element by provided table element.
     */
    private List<Tr> getTblAllTr(Tbl table) {
        ArrayListWml<?> objects = (ArrayListWml<?>) table.getContent();
        List<Tr> trList = new ArrayList<>();
        if (objects.isEmpty()) {
            return trList;
        }
        for (Object obj : objects) {
            if (obj instanceof Tr) {
                Tr tr = (Tr) obj;
                trList.add(tr);
            }
        }
        return trList;
    }

    /**
     * Helper method to create specific instance docx4j TcPr.
     * @param cell the element represented cell in docx4j structure.
     * @return new TcPr instance.
     */
    private TcPr getTcPr(Tc cell) {
        TcPr tcPr = cell.getTcPr();
        if (tcPr == null) {
            tcPr = new TcPr();
            cell.setTcPr(tcPr);
        }
        return tcPr;
    }
//        List<Tc> tcList = getTrAllCell(tr);
//        for (int cellIndex = fromCell, len = Math.min(tcList.size() - 1, toCell); cellIndex <= len; cellIndex++) {
//            Tc tc = tcList.get(cellIndex);
//            TcPr tcPr = getTcPr(tc);
//            HMerge hMerge = tcPr.getHMerge();
//            if (hMerge == null) {
//                hMerge = new HMerge();
//                tcPr.setHMerge(hMerge);
//            }
//            if (cellIndex == fromCell) {
//                hMerge.setVal("restart");
//            } else {
//                hMerge.setVal("continue");
//            }
//        }
//    }
//
//    public static void mergeCellsVertically(Tr row, int col, int fromRow, int toRow) {
//        if (col < 0 || fromRow < 0 || toRow < 0) {
//            return;
//        }
//        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
//            Tc tc = getTc(tbl, rowIndex, col);
//            if (tc == null) {
//                break;
//            }
//            TcPr tcPr = getTcPr(tc);
//            VMerge vMerge = tcPr.getVMerge();
//            if (vMerge == null) {
//                vMerge = new VMerge();
//                tcPr.setVMerge(vMerge);
//            }
//            if (rowIndex == fromRow) {
//                vMerge.setVal("restart");
//            } else {
//                vMerge.setVal("continue");
//            }
//        }
//    }

//    public void setCellNoWrap(Tc tableCell) {
//        TcPr tableCellProperties = tableCell.getTcPr();
//        if (tableCellProperties == null) {
//            tableCellProperties = new TcPr();
//            tableCell.setTcPr(tableCellProperties);
//        }
//        BooleanDefaultTrue b = new BooleanDefaultTrue();
//        b.setVal(true);
//        tableCellProperties.setNoWrap(b);
//    }
//
//    public void setCellVMerge(Tc tableCell, String mergeVal) {
//        if (mergeVal != null) {
//            TcPr tableCellProperties = tableCell.getTcPr();
//            if (tableCellProperties == null) {
//                tableCellProperties = new TcPr();
//                tableCell.setTcPr(tableCellProperties);
//            }
//            TcPrInner.VMerge merge = new TcPrInner.VMerge();
//            if (!"close".equals(mergeVal)) {
//                merge.setVal(mergeVal);
//            }
//            tableCellProperties.setVMerge(merge);
//            tableCell.setTcPr(tableCellProperties);
//        }
//    }
//
//    public void setCellHMerge(Tc tableCell, int horizontalMergedCells) {
//        if (horizontalMergedCells > 1) {
//            TcPr tableCellProperties = tableCell.getTcPr();
//            if (tableCellProperties == null) {
//                tableCellProperties = new TcPr();
//                tableCell.setTcPr(tableCellProperties);
//            }
//            TcPrInner.GridSpan gridSpan = new TcPrInner.GridSpan();
//            gridSpan.setVal(new BigInteger(String
//                    .valueOf(horizontalMergedCells)));
//            tableCellProperties.setGridSpan(gridSpan);
//            tableCell.setTcPr(tableCellProperties);
//        }
//    }
//
//    public void setCellColor(Tc tableCell, String color) {
//        if (color != null) {
//            TcPr tableCellProperties = tableCell.getTcPr();
//            if (tableCellProperties == null) {
//                tableCellProperties = new TcPr();
//                tableCell.setTcPr(tableCellProperties);
//            }
//            CTShd shd = new CTShd();
//            shd.setFill(color);
//            tableCellProperties.setShd(shd);
//        }
//    }
//
//    public void setCellMargins(Tc tableCell, int top, int right, int bottom,
//                               int left) {
//        TcPr tableCellProperties = tableCell.getTcPr();
//        if (tableCellProperties == null) {
//            tableCellProperties = new TcPr();
//            tableCell.setTcPr(tableCellProperties);
//        }
//        TcMar margins = new TcMar();
//        if (bottom > 0) {
//            TblWidth bW = new TblWidth();
//            bW.setType("dxa");
//            bW.setW(BigInteger.valueOf(bottom));
//            margins.setBottom(bW);
//        }
//        if (top > 0) {
//            TblWidth tW = new TblWidth();
//            tW.setType("dxa");
//            tW.setW(BigInteger.valueOf(top));
//            margins.setTop(tW);
//        }
//        if (left > 0) {
//            TblWidth lW = new TblWidth();
//            lW.setType("dxa");
//            lW.setW(BigInteger.valueOf(left));
//            margins.setLeft(lW);
//        }
//        if (right > 0) {
//            TblWidth rW = new TblWidth();
//            rW.setType("dxa");
//            rW.setW(BigInteger.valueOf(right));
//            margins.setRight(rW);
//        }
//        tableCellProperties.setTcMar(margins);
//    }
//
//
//    public Tbl getDoxcTable() {
//        return doxcTable;
//    }
//
//    public void setDoxcTable(Tbl doxcTable) {
//        this.doxcTable = doxcTable;
//    }
}
