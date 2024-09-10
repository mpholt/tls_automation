package com.sas.standalone;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.sas.standalone.ConfigurationManager;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationManagerTest {

    private ConfigurationManager configManager = new ConfigurationManager();

    @Test
    void testCreateDocumentFromString() throws Exception {
        String xmlInput = "<root><test>123</test></root>";
        Document doc = configManager.createDocumentFromString(xmlInput);
        assertNotNull(doc);
        assertEquals("123", doc.getElementsByTagName("test").item(0).getTextContent());
    }

    @Test
    void testGetStringFromDocument() throws Exception {
        String xmlInput = "<root><test>ABC</test></root>";
        Document doc = configManager.createDocumentFromString(xmlInput);
        String xmlOutput = configManager.getStringFromDocument(doc);
        assertTrue(xmlOutput.contains("ABC"));
    }
}
