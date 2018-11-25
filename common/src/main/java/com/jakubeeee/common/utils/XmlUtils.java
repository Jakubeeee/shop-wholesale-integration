package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class XmlUtils {

    public static Optional<Document> stringToXml(String string) {
        Document document;
        try {
            document = DocumentHelper.parseText(string);
        } catch (DocumentException e) {
            document = null;
        }
        return Optional.ofNullable(document);
    }

    @SuppressWarnings(value = "unchecked")
    public static List<Node> toNodeList(Document xml, String xpath) {
        return xml.selectNodes(xpath);
    }

    public static String getTextValue(Node node, String name) {
        return node.selectSingleNode(name).getText();
    }

}
