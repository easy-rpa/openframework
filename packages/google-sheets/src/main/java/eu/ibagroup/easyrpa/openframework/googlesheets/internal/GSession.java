package eu.ibagroup.easyrpa.openframework.googlesheets.internal;

import com.google.api.services.sheets.v4.model.*;
import eu.ibagroup.easyrpa.openframework.googlesheets.Cell;
import eu.ibagroup.easyrpa.openframework.googlesheets.Row;
import eu.ibagroup.easyrpa.openframework.googlesheets.Sheet;
import eu.ibagroup.easyrpa.openframework.googlesheets.SpreadsheetDocument;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.UnknownFieldException;
import eu.ibagroup.easyrpa.openframework.googlesheets.exceptions.UpdateException;
import eu.ibagroup.easyrpa.openframework.googlesheets.utils.GSheetUtils;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GSession {
    private static final Integer KEY_FOR_NOT_VALUE_REQUESTS = 0;

    private static final String VALUE_FIELD = "userEnteredValue";

    private static final String STYLE_FIELD = "userEnteredFormat";

    private static final String VALUE_AND_STYLE_FIELD = VALUE_FIELD + "," + STYLE_FIELD;

    private List<Request> requests = new ArrayList<>();

    public void addCellValueRequest(Cell cell, SpreadsheetDocument document) {
        CellData googleCell = cell.getGCell();
        requests.add(new Request().setRepeatCell(new RepeatCellRequest()
                .setRange(new GridRange()
                        .setSheetId(document.getActiveSheet().getId())
                        .setStartRowIndex(cell.getRowIndex())
                        .setEndRowIndex(cell.getRowIndex() + 1)
                        .setStartColumnIndex(cell.getColumnIndex())
                        .setEndColumnIndex(cell.getColumnIndex() + 1)
                )
                .setCell(googleCell)
                .setFields(VALUE_FIELD)));
    }

    public void addCellFormulaRequest(Cell cell, SpreadsheetDocument document) {
        CellData googleCell = cell.getGCell();
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex() + 1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex() + 1)
                        )
                        .setCell(googleCell)
                        //user entered value?
                        .setFields(VALUE_FIELD)));
    }

    public void addCellStyleRequest(Cell cell, SpreadsheetDocument document) {
        requests.add(new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(new GridRange()
                                .setSheetId(document.getActiveSheet().getId())
                                .setStartRowIndex(cell.getRowIndex())
                                .setEndRowIndex(cell.getRowIndex() + 1)
                                .setStartColumnIndex(cell.getColumnIndex())
                                .setEndColumnIndex(cell.getColumnIndex() + 1)
                        )
                        .setCell(cell.getGCell())
                        .setFields(STYLE_FIELD)));
    }

    public void addRowValueRequest(Row row, SpreadsheetDocument document) {
        RowData rowData = row.getGSheetRow();
        requests.add(new Request().setUpdateCells(new UpdateCellsRequest()
                .setRange(new GridRange()
                        .setSheetId(document.getActiveSheet().getId())
                        .setStartRowIndex(row.getIndex())
                        .setEndRowIndex(row.getIndex() + 1)
                        .setStartColumnIndex(row.getFirstCellIndex())
                        .setEndColumnIndex(row.getLastCellIndex())
                )
                .setRows(Collections.singletonList(rowData))
                .setFields(VALUE_FIELD)));
    }

    public void addMergeCellsRequest(GridRange range) {
        requests.add(new Request().setMergeCells(
                new MergeCellsRequest()
                        .setMergeType("MERGE_ALL")
                        .setRange(range)));
    }

    public void addUnmergeCellsRequest(GridRange range) {
        requests.add(new Request().setUnmergeCells(
                new UnmergeCellsRequest().setRange(range)
        ));
    }

    public void addUpdateSheetNameRequest(eu.ibagroup.easyrpa.openframework.googlesheets.Sheet sheet) {
        requests.add(new Request().setUpdateSheetProperties(
                new UpdateSheetPropertiesRequest()
                        .setProperties(sheet.getGSheet().getProperties())
                        .setFields("*")
        ));
    }

    public void addUpdateSpreadsheetDocumentNameRequest(SpreadsheetDocument document) {
        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest()
                        .setProperties(document.getProperties())
                        .setFields("Title")
        ));
    }

    public void addDeleteSheetRequest(Sheet sheet) {
        addDeleteSheetRequest(sheet.getGSheet().getProperties().getSheetId());
    }

    public void addDeleteSheetRequest(String sheetName, SpreadsheetDocument document) {
        addDeleteSheetRequest(document.getSheet(sheetName));
    }

    public void addDeleteSheetRequest(int sheetIndex, SpreadsheetDocument document) {
        addDeleteSheetRequest(document.getSheetAt(sheetIndex));
    }

    public void addDeleteSheetRequest(int sheetId) {
        requests.add(new Request().setDeleteSheet(
                new DeleteSheetRequest()
                        .setSheetId(sheetId)
        ));
    }

    public void commit(SpreadsheetDocument document) {
        if (requests.size() > 0) {
            //   optimizeRequests();
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            try {
                document.getService().spreadsheets().batchUpdate(document.getId(), body).execute();
                requests.clear();
            } catch (IOException e) {
                throw new UpdateException(e.getMessage());
            }
        }
    }

    private void optimizeRequests() {
        Map<Integer, List<Request>> requestMap = divisionBySheetId(this.requests);
        List<Request> resultList = new ArrayList<>(requestMap.get(KEY_FOR_NOT_VALUE_REQUESTS));
        requestMap.remove(KEY_FOR_NOT_VALUE_REQUESTS);

        for (List<Request> list : requestMap.values()) {
            resultList.addAll(optimizeValueList(list));
        }
        this.requests = resultList;
    }

    private Map<Integer, List<Request>> divisionBySheetId(List<Request> requestList) {
        Map<Integer, List<Request>> requestMap = new HashMap<>();
        //for not value requests, such as delete/clone sheet
        requestMap.put(KEY_FOR_NOT_VALUE_REQUESTS, new ArrayList<>());
        for (Request request : requestList) {
            if (request.getRepeatCell() != null) {
                GridRange range = request.getRepeatCell().getRange();
                if (!requestMap.containsKey(range.getSheetId())) {
                    requestMap.put(range.getSheetId(), new ArrayList<>());
                }
                requestMap.get(range.getSheetId()).add(request);
            } else {
                requestMap.get(KEY_FOR_NOT_VALUE_REQUESTS).add(request);
            }
        }
        return requestMap;
    }

    private List<Request> optimizeValueList(List<Request> requestList) {
        List<RepeatCellRequest> valueList = convertToValueList(requestList);
        valueList = mergeDataWithStyle(valueList);
        Map<String, List<RepeatCellRequest>> stylesAndData = styleAndDataDivision(valueList);
        List<UpdateCellsRequest> rowValueRequests = mergeIntoRowsRequests(stylesAndData);
        return convertToRequestList(rowValueRequests);
    }

    private Map<String, List<RepeatCellRequest>> styleAndDataDivision(List<RepeatCellRequest> valueList) {
        Map<String, List<RepeatCellRequest>> resultMap = new HashMap<>();
        resultMap.put(STYLE_FIELD, new ArrayList<>());
        resultMap.put(VALUE_FIELD, new ArrayList<>());
        resultMap.put(VALUE_AND_STYLE_FIELD, new ArrayList<>());
        for (RepeatCellRequest request : valueList) {
            switch (request.getFields()) {
                case STYLE_FIELD: {
                    resultMap.get(STYLE_FIELD).add(request);
                    break;
                }
                case VALUE_FIELD: {
                    resultMap.get(VALUE_FIELD).add(request);
                    break;
                }
                case VALUE_AND_STYLE_FIELD: {
                    resultMap.get(VALUE_AND_STYLE_FIELD).add(request);
                    break;
                }
                default: {
                    throw new UnknownFieldException("Can not divide into map unknowns fields");
                }
            }
        }
        return resultMap;
    }

    private List<RepeatCellRequest> mergeDataWithStyle(List<RepeatCellRequest> requestList) {
        List<RepeatCellRequest> resultList = new ArrayList<>();
        for (RepeatCellRequest request : requestList) {
            boolean isUsed = false;

            for (RepeatCellRequest item : resultList) {
                if (GSheetUtils.isTheSameCellRange(item.getRange(), request.getRange())) {
                    isUsed = true;
                    mergeCellData(item.getCell(), request.getCell());
                    if (!item.getFields().equals(request.getFields())) {
                        item.setFields(VALUE_AND_STYLE_FIELD);
                    }
                    break;
                }
            }
            if (!isUsed) {
                resultList.add(request);
            }
        }
        return resultList;
    }

    //if cell has not empty field and mainCell has it, mainCell will overwrite it
    private void mergeCellData(CellData cell, CellData mainCell) {
        if (mainCell.getUserEnteredFormat() != null) {
            cell.setUserEnteredFormat(mainCell.getUserEnteredFormat());
        }
        if (mainCell.getUserEnteredValue() != null) {
            cell.setEffectiveValue(mainCell.getUserEnteredValue());
        }
        if (mainCell.getEffectiveFormat() != null) {
            cell.setEffectiveFormat(mainCell.getEffectiveFormat());
        }
        if (mainCell.getEffectiveValue() != null) {
            cell.setEffectiveValue(mainCell.getEffectiveValue());
        }
        if (mainCell.getDataValidation() != null) {
            cell.setDataValidation(mainCell.getDataValidation());
        }
        if (mainCell.getFormattedValue() != null) {
            cell.setFormattedValue(mainCell.getFormattedValue());
        }
        if (mainCell.getUserEnteredFormat() != null) {
            cell.setUserEnteredFormat(mainCell.getUserEnteredFormat());
        }
        if (mainCell.getHyperlink() != null) {
            cell.setHyperlink(mainCell.getHyperlink());
        }
        if (mainCell.getNote() != null) {
            cell.setNote(mainCell.getNote());
        }
        if (mainCell.getPivotTable() != null) {
            cell.setPivotTable(mainCell.getPivotTable());
        }
        if (mainCell.getTextFormatRuns() != null) {
            cell.setTextFormatRuns(mainCell.getTextFormatRuns());
        }
    }

    private List<Request> convertToRequestList(List<UpdateCellsRequest> rowValueRequests) {
        return  rowValueRequests.stream().map(request -> new Request().setUpdateCells(request)).collect(Collectors.toList());
    }

    private List<RepeatCellRequest> convertToValueList(List<Request> requestList) {
        return requestList.stream().map(Request::getRepeatCell).collect(Collectors.toList());
    }


    private List<UpdateCellsRequest> mergeIntoRowsRequests(Map<String, List<RepeatCellRequest>> requestMap) {
        List<UpdateCellsRequest> resultList = new ArrayList<>();
        for (String key : requestMap.keySet()) {
            RangeDataList rangeDataList = new RangeDataList(key);
            for (RepeatCellRequest request : requestMap.get(key)) {
                rangeDataList.insertRequest(request);
            }
            resultList.addAll(rangeDataList.getMergedRequests());
        }
        return resultList;
    }
}
