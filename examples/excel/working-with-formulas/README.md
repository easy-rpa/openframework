# Working with formulas

This process example shows how to evaluate and get values of cells that contains formulas.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `shared.spreadsheet.file` | Path to the shared spreadsheet file that has data used in formulas of source file.  It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.