package com.sas.standalone;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppService {

    private final ConfigurationManager configManager;
    private final FileHandler fileHandler;
    private final String keystorePath;
    private final String keystorePassword;

    public AppService(ConfigurationManager configManager, FileHandler fileHandler, String keystorePath, String keystorePassword) {
        this.configManager = configManager;
        this.fileHandler = fileHandler;
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
    }

    public void modifyServerXml(String filePath) {
        try {
            fileHandler.backupFile(filePath);
            String xmlContent = fileHandler.readFileAsString(filePath);
            Document doc = configManager.createDocumentFromString(xmlContent);

            if (!sslConnectorExists(doc)) {
                Node connector = configManager.createSSLConnector(doc, keystorePath, keystorePassword);
                doc.getDocumentElement().appendChild(connector);
                String modifiedXmlContent = configManager.getStringFromDocument(doc);
                fileHandler.writeStringToFile(modifiedXmlContent, filePath);
                System.out.println("SSL Connector has been added to the server.xml.");
            } else {
                System.out.println("An existing SSL Connector is already present. No changes made.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sslConnectorExists(Document doc) {
        NodeList connectors = doc.getElementsByTagName("Connector");
        if (connectors != null) {
            for (int i = 0; i < connectors.getLength(); i++) {
                Node connector = connectors.item(i);
                if (connector.getAttributes() != null) {
                    Node sslEnabledAttr = connector.getAttributes().getNamedItem("SSLEnabled");
                    if (sslEnabledAttr != null && "true".equals(sslEnabledAttr.getTextContent())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
