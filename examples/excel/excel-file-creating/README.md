# Creating of new Excel file

Example of process that creates a simple XLSX file on the local file system.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `sample.data.file` | Path to JSON file that contains sample data for this process. It can be path on local file system or within resources of this project. |
| `output.files.dir` | Path to directory on local file system where robot will put all created within this process spreadsheet files. |

## Running

Run `main()` method of `LocalRunner` class.