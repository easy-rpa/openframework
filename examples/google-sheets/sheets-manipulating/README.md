# Sheets manipulating

This process example show how it's possible to perform different actions with sheets GoogleSheets package functionality.  

## Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `spreadsheet.id` | A unique spreadsheet id that can be found in the table properties on google drive. |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64.<br> |

## Running

Run `main()` method of `LocalRunner` class.