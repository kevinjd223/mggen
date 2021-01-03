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
public class GeneratorConfig implements Config, Displayable {
    public final static String CONFIG_NAME = "generatorConfig";
	public final static String ENV_GENERATOR_ROOT = "generator.root";
    private String strutsConfigFile;
    private String tilesDefFile;
    private String resourceFile;
    private String controllerRoot;
    private String jspRoot;
    private String modelLocation; // todo: temporary until model calls screengen
    private String projectPrototypeDirectory;

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
    
    public String getStrutsConfigFile() {
        return strutsConfigFile;        
    }
    public String getTilesDefFile() {
        return tilesDefFile;        
    }
    public String getResourceFile() {
        return resourceFile;        
    }
    public String getControllerRoot() {
        return controllerRoot;        
    }
    public String getJspRoot() {
        return jspRoot;        
    }
    public String getModelLocation() {
        return modelLocation;        
    }
    public String getProjectPrototypeDirectory() {
        return projectPrototypeDirectory;        
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

            strutsConfigFile = DomUtil.getChildElementText(config, "StrutsConfigFile");
            Assert.check(strutsConfigFile != null, "strutsConfigFile != null");
            strutsConfigFile = generatorRoot + strutsConfigFile; 
            
            tilesDefFile = DomUtil.getChildElementText(config, "TilesDefFile");
            Assert.check(tilesDefFile != null, "tilesDefFile != null");
            tilesDefFile = generatorRoot + tilesDefFile; 
            
            resourceFile = DomUtil.getChildElementText(config, "ResourceFile");
            Assert.check(resourceFile != null, "resourceFile != null");
            resourceFile = generatorRoot + resourceFile; 
            
            controllerRoot = DomUtil.getChildElementText(config, "ControllerRoot");
            Assert.check(controllerRoot != null, "controllerRoot != null");
            controllerRoot = generatorRoot + controllerRoot; 
            
            jspRoot = DomUtil.getChildElementText(config, "JspRoot");
            Assert.check(jspRoot != null, "jspRoot != null");
            jspRoot = generatorRoot + jspRoot; 

            modelLocation = DomUtil.getChildElementText(config, "ModelLocation");
            Assert.check(modelLocation != null, "modelLocation != null");
            modelLocation = generatorRoot + modelLocation; 
            
            projectPrototypeDirectory = DomUtil.getChildElementText(config, "ProjectPrototypeDirectory");
            // Assert.check(projectPrototypeDirectory != null, "projectPrototypeDirectory != null");
            projectPrototypeDirectory = generatorRoot + projectPrototypeDirectory; 
            
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
    

    // Displayable implementation
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("DataAccessConfig", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "strutsConfigFile: " + strutsConfigFile);     
        displayBuffer.addLine(level+1, "tilesDefFile: " + tilesDefFile);     
        displayBuffer.addLine(level+1, "resourceFile: " + resourceFile);     
        displayBuffer.addLine(level+1, "controllerRoot: " + controllerRoot);     
        displayBuffer.addLine(level+1, "jspRoot: " + jspRoot);     
        displayBuffer.addLine(level+1, "modelLocation: " + modelLocation);
        displayBuffer.addLine(level+1, "projectPrototypeDirectory: " + projectPrototypeDirectory);     
       
        displayBuffer.addLine(level+1, "javaCodeFolder: " + javaCodeFolder);
        displayBuffer.addLine(level+1, "junitFolder: " + junitFolder);
        displayBuffer.addLine(level+1, "testDataFolder: " + testDataFolder);
        displayBuffer.addLine(level+1, "databaseFolder: " + databaseFolder);
        
        return displayBuffer.toString();
        
    }
    
}
