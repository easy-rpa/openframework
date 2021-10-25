# Waiting of message based on specified condition

Example of process that periodically looks up email messages with specific keywords in subject or body. Process ends 
when email message with corresponding keywords is present or appears in mailbox.    

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