/** 
 * Copyright 2013 AppDynamics 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.extensions.couchdb;

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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationParser {

	private static final String HOST_TAG = "Host";
	private static final String HOST_ID_ATTRIBUTE = "id";
	private static final String PORT_ATTRIBUTE = "port";
	private static final String USERNAME_ATTRIBUTE = "username";
	private static final String PASSWORD_ATTRIBUTE = "password";

	private static final Logger logger = Logger.getLogger("com.singularity.extensions.ConfigurationParser");

	private String filePath;

	public ConfigurationParser(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Parses the hosts' configuration file
	 * 
	 * @return HashSet Set containing all host configurations
	 */
	public Set<HostConfig> parseHostConfig() throws Exception {
		Set<HostConfig> hostConfigs = new HashSet<HostConfig>();
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
				newHost.hostId = attributes.getNamedItem(HOST_ID_ATTRIBUTE).getNodeValue();
				newHost.port = attributes.getNamedItem(PORT_ATTRIBUTE).getNodeValue();
				newHost.username = attributes.getNamedItem(USERNAME_ATTRIBUTE).getNodeValue();
				newHost.password = attributes.getNamedItem(PASSWORD_ATTRIBUTE).getNodeValue();
				hostConfigs.add(newHost);
			}
		} catch (ParserConfigurationException e) {
			logger.error("Failed to initialize instance of DocumentBuilder");
			throw e;
		} catch (FileNotFoundException e) {
			logger.error("HostConfig file not found, check the path.");
			throw e;
		} catch (SAXException e) {
			logger.error("Unable to parse the file");
			throw e;
		} catch (IOException e) {
			logger.error("IOException", e);
			throw e;
		}
		return hostConfigs;
	}
}
