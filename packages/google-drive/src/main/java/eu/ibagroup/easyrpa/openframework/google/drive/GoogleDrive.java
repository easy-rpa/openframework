package eu.ibagroup.easyrpa.openframework.google.drive;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import eu.ibagroup.easyrpa.openframework.core.sevices.RPAServicesAccessor;
import eu.ibagroup.easyrpa.openframework.google.drive.constants.GDriveConfigParam;
import eu.ibagroup.easyrpa.openframework.google.drive.exceptions.GoogleDriveException;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFile;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileType;
import eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService;
import eu.ibagroup.easyrpa.openframework.google.services.AuthorizationPerformer;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleAuth;
import eu.ibagroup.easyrpa.openframework.google.services.GoogleServicesProvider;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoogleDrive {

    private List<String> extraFileFields;

    private Boolean supportsTeamDrives;

    private GoogleServicesProvider googleServicesProvider;

    private GoogleDriveService service;

    private RPAServicesAccessor rpaServices;

    public GoogleDrive() {
        googleServicesProvider = new GoogleServicesProvider();
    }

    @Inject
    public GoogleDrive(RPAServicesAccessor rpaServices) {
        this.rpaServices = rpaServices;
        googleServicesProvider = new GoogleServicesProvider(rpaServices);
    }

    public GoogleDrive onAuthorization(AuthorizationPerformer authorizationPerformer) {
        googleServicesProvider.onAuthorization(authorizationPerformer);
        service = null;
        return this;
    }

    public GoogleDrive secret(String vaultAlias) {
        googleServicesProvider.secret(vaultAlias);
        return this;
    }

    public GoogleDrive secret(String userId, String secret) {
        googleServicesProvider.secret(userId, secret);
        service = null;
        return this;
    }

    public List<String> getExtraFileFields() {
        if (extraFileFields == null) {
            String extraFields = getConfigParam(GDriveConfigParam.EXTRA_FILE_FIELDS);
            if (extraFields != null) {
                extraFileFields = Arrays.stream(extraFields.split(";")).collect(Collectors.toList());
            } else {
                extraFileFields = new ArrayList<>();
            }
        }
        return extraFileFields;
    }

    public void setExtraFileFields(List<String> extraFileFields) {
        this.extraFileFields = extraFileFields;
        if (service != null) {
            service.setExtraFileFields(this.extraFileFields);
        }
    }

    public GoogleDrive extraFields(String... fields) {
        setExtraFileFields(Arrays.stream(fields).collect(Collectors.toList()));
        return this;
    }

    public boolean isSupportsTeamDrives() {
        if (supportsTeamDrives == null) {
            String supportsTeamDrivesStr = getConfigParam(GDriveConfigParam.SUPPORT_TEAM_DRIVES);
            supportsTeamDrives = supportsTeamDrivesStr == null || Boolean.getBoolean(supportsTeamDrivesStr);
        }
        return supportsTeamDrives;
    }

    public void setSupportsTeamDrives(boolean supportsTeamDrives) {
        this.supportsTeamDrives = supportsTeamDrives;
        if (service != null) {
            service.setSupportsTeamDrives(this.supportsTeamDrives);
        }
    }

    public GoogleDrive supportsTeamDrives(boolean supportsTeamDrives) {
        setSupportsTeamDrives(supportsTeamDrives);
        return this;
    }

    public List<GFileInfo> listFiles() {
        initService();
        return this.service.listFiles(String.format("mimeType != '%s' ", GFileType.FOLDER.toString()), null);
    }

    public List<GFileInfo> listFiles(GFileType fileType) {
        initService();
        return this.service.listFiles(String.format("mimeType = '%s' ", fileType.getMimeType()), null);
    }

    public List<GFileInfo> listFiles(GFileId parentId) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType != '%s' ", parentId.getId(), GFileType.FOLDER.toString()),
                null);
    }

    public List<GFileInfo> listFiles(GFileId parentId, GFileType fileType) {
        initService();
        return this.service.listFiles(
                String.format("'%s' in parents and mimeType = '%s' ", parentId.getId(), fileType.getMimeType()),
                null);
    }

    public List<GFileInfo> listFiles(String query) {
        return listFiles(query, null);
    }

    public List<GFileInfo> listFiles(String query, Integer pageSize) {
        initService();
        return this.service.listFiles(query, pageSize);
    }

    public Optional<GFile> getFile(String fileName) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileName).orElse(null));
    }

    public Optional<GFile> getFile(GFileId fileId) {
        initService();
        return this.service.getFile(this.service.getFileInfo(fileId).orElse(null));
    }

    public Optional<GFileInfo> getFolder(String folderName) {
        initService();
        return this.service.getFileInfo(folderName).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    public Optional<GFileInfo> getFolder(GFileId folderId) {
        initService();
        return this.service.getFileInfo(folderId).filter(f -> f.getFileType() == GFileType.FOLDER);
    }

    public Optional<GFileInfo> getFileInfo(GFileId fileId) {
        initService();
        return this.service.getFileInfo(fileId);
    }

    public Optional<GFileInfo> getFileInfo(String fileName) {
        initService();
        return this.service.getFileInfo(fileName);
    }

    public Optional<GFileInfo> create(String name, GFileType type) {
        return this.create(name, type, null);
    }

    public Optional<GFileInfo> create(String name, GFileType type, GFileId folderId) {
        if (name != null) {
            initService();
            return this.service.createFile(name, type, null, folderId);
        }
        return Optional.empty();
    }

    public Optional<GFileInfo> copy(GFileInfo source, String nameOfCopy) {
        if (source != null && nameOfCopy != null) {
            initService();
            return this.service.copyFile(source, nameOfCopy);
        }
        return Optional.empty();
    }

    public Optional<GFile> createFile(File file) {
        return createFile(file, null);
    }

    public Optional<GFile> createFile(File file, GFileId folderId) {
        if (file != null) {
            try {
                initService();
                FileContent fileContent = new FileContent(null, file);
                Optional<GFileInfo> newFile = this.service.createFile(file.getName(), GFileType.FILE, fileContent,
                        folderId);
                if (newFile.isPresent()) {
                    return Optional.of(new GFile(newFile.get(), fileContent.getInputStream()));
                }
            } catch (GoogleDriveException e) {
                throw e;
            } catch (Exception e) {
                throw new GoogleDriveException(String.format("Creating of file '%s' has failed.", file.getName()), e);
            }
        }
        return Optional.empty();
    }

    public Optional<GFile> createFile(String fileName, InputStream content) {
        return createFile(fileName, content, null);
    }

    public Optional<GFile> createFile(String fileName, InputStream content, GFileId folderId) {
        if (fileName != null && content != null) {
            initService();
            InputStreamContent fileContent = new InputStreamContent(null, content);
            Optional<GFileInfo> newFile = this.service.createFile(fileName, GFileType.FILE, fileContent, folderId);
            if (newFile.isPresent()) {
                return Optional.of(new GFile(newFile.get(), fileContent.getInputStream()));
            }
        }
        return Optional.empty();
    }



    public Optional<GFileInfo> createFolder(String folderName) {
        return createFolder(folderName, null);
    }

    public GFileInfo createFolderIfAbsent(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName).orElse(null));
    }

    public Optional<GFileInfo> createFolder(String folderName, GFileId parentId) {
        if (folderName != null) {
            initService();
            return this.service.createFile(folderName, GFileType.FOLDER, null, parentId);
        }
        return Optional.empty();
    }

    public GFileInfo createFolderIfAbsent(String folderName, GFileId parentId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        return folder.orElseGet(() -> createFolder(folderName, parentId).orElse(null));
    }

    public void renameFolder(String folderName, String newFolderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(fileInfo -> this.service.renameFile(fileInfo, newFolderName));
    }

    public void renameFolder(GFileInfo folder, String newFolderName) {
        renameFile(folder, newFolderName);
    }

    public void renameFile(GFileInfo file, String newFileName) {
        initService();
        this.service.renameFile(file, newFileName);
    }

    public void updateFile(GFileInfo file) {
        initService();
        this.service.updateFile(file);
    }

    public void moveFolder(String folderName, String targetFolderName) {
        Optional<GFileInfo> targetFolder = getFolder(targetFolderName);
        targetFolder.ifPresent(targetFolderInfo -> moveFolder(folderName, targetFolderInfo.getFileId()));
    }

    public void moveFolder(String folderName, GFileId targetFolderId) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(folderInfo -> this.service.moveFile(folderInfo, targetFolderId));
    }

    public void moveFile(GFileInfo file, GFileId targetFolderId) {
        initService();
        this.service.moveFile(file, targetFolderId);
    }

    public void deleteFile(GFileId fileId) {
        initService();
        this.service.deleteFile(fileId);
    }

    public void deleteFile(GFileInfo file) {
        initService();
        this.service.deleteFile(file.getFileId());
    }

    public void deleteFolder(GFileId folderId) {
        initService();
        this.service.deleteFile(folderId);
    }

    public void deleteFolder(String folderName) {
        Optional<GFileInfo> folder = getFolder(folderName);
        folder.ifPresent(f -> this.service.deleteFile(f.getFileId()));
    }

    public Drive getDrive() {
        initService();
        return service.getDrive();
    }

    /**
     * Gets value of configuration parameter specified in the RPA platform by the given key.
     *
     * @param key the key of configuration parameter that need to lookup.
     * @return string value of configuration parameter with the given key. Returns <code>null</code> if parameter is
     * not found or {@link RPAServicesAccessor} is not defined.
     */
    protected String getConfigParam(String key) {
        String result = null;

        if (rpaServices == null) {
            return null;
        }

        try {
            result = rpaServices.getConfigParam(key);
        } catch (Exception e) {
            //do nothing
        }

        return result;
    }

    private void initService() {
        if (service == null) {
            service = new GoogleDriveService(googleServicesProvider.getService(Drive.class, DriveScopes.DRIVE));
            service.setExtraFileFields(getExtraFileFields());
            service.setSupportsTeamDrives(isSupportsTeamDrives());
        }
    }
}
