# Running of macros

This process example demonstrates how to run embedded to spreadsheet document macros using Excel package functionality.

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source macro-enabled spreadsheet file. It can be path on local file system or within resources of this project. |
| `macro.function.name` | Name of macro function that is going to be run. |
| `output.spreadsheet.file` | Path on local file system where robot will save modified by macro spreadsheet file. |
## Running

Run `main()` method of `LocalRunner` class.