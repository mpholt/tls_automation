package com.sas.standalone;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.StringReader;
import org.xml.sax.InputSource;

public class ConfigurationManager {

    public Document createDocumentFromString(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlContent)));
    }

    public String getStringFromDocument(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public Node createSSLConnector(Document doc, String keystorePath, String keystorePassword) {
        Element connector = doc.createElement("Connector");
        connector.setAttribute("port", "8443");
        connector.setAttribute("protocol", "org.apache.coyote.http11.Http11NioProtocol");
        connector.setAttribute("maxThreads", "150");
        connector.setAttribute("SSLEnabled", "true");

        Element upgradeProtocol = doc.createElement("UpgradeProtocol");
        upgradeProtocol.setAttribute("className", "org.apache.coyote.http2.Http2Protocol");
        
        Element sslHostConfig = doc.createElement("SSLHostConfig");
        
        Element certificate = doc.createElement("Certificate");
        certificate.setAttribute("certificateKeystoreFile", keystorePath);
        certificate.setAttribute("certificateKeystorePassword", keystorePassword);
        certificate.setAttribute("type", "RSA");

        sslHostConfig.appendChild(certificate);
        connector.appendChild(upgradeProtocol);
        connector.appendChild(sslHostConfig);

        return connector;
    }
}
