package com.sas.embedded;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Note: This is a pseudo-code, non-fully integrated version used to demonstrate using the
 *       Tomcat API
 */
public class Embedded {
    public static void main(String[] args) {
        String filePath = "/Users/mpholt/development/interviews/sas/apache-tomcat-11.0.0-M24/conf/server.xml";
        backupXmlDocument(filePath); // Backup the original XML file
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            // Initialize the XML parser
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            if (!sslConnectorExists(doc)) {
                // Create an SSL Connector node
                Node connector = createSSLConnector(doc);

                // Get the Service node (usually "Catalina")
                Node service = doc.getElementsByTagName("Service").item(0);

                // Append the SSL Connector node to the Service node
                service.appendChild(connector);

                // Save the modified XML to the same file or a new file
                saveXmlDocument(doc, filePath);
            } else {
                System.out.println("An SSL Connector already exists. No modifications were made.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean sslConnectorExists(Document doc) {
        NodeList connectors = doc.getElementsByTagName("Connector");
        for (int i = 0; i < connectors.getLength(); i++) {
            Node connector = connectors.item(i);
            if (connector.getAttributes().getNamedItem("SSLEnabled") != null &&
                    "true".equals(connector.getAttributes().getNamedItem("SSLEnabled").getTextContent())) {
                return true; // SSL Connector found
            }
        }
        return false; // No SSL Connector found
    }

    private static Node createSSLConnector(Document doc) {
        Element connector = doc.createElement("Connector");
        connector.setAttribute("port", "8443");
        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11NioProtocol");
        connector.setAttribute("maxThreads", "150");
        connector.setAttribute("SSLEnabled", "true");

        // Create UpgradeProtocol subelement for HTTP/2 support
        Element upgradeProtocol = doc.createElement("UpgradeProtocol");
        upgradeProtocol.setAttribute("className", "org.apache.coyote.http2.Http2Protocol");

        // Create SSLHostConfig subelement
        Element sslHostConfig = doc.createElement("SSLHostConfig");

        // Create Certificate subelement
        Element certificate = doc.createElement("Certificate");
        certificate.setAttribute("certificateKeystoreFile", "/Users/mpholt/development/interviews/sas/my_keystore");
        certificate.setAttribute("certificateKeystorePassword", "changeit");
        certificate.setAttribute("type", "RSA");

        // Assemble the structure
        sslHostConfig.appendChild(certificate);
        connector.appendChild(upgradeProtocol);
        connector.appendChild(sslHostConfig);

        return connector;
    }

    private static void backupXmlDocument(String filePath) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String backupFilePath = filePath + "." + sdf.format(new Date()) + ".bak";
        try {
            Files.copy(Paths.get(filePath), Paths.get(backupFilePath));
            System.out.println("Backup created: " + backupFilePath);
        } catch (IOException e) {
            System.out.println("Failed to create backup");
            e.printStackTrace();
        }
    }

    private static void saveXmlDocument(Document doc, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
        System.out.println("Configuration file updated successfully.");
    }
}
