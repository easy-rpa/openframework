# Copying of Sheet from one GoogleSheet to another

This process example demonstrates how copy Sheet from one spreadsheet file to another with preserving 
of all original styles.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `spreadsheet.id.copyFrom` | A unique spreadsheet id of Google Spreadsheet to copy from. |
| `spreadsheet.id.copyTo` | A unique spreadsheet id of Google Spreadsheet to copy to. |
| `Sheet.name` |Name of the Sheet that should be coped from `spreadsheet.id.copyFrom` . |
| 
**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.|

## Running

Run `main()` method of `LocalRunner` class.