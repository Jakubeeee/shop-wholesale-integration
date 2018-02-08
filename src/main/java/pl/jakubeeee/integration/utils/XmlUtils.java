package pl.jakubeeee.integration.utils;

import lombok.experimental.UtilityClass;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import pl.jakubeeee.integration.services.LoggingService;

import java.util.List;

@UtilityClass
public class XmlUtils {

    @Autowired
    LoggingService loggingService;

    public static Document stringToXml(String string) {
        try {
            return DocumentHelper.parseText(string);
        } catch (DocumentException e) {
            loggingService.error("An error occurred while reading xml file");
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
