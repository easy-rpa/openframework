package eu.ibagroup.easyrpa.openframework.googledrive.utils;

import eu.ibagroup.easyrpa.openframework.googledrive.FileType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GoogleUtils {
    private static final Map<FileType,String> types = new HashMap<>();

    static {
        types.put(FileType.FILE,"text/plain");
        types.put(FileType.DOCUMENT,"text/plain");
        types.put(FileType.SPREADSHEET,"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        types.put(FileType.DRAWING,"image/png");
        types.put(FileType.PRESENTATION,"application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }

    public static ByteArrayOutputStream isToOs(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer;
    }

    public static String getDownloadType(FileType type){
        return types.getOrDefault(type, null);
    }
}
