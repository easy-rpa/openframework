package eu.ibagroup.easyrpa.openframework.googlesheets;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static eu.ibagroup.easyrpa.openframework.googlesheets.utils.GSheetUtils.convertNumToColString;

//TODO Supporting of styles coping per row (getting of while row style and applying to another row)
public class Row implements Iterable<Cell> {

    private String id;

    private GoogleSheets service;

    private String spreadsheetId;

    private String sheetName;

    private int rowIndex;

    public Row(GoogleSheets service, String spreadsheetId, String sheetName, int rowIndex) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.rowIndex = rowIndex;
        this.sheetName = sheetName;
        this.id = StringUtils.isEmpty(this.sheetName) ? String.valueOf(this.rowIndex) : this.sheetName + "|" + this.rowIndex;
    }

    public Row(GoogleSheets service, String spreadsheetId, int rowIndex) {
        this.service = service;
        this.spreadsheetId = spreadsheetId;
        this.rowIndex = rowIndex;
        this.sheetName = "";
        this.id = String.valueOf(this.rowIndex);
    }

    @Override
    public Iterator<Cell> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Cell> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Cell> spliterator() {
        return Iterable.super.spliterator();
    }

    public List<String> getRange(int headerLeftCol, int headerRightCol, Class<String> stringClass) throws IOException {
        String leftCoord = convertNumToColString(headerLeftCol) + String.valueOf(rowIndex);
        String rightCoord = convertNumToColString(headerRightCol) + String.valueOf(rowIndex);
        String coord = leftCoord+":"+rightCoord;
        List<Object> vals = service.getValues(spreadsheetId, coord).getValues().get(0);
        return vals.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());

    }

    public List<Object> getRange(int hLeftCol, int hRightCol) throws IOException {
        ValueRange range = service.getValues("", "");
        return range.getValues().get(0);
    }
}
