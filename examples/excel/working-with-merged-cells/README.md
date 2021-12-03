# Working with merged cells

This process example demonstrates Excel package functionality to merge/unmerge cell groups and read/edit its values.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to the source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all modified within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.