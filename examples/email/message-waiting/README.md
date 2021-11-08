# Waiting of message based on specified condition

Example of process that periodically looks up email messages with specific keywords in subject or body. Process ends 
when email message with corresponding keywords is present or appears in mailbox.    

## Configuration
All necessary configuration files can be found in `src/main/resources` directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `inbound.email.server` | Host name and port of inbound email server |
| `inbound.email.protocol` | Protocol which is used by inbound email server |
| `inbound.email.secret` | Vault alias that contains credentials for authentication on the inbound email server |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `mailbox` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

## Running

Run `main()` method of `LocalRunner` class.