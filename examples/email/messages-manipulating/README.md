# Mailbox messages manipulating

This process example show how it's possible to perform different actions with email messages using Email package 
functionality.    

## Configuration
All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `outbound.email.secret` | Vault alias that contains credentials for authentication on the email server for outbound emails |
| `inbound.email.server` | Host name and port of inbound email server |
| `inbound.email.protocol` | Protocol which is used by inbound email server |
| `inbound.email.secret` | Vault alias that contains credentials for authentication on the inbound email server |
| `email.sender.name` | Name of email sender that will be display as "from" for email recipients |
| `forwarded.email.recipients` | Email address where email message will be forwarded |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |


## Running

Run `main()` method of `LocalRunner` class.