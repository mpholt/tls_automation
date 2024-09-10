package com.sas.standalone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;

import com.sas.standalone.AppService;
import com.sas.standalone.ConfigurationManager;
import com.sas.standalone.FileHandler;

import javax.xml.xpath.*; 
import org.w3c.dom.*;

class AppServiceTest {

    private ConfigurationManager configManager = mock(ConfigurationManager.class);
    private FileHandler fileHandler = mock(FileHandler.class);
    private AppService appService = new AppService(configManager, fileHandler, "path/to/keystore", "password");

    @Test
    void testModifyServerXmlWithExistingConnector() throws Exception {
        // Mock XML content with an existing SSL Connector
        String xmlContentWithConnector = "<Server><Connector SSLEnabled=\"true\"/></Server>";
        Document mockDoc = mock(Document.class);
        NodeList mockNodeList = mock(NodeList.class);
        Node mockConnectorNode = mock(Node.class);
        NamedNodeMap mockAttributes = mock(NamedNodeMap.class);

        when(fileHandler.readFileAsString(anyString())).thenReturn(xmlContentWithConnector);
        when(configManager.createDocumentFromString(anyString())).thenReturn(mockDoc);
        when(mockDoc.getElementsByTagName("Connector")).thenReturn(mockNodeList);
        when(mockNodeList.getLength()).thenReturn(1);
        when(mockNodeList.item(0)).thenReturn(mockConnectorNode);
        when(mockConnectorNode.getAttributes()).thenReturn(mockAttributes);
        when(mockAttributes.getNamedItem("SSLEnabled")).thenReturn(mock(Node.class));
        when(mockAttributes.getNamedItem("SSLEnabled").getTextContent()).thenReturn("true");

        // Test the method
        appService.modifyServerXml("server.xml");

        // Verify that no new connector is created and no file is written
        verify(fileHandler, never()).writeStringToFile(anyString(), anyString());
        verify(configManager, never()).createSSLConnector(any(Document.class), anyString(), anyString());
    }
}