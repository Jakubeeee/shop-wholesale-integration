package com.jakubeeee.common;

import lombok.experimental.UtilityClass;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.util.Optional;

import static org.dom4j.DocumentHelper.*;

/**
 * Utility class providing useful static methods related to parsing and processing of xml files.
 */
@UtilityClass
public final class XmlUtils {

    public static Optional<Document> stringToXml(String string) {
        Document document;
        try {
            document = parseText(string);
        } catch (DocumentException e) {
            document = null;
        }
        return Optional.ofNullable(document);
    }

}
