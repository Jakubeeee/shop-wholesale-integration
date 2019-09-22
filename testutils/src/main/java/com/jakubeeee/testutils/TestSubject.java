package com.jakubeeee.testutils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.Map;

import static java.util.Comparator.comparing;
import static org.dom4j.DocumentHelper.createDocument;

/**
 * Immutable class whose instances are used as value containers during testing.
 */
@Value
@With
@AllArgsConstructor
public class TestSubject implements Comparable<TestSubject> {

    @NonNull
    private final int uniqueId;

    @NonNull
    private final String stringField1;

    @NonNull
    private final String stringField2;

    @NonNull
    private final String stringField3;

    public String asJson() {
        return "{" +
                "\"uniqueId\":" + uniqueId + "," +
                "\"stringField1\":\"" + stringField1 + "\"," +
                "\"stringField2\":\"" + stringField2 + "\"," +
                "\"stringField3\":\"" + stringField3 + "\"" +
                "}";
    }

    public String asXml() {
        return "<testSubject>" +
                "<uniqueId>" + uniqueId + "</uniqueId>" +
                "<stringField1>" + stringField1 + "</stringField1>" +
                "<stringField2>" + stringField2 + "</stringField2>" +
                "<stringField3>" + stringField3 + "</stringField3>" +
                "</testSubject>";
    }

    public Document asDom4jDocument() {
        Document document = createDocument();
        Element rootElement = document.addElement("testSubject");
        rootElement.addElement("uniqueId").addText(String.valueOf(uniqueId));
        rootElement.addElement("stringField1").addText(stringField1);
        rootElement.addElement("stringField2").addText(stringField2);
        rootElement.addElement("stringField3").addText(stringField3);
        return document;
    }

    public Map<String, Object> asMap() {
        return Map.of("uniqueId", uniqueId,
                "stringField1", stringField1,
                "stringField2", stringField2,
                "stringField3", stringField3);
    }

    @Override
    public int compareTo(TestSubject other) {
        return comparing(TestSubject::getStringField1)
                .thenComparing(TestSubject::getStringField2)
                .thenComparing(TestSubject::getStringField3)
                .compare(this, other);
    }

}
