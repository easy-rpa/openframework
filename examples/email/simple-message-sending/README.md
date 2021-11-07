# Sending of simple email message

Example of process that sends simple email message.  

##Configuration
All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| `outbound.email.server` | Host name and port of email server for outbound emails |
| `outbound.email.protocol` | Protocol which is used by email server for outbound emails |
| `outbound.email.secret` | Vault alias that contains credentials for authentication on the email server for outbound emails |
| `email.sender.name` | Name of email sender that will be display as "from" for email recipients |
| `email.recipients` | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `email.user` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

##Running

Run `main()` method of `LocalRunner` class.