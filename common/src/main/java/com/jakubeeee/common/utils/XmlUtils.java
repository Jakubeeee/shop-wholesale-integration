package com.jakubeeee.common.utils;

import lombok.experimental.UtilityClass;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import java.util.List;

@UtilityClass
public class XmlUtils {

    public static Document stringToXml(String string) {
        try {
            return DocumentHelper.parseText(string);
        } catch (DocumentException e) {
            return null;
        }
    }

    @SuppressWarnings(value = "unchecked")
    public static List<Node> nodesToList(Document xml, String xpath) {
        return xml.selectNodes(xpath);
    }

    public static String getTextValue(Node node, String name) {
        return node.selectSingleNode(name).getText();
    }


}
