package eu.ibagroup.easyrpa.openframework.googlesheets.internal;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.GSheetUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RangeDataList {
    private List<GridRange> ranges;

    //list rowdata = rows to include in request
    //list list rowdata = structure that saves all list rowdata
    //it's needed to create merged row ranges
    private List<List<RowData>> rowsDataList;

    private String updateType;

    public RangeDataList(String updateType) {
        this.ranges = new ArrayList<>();
        this.rowsDataList = new ArrayList<>();
        this.updateType = updateType;
    }

    public List<GridRange> getRanges() {
        return ranges;
    }

    public void setRanges(List<GridRange> ranges) {
        this.ranges = ranges;
    }

    public List<List<RowData>> getRowsDataList() {
        return rowsDataList;
    }

    public void setRowsDataList(List<List<RowData>> rowsDataList) {
        this.rowsDataList = rowsDataList;
    }

    public void insertRequest(RepeatCellRequest request) {
        boolean isUsed = false;
        for (int i = 0; i < ranges.size(); i++) {
            if (GSheetUtils.isOneRangeNextToAnother(ranges.get(i), request.getRange())) {
                mergeCellWithRow(request, rowsDataList.get(i), ranges.get(i));
                ranges.set(i, mergeRanges(ranges.get(i), request.getRange()));
                isUsed = true;
            }
        }
        if (!isUsed) {
            ranges.add(request.getRange());
            List<RowData> newRow = new ArrayList<>();
            newRow.add(createRow(request.getCell()));
            rowsDataList.add(newRow);
        }
    }

    public List<UpdateCellsRequest> getMergedRequests() {
        //try to merge regions between each other
        List<UpdateCellsRequest> resultList = new ArrayList<>();
        for(int i=0; i<ranges.size();i++){
            UpdateCellsRequest request = new UpdateCellsRequest()
                    .setRange(ranges.get(i))
                    .setRows(rowsDataList.get(i))
                    .setFields(updateType);
            resultList.add(request);
        }
        return resultList;
    }
    //переделать через мапку

    private void mergeCellWithRow(RepeatCellRequest request, List<RowData> rowData, GridRange rowRange) {
        GridRange cellRange = request.getRange();
        if (cellRange.getStartRowIndex() < rowRange.getStartRowIndex()) {
            rowData.add(0, createRow(request.getCell()));
            return;
        }
        if (cellRange.getEndRowIndex() > rowRange.getEndRowIndex()) {
            rowData.add(createRow(request.getCell()));
            return;
        }
        if (cellRange.getStartColumnIndex() < rowRange.getStartColumnIndex()) {
            rowData.get(0).getValues().add(0, request.getCell());
            return;
        }
        if (cellRange.getEndColumnIndex() > rowRange.getEndColumnIndex()) {
            rowData.get(0).getValues().add( request.getCell());
        }
    }

    private RowData createRow(CellData cell) {
        List<CellData> dataList = new ArrayList<>();
        dataList.add(cell);
        return new RowData().setValues(dataList);
    }

    private GridRange mergeRanges(GridRange range1, GridRange range2) {
        GridRange resultRange = range1.clone();
        if (resultRange.getStartRowIndex() > range2.getStartRowIndex()) {
            resultRange.setStartRowIndex(range2.getStartRowIndex());
        }
        if (resultRange.getEndRowIndex() < range2.getEndRowIndex()) {
            resultRange.setEndRowIndex(range2.getEndRowIndex());
        }
        if (resultRange.getStartColumnIndex() > range2.getStartColumnIndex()) {
            resultRange.setStartColumnIndex(range2.getStartColumnIndex());
        }
        if (resultRange.getEndColumnIndex() < range2.getEndColumnIndex()) {
            resultRange.setEndColumnIndex(range2.getEndColumnIndex());
        }
        return resultRange;
    }
}
