package eu.ibagroup.easyrpa.openframework.excel.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePathUtils {

	public static File getFile(String filePath){
		File file = null;
		if(filePath != null && !filePath.trim().isEmpty()){
			filePath = normalizeFilePath(filePath);
			try {
				file = new File(FilePathUtils.class.getResource(filePath.startsWith("/") ? filePath : "/" + filePath).toURI());
			} catch (Exception e) {
				file = new File(filePath);
			}
		}
		return file;
	}

    public static String normalizeFilePath(String path) {
        if (path.contains("%")) {
            path = FilenameUtils.separatorsToSystem(path);
            Matcher matcher = Pattern.compile("%\\w+%").matcher(path);
            while (matcher.find()) {
                String var = matcher.group();
                path = path.replaceAll(var, FilenameUtils.separatorsToSystem(System.getenv(var.replaceAll("%", ""))));
            }
        }
        return FilenameUtils.separatorsToSystem(path);
    }
}
