# Creating of message based on FreeMarker template

This example shows how properly to create complex email message with large body that depends on many inputs. 

 ![Image](message_screenshot.png 'Complex message example')

In such cases much more convenient to keep and edit text of the body in separate file. This can be achieved using 
FreeMarker templates that are supported by Email component.

For more details about possibilities of FreeMarker templates see
 [FreeMarker Manual](https://freemarker.apache.org/docs/index.html)
 
 
## Configuration

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


## Running

Run `main()` method of `LocalRunner` class.