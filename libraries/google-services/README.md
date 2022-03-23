# Google API Services

### Table of Contents
* [Description](#description)
* [Usage](#usage)

### Description

Component which creates java services to operate Google API for Drive, Sheets, Mail.

### Usage

To start use the library first you need to add corresponding Maven dependency to your project.

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-openframework-google-drive)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-openframework-google-services</artifactId>
    <version>1.0</version>
</dependency>
```

Additionally, to let the library collaborate with RPA platform make sure that Maven dependency to corresponding adapter 
is added also. 

![mavenVersion](https://img.shields.io/maven-central/v/eu.easyrpa/easy-rpa-adapter-for-openframework)
```xml
<dependency>
    <groupId>eu.easyrpa</groupId>
    <artifactId>easy-rpa-adapter-for-openframework</artifactId>
    <version>1.0</version>
</dependency>
```

All requests to the Google API must be authorized by an authenticated user.

### Client registration

Before using the component, you probably need to register your application with an authorization server to receive a client ID and client secret.

### Create a Google Cloud project
1. Open the [Google Cloud Console] (https://console.cloud.google.com/)
2. At the top-left, click **Menu > IAM & Admin > Create a Project**.
3. In the **Project Name** field, enter a descriptive name for your project.
4. In the **Location** field, click **Browse** to display potential locations for your project. Then, click **Select**.
5. Click **Create**. The console navigates to the Dashboard page and your project is created within a few minutes.

[Reference guide] https://developers.google.com/workspace/guides/create-project

### Create access credentials
Credentials are used to obtain an access token from Google's authorization servers, so your app can call Google Workspace APIs.
1. Open the [Google Cloud Console] (https://console.cloud.google.com/)
2. At the top-left, click **Menu > APIs & Services > Credentials**.
3. Click **Create Credentials > OAuth client ID**.
4. Click **Application type > Desktop app**.
5. In the "Name" field, type a name for the credential. This name is only shown in the Cloud Console.
6. Click **Create**. The OAuth client created screen appears, showing your new Client ID and Client secret.
7. Click **OK**. The newly created credential appears under "OAuth 2.0 Client IDs."

### Add test user
In order to use your project while it is not in production state, you should add test users.
1. 1. Open the [Google Cloud Console] (https://console.cloud.google.com/)
2. At the top-left, click **Menu > APIs & Services > OAuth consent screen **.
3. Scroll to **Test users** section and click **+Add Users**.
4. Specify email address under and click **Save** button.

### Choose auth scopes
Auth scopes express the permissions you request users to authorize for your app.
[Mail Scopes](https://developers.google.com/gmail/api/auth/scopes)
[Drive Scopes](https://developers.google.com/resources/api-libraries/documentation/drive/v2/java/latest/com/google/api/services/drive/DriveScopes.html)

### Creating of file

In this example we will create a file in Google Drive, and after we'll move it to a specific location.

First step - we need to configure Google Drive credentials in order to connect:

**vault.properties**

| Alias     | Value         |
| ------------- |---------------|
| `google.credentials` | Json with credentials in encoded with Base64. Example of json:<br>`{ "user": "sender@gmail.com", "password": "passphrase" }` |

Note: All necessary configuration files can be found in `src/main/resources` directory.

Next step - inside the main program we inject object of 'GoogleDrive':

```java
    @Inject
    private GoogleDrive drive;
```

And simply use a command to create a new file:

```java
Optional<GoogleFile> file = drive.createFile("test");
```

Now new file is available on Google Drive.

### Moving file to a specific location

In this section we will continue previous case and move previously created file to a specific folder.
To achieve this we simply use a command called 'moveFile':

```java
public class MoveFile extends ApTask {

    private final String newFolderName = "newDirectory";

    @Inject
    private GoogleDrive drive;

    public void execute() {

        log.info("Creating file");
        Optional<GoogleFile> file = drive.createFile(fileName);

        Optional<GoogleFolderInfo> folder = drive.getFolder(newFolderName);

        if (file.isPresent() && folder.isPresent()) {
            log.info("File created in root directory ");

            drive.moveFile(file.get(), folder.get());

            log.info("Now file in directory '{}'", newFolderName);
        } else {
            log.info("File wasn't created or directory not found");
        }
    }
}
```
