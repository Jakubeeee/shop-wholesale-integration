package com.jakubeeee.common;

import com.jakubeeee.testutils.marker.BehavioralUnitTestCategory;
import org.dom4j.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Optional;

import static com.jakubeeee.common.XmlUtils.stringToXml;
import static com.jakubeeee.testutils.TestSubjectFactory.getTestSubject;
import static org.dom4j.DocumentHelper.createDocument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Category(BehavioralUnitTestCategory.class)
public class XmlUtilsTest {

    private static String TEST_XML;

    private static Document TEST_DOM4J_DOCUMENT;

    @BeforeClass
    public static void setUp() {
        TEST_XML = getTestSubject(1).asXml();
        TEST_DOM4J_DOCUMENT = getTestSubject(1).asDom4jDocument();
    }

    @Test
    public void stringToXmlTest_shouldConvert() {
        Optional<Document> resultO = stringToXml(TEST_XML);
        assertThat(resultO.orElse(createDocument()).asXML(), is(equalTo(TEST_DOM4J_DOCUMENT.asXML())));
    }

}
