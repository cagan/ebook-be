package com.cagan.library.integration.s3;

import com.cagan.library.web.errors.BookBucketException;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@UtilityClass
public final class ObjectLocatorUtils {

    public static String createLocator(String bucketName, String extension) {
        String datePattern = "yyyy-MM-dd-HH-mm-ss";
        SimpleDateFormat format = new SimpleDateFormat(datePattern);
        String fileName = String.format("%s-%s", format.format(new Date()), UUID.randomUUID());
        return String.format("%s/%s.%s", bucketName, fileName, extension);
    }

    public static ObjectLocator getObjectLocator(String locator) {
        String[] locatorParts = locator.split("/", 2);
        if (locatorParts.length == 2) {
            return new ObjectLocator(locatorParts[0], locatorParts[1]);
        } else {
            throw new BookBucketException("Invalid object locator string");
        }
    }
}
