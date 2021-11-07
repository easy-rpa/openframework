package eu.ibagroup.easyrpa.openframework.email.utils;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailUtils {

    public EmailUtils() {
    }

    public static String detectMimeType(String filename) {
        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(filename.toLowerCase()));
            if (mimeType == null) {
                mimeType = "text/plain";
            }
        } catch (IOException e) {
            mimeType = "text/plain";
        }

        return mimeType;
    }

    public static String htmlToText(String html) {
        return Jsoup.parse(html).text();
    }
}