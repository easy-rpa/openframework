package eu.ibagroup.easyrpa.openframework.email.utils;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
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

    public static byte[] readAllBytes(InputStream source) throws IOException {
        try {
            return IOUtils.toByteArray(source);
        } catch (IOException e) {
            throw e;
        }
    }
}