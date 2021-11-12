# Exporting Excel file to PDF

This process example demonstrates how to export spreadsheet document to PDF file using Excel package functionality.

**IMPORTANT:** Excel package uses MS Excel functionality to perform exporting to PDF. To run this process without 
errors it's required the MS Excel to be installed on machine where the process is running. 

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.pdf.file` | Path on local file system where spreadsheet document is going to be exported. |

## Running

Run `main()` method of `LocalRunner` class.