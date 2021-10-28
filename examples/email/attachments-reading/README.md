# Reading of attached files

Example of process that looks up email with attachments and reads content of attached files.    

## Configuration
All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `email.service` | Host name and port of email server |
| `email.service.protocol` | Protocol which is used by email server |
| `email.service.credentials` | Vault alias that contains credentials for authentication on the email server |
| `email.recipients` | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |


## Running

Run `main()` method of `LocalRunner` class.