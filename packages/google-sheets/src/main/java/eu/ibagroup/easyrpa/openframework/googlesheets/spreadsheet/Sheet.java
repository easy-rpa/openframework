package eu.ibagroup.easyrpa.openframework.googlesheets.spreadsheet;

public class Sheet {

    private com.google.api.services.sheets.v4.model.Sheet googleSheet;

    public Sheet(com.google.api.services.sheets.v4.model.Sheet googleSheet) {
        this.googleSheet = googleSheet;
    }

    public String getName(){
        return googleSheet.getProperties().getTitle();
    }

    public void rename(String name){
        googleSheet.getProperties().setTitle(name);
    }
}
