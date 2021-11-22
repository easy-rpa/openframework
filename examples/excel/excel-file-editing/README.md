# Editing of existing Excel file

Example of process that edits specific cells within provided as input XLSX file.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to spreadsheet file that has to be edited. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all edited within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.