package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet;

import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;

public class Sheet {

    private com.google.api.services.sheets.v4.model.Sheet googleSheet;

    private Spreadsheet parentSpreadsheet;

    public Sheet(com.google.api.services.sheets.v4.model.Sheet googleSheet, Spreadsheet parent) {
        this.googleSheet = googleSheet;
        this.parentSpreadsheet = parent;
    }

    public String getName() {
        return googleSheet.getProperties().getTitle();
    }

    public int getId(){
        return googleSheet.getProperties().getSheetId();
    }

    public void rename(String name) {
        googleSheet.getProperties().setTitle(name);

        parentSpreadsheet.getRequests().add(new Request().setUpdateSheetProperties(
                new UpdateSheetPropertiesRequest()
                        .setProperties(googleSheet.getProperties())
                        .setFields("*")
        ));
    }
}
