# Running of custom VB script

This process example demonstrates how to run custom VB script for some spreadsheet document using Excel package 
functionality.

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `source.spreadsheet.file` | Path to source spreadsheet file. It can be path on local file system or within resources of this project. |
| `vb.script.file` | Path to VBS file that contains script to run. It can be path on local file system or within resources of this project. |

## Running

Run `main()` method of `LocalRunner` class.