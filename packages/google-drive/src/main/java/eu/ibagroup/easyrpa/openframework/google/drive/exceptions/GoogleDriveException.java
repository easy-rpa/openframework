package eu.ibagroup.easyrpa.openframework.google.drive.exceptions;

import com.google.api.client.http.AbstractInputStreamContent;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFile;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileId;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileInfo;
import eu.ibagroup.easyrpa.openframework.google.drive.model.GFileType;

import java.io.File;

/**
 * Google Drive runtime exception. Thrown in case of some errors or problems during working with Google Drive service.
 *
 * @see eu.ibagroup.easyrpa.openframework.google.drive.GoogleDrive#createFile(File, GFileId)
 * @see GFile#getBytes()
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#listFiles(String, Integer)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#getFileInfo(String)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#getFileInfo(GFileId)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#getFile(GFileInfo)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#createFile(String, GFileType, AbstractInputStreamContent, GFileId)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#renameFile(GFileInfo, String)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#moveFile(GFileInfo, GFileId)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#updateFile(GFileInfo)
 * @see eu.ibagroup.easyrpa.openframework.google.drive.service.GoogleDriveService#deleteFile(GFileId)
 */
public class GoogleDriveException extends RuntimeException {

    /**
     * Constructs a new Google Drive exception with the specified detail message and
     * cause.
     *
     * @param message the detail message.
     * @param cause   the cause.
     */
    public GoogleDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Google Drive exception with the specified cause.
     *
     * @param cause the cause.
     */
    public GoogleDriveException(Throwable cause) {
        super(cause);
    }
}
