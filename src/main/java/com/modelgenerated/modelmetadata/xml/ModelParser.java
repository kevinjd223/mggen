/*
 * ModelParser.java
 *
 * Created on May 4, 2003, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;

import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.generator.GeneratorException;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.enums.EnumDescriptor;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;
import com.modelgenerated.util.StringUtil;
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
import org.w3c.dom.NodeList;

/**
 *
 *
 * @author  kevind
 */
public class ModelParser {
    private Model model;
    private Element root;
    private String objectDescriptorPath;
    private String externalObjectDescriptorPath;
    private String serviceDescriptorPath;
    private String basePath;

    /** Creates a new instance of ValueObjectDescription */
    public ModelParser() {
    }
    
    
    public Model parse(String modelFileLocation) {
        try {
            model = new Model();
    
            System.out.println("modelFileLocation: " + modelFileLocation);
            URL configURL = new URL(modelFileLocation);          
            InputStream configInputStream = configURL.openStream();

            basePath = StringUtil.getPathFromFilePath(modelFileLocation);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            root = doc.getDocumentElement();
            System.out.println("root nodename: " + root.getNodeName());
            
            objectDescriptorPath = DomUtil.getChildElementText(root, "ObjectDescriptorPath");
            Assert.check(objectDescriptorPath != null, "objectDescriptorPath != null");
            
            externalObjectDescriptorPath = DomUtil.getChildElementText(root, "ExternalObjectDescriptorPath");
            
            serviceDescriptorPath = DomUtil.getChildElementText(root, "ServiceDescriptorPath");
            //Assert.check(screenDescriptorPath != null, "screenDescriptorPath != null");
            
            String packagesToGenerate = DomUtil.getChildElementText(root, "PackagesToGenerate");
            Assert.check(packagesToGenerate != null, "packagesToGenerate != null");
            model.setPackagesToGenerate(packagesToGenerate);
            
            String copyrightNotice = DomUtil.getChildElementText(root, "CopyrightNotice");
            model.setCopyrightNotice(copyrightNotice);
            
            loadObjects();
            loadExternalObjects();
            loadEnums();
            loadServices();
            
            return model;
            
        } catch (MalformedURLException e) {
            throw new GeneratorException("Bad url", e);
        } catch (ParserConfigurationException e) {
            throw new GeneratorException("Error parsing configXML file", e);
        } catch (SAXException e) {
            throw new GeneratorException("Error parsing configXML file", e);
        } catch (IOException e) {
            throw new GeneratorException("Couldn't parse config input stream", e);
        }
    }

    private void loadObjects() {
        ObjectDescriptorParser objectDescriptorParser = new ObjectDescriptorParser();            

        Element fieldsElement = DomUtil.getChildElement(root, "Objects");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("ObjectDescriptor");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("ObjectDescriptor"), "nodeName.equals(ObjectDescriptor)");
            
            String file = DomUtil.getElementText(elem);
            String filePath = getObjectDescriptorPath() + file;
            
            ObjectDescriptor objectDescriptor = objectDescriptorParser.parse(model, filePath);
            
            model.addObject(objectDescriptor);
        }
        
    }

    private void loadExternalObjects() {
        ObjectDescriptorParser objectDescriptorParser = new ObjectDescriptorParser();            

        Element fieldsElement = DomUtil.getChildElement(root, "ExternalObjects");
        if (fieldsElement != null) {
            Assert.check(externalObjectDescriptorPath != null, "externalObjectDescriptorPath != null");

            NodeList fieldsNodeList = fieldsElement.getElementsByTagName("ObjectDescriptor");
            for (int i = 0; i < fieldsNodeList.getLength(); i++) {
                Element elem = (Element)fieldsNodeList.item(i);
                String nodeName = elem.getNodeName();
                Assert.check(nodeName.equals("ObjectDescriptor"), "nodeName.equals(ObjectDescriptor)");
                
                String file = DomUtil.getElementText(elem);
                String filePath = getExternalObjectDescriptorPath() + file;
                
                ObjectDescriptor objectDescriptor = objectDescriptorParser.parse(model, filePath);
                
                model.addExternalObject(objectDescriptor);
            }
        }
        
    }
    
    
    private void loadEnums() {
        EnumParser enumParser = new EnumParser();            

        Element fieldsElement = DomUtil.getChildElement(root, "Enums");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("Enum");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Enum"), "nodeName.equals(Enum)");
            
            String file = DomUtil.getElementText(elem);
            String filePath = getObjectDescriptorPath() + file;
            
            EnumDescriptor enumDescriptor = enumParser.parse(model, filePath);

            model.addEnum(enumDescriptor);
        }
        
    }
    
    private void loadServices() {
        ServiceDescriptorParser serviceDescriptorParser = new ServiceDescriptorParser();            

        Element fieldsElement = DomUtil.getChildElement(root, "Services");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("Service");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Service"), "nodeName.equals(\"Service\")");
            
            String file = DomUtil.getElementText(elem);
            String filePath = getServiceDescriptorPath() + file;
            
            ServiceDescriptor serviceDescriptor = serviceDescriptorParser.parse(model, filePath);
            
            model.addService(serviceDescriptor); 
        }
        
    }
    
    public String getObjectDescriptorPath() {
        return getDescriptorPath(objectDescriptorPath);
    }
    
    public String getExternalObjectDescriptorPath() {
        return getDescriptorPath(externalObjectDescriptorPath);
    }
    
    public String getServiceDescriptorPath() {
        return getDescriptorPath(serviceDescriptorPath);
    }

    public String getDescriptorPath(String descriptor) {
        if (descriptor.startsWith("./")) {
            return basePath + descriptor.substring(1) + "/";
        } else {
            return basePath + descriptor+ "/";
        }
    }
    
}
