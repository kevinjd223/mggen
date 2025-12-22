/*
 * GeneratorConfig.java
 *
 * Created on November 2, 2002, 9:57 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

import com.modelgenerated.foundation.config.Config;
import com.modelgenerated.foundation.config.ConfigNotFoundException;
import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author  kevind
 */
public class GeneratorConfig implements Config {
    public final static String CONFIG_NAME = "generatorConfig";
	public final static String ENV_GENERATOR_ROOT = "generator.root";
    private String modelLocation;

    private String javaCodeFolder;
    private String junitFolder;
    private String testDataFolder;
    private String databaseFolder;
    private String objectFieldSizePath;
    private boolean generateDAOTests = false;

    /** Creates a new instance of DataAccessConfig */
    public GeneratorConfig() {
    }
    
    public String getName() {
        return CONFIG_NAME;
    }
    
    public String getModelLocation() {
        return modelLocation;        
    }

    public String getJavaCodeFolder() {
        return javaCodeFolder;        
    }
    
    public String getJunitFolder() {
        return junitFolder;        
    }
    
    public String getTestDataFolder() {
        return testDataFolder;        
    }
    
    public String getDatabaseFolder() {
        return databaseFolder;        
    }

    public String getObjectFieldSizePath() {
        return objectFieldSizePath;
    }
    
    
    public boolean getGenerateDAOTests() {
		return generateDAOTests;
	}

	public void load(InputStream configInputStream) {
        try {
            String generatorRoot = System.getProperty(ENV_GENERATOR_ROOT);
            if (generatorRoot == null) {
            	generatorRoot = "c:/workspaces/modelgenerated/modelgenerated";
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            Element root = doc.getDocumentElement();
            System.out.println("nodename: " + root.getNodeName());
            Element config = DomUtil.getChildElement(root, "ConfigData");
            Assert.check(config != null, "config != null");

            modelLocation = DomUtil.getChildElementText(config, "ModelLocation");
            Assert.check(modelLocation != null, "modelLocation != null");
            modelLocation = generatorRoot + modelLocation; 
            
            javaCodeFolder = DomUtil.getChildElementText(config, "JavaCodeFolder");
            Assert.check(javaCodeFolder != null, "javaCodeFolder != null");
            javaCodeFolder = generatorRoot + javaCodeFolder; 
    
            junitFolder = DomUtil.getChildElementText(config, "JunitFolder");
            Assert.check(junitFolder != null, "junitFolder != null");
            junitFolder = generatorRoot + junitFolder; 
    
            testDataFolder = DomUtil.getChildElementText(config, "TestDataFolder");
            Assert.check(testDataFolder != null, "testDataFolder != null");
            testDataFolder = generatorRoot + testDataFolder; 
    
            databaseFolder = DomUtil.getChildElementText(config, "DatabaseFolder");
            Assert.check(databaseFolder != null, "databaseFolder != null");
            databaseFolder = generatorRoot + databaseFolder; 

            objectFieldSizePath = DomUtil.getChildElementText(config, "ObjectFieldSizePath");
            Assert.check(objectFieldSizePath != null, "objectFieldSizePath != null");
            objectFieldSizePath = generatorRoot + objectFieldSizePath; 

            String generateDAOTestsString = DomUtil.getChildElementText(config, "GenerateDAOTests");
            if ("true".equals(generateDAOTestsString)) {
                generateDAOTests = true; 
            }


        } catch (MalformedURLException e) {
            throw new ConfigNotFoundException("Bad url", e);
        } catch (ParserConfigurationException e) {
            throw new ConfigNotFoundException("Error parsing configXML file", e);
        } catch (SAXException e) {
            throw new ConfigNotFoundException("Error parsing configXML file", e);
        } catch (IOException e) {
            throw new ConfigNotFoundException("Couldn't parse config input stream", e);
        }
    }    
    

    
}
