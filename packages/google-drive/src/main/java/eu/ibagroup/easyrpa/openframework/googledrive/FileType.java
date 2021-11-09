package eu.ibagroup.easyrpa.openframework.googledrive;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FileType {
    AUDIO, DOCUMENT, DRAWING, FILE, FOLDER, FORM, FUSIONTABLE, MAP, PHOTO, PRESENTATION,
    SCRIPT, SITE, SPREADSHEET, UNKNOWN, VIDEO;

    private static final String DEFAULT_MIME_TYPE = "application/vnd.google-apps.";

    public static  String toString(FileType type){
        return DEFAULT_MIME_TYPE+type.toString().toLowerCase(Locale.ROOT);
    }

    public static FileType getValue(String fullFileType){
        final String regex = "[.](.*?)$";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(fullFileType);
        matcher.find();
        matcher = pattern.matcher(matcher.group(1));
        matcher.find();
        return valueOf(matcher.group(1).toUpperCase(Locale.ROOT));
    }
}
