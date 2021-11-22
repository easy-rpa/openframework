# Changing cells style

This process example demonstrates how to specify fonts, colors and borders for cells using Excel package functionality.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all edited within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.