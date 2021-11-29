# Copying of sheet from one Excel file to another

This process example demonstrates how copy sheet from one spreadsheet file to another with preserving 
of all original styles.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `source.sheet.name` | Name of sheet in the source spreadsheet file that has to be copied. |
| `target.spreadsheet.file` | Path to target spreadsheet file where the sheet has to be copied. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.