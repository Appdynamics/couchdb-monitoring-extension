package com.appdynamics.monitors.couchdb;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

public class ConfigurationParser {

    private static final String HOST_TAG = "Host";
    private static final String HOST_ID_ATTRIBUTE = "id";
    private static final String PORT_ATTRIBUTE = "port";
    private static final String USERNAME_ATTRIBUTE = "username";
    private static final String PASSWORD_ATTRIBUTE = "password";

    private static final Logger logger = Logger.getLogger(ConfigurationParser.class.getSimpleName());

    private String filePath;

    public ConfigurationParser(String filePath) {
        this.filePath = filePath;
    }

    public HashSet<HostConfig> parseHostConfig() throws Exception{
        HashSet<HostConfig> hostConfigs = new HashSet<HostConfig>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new FileInputStream(filePath));
            NodeList hostNodes = doc.getElementsByTagName(HOST_TAG);

            for (int i = 0; i < hostNodes.getLength(); i++) {
                Node host = hostNodes.item(i);
                NamedNodeMap attributes = host.getAttributes();
                HostConfig newHost = new HostConfig();
                newHost.hostId = attributes.getNamedItem(HOST_ID_ATTRIBUTE).toString();
                newHost.port = attributes.getNamedItem(PORT_ATTRIBUTE).toString();
                newHost.username = attributes.getNamedItem(USERNAME_ATTRIBUTE).toString();
                newHost.password = attributes.getNamedItem(PASSWORD_ATTRIBUTE).toString();
                hostConfigs.add(newHost);
            }
        } catch(ParserConfigurationException e) {
            logger.error("Failed to initialize instance of DocumentBuilder");
            throw e;
        } catch(FileNotFoundException e) {
            logger.error("HostConfig file not found, check the path.");
            throw e;
        } catch(SAXException e) {
            logger.error("Unable to parse the file");
            throw e;
        }
        return hostConfigs;
    }
}
