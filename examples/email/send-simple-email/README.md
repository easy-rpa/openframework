# Sending of simple email

Example of process that sends simple email message. 

To run this example it's necessary to specify configuration for email service that is going to be used for 
message sending. All necessary configuration files can be found in <code>src/main/resources</code> directory.

**apm_run.properties**

| Parameter     | Value         |
| ------------- |---------------|
| <code>email.service</code> | Host name and port of email server |
| <code>email.service.protocol</code> | Protocol which is used by email server |
| <code>email.service.credentials</code> | Vault alias that contains credentials for authentication on the email server |
| <code>email.recipients</code> | Email address where email message will be sent |

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| <code>email.user</code> | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |
