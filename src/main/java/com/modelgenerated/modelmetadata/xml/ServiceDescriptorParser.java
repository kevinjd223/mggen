/*
 * ScreenParser.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;


import com.modelgenerated.generator.GeneratorException;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.service.EjbVersionEnum;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.modelmetadata.service.CrudObject;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This object parses ScreenDescriptor from xml files.
 *
 * It parses an XML file that looks likes this.
 * 
 *
 *
 * @author  kevind
 */

public class ServiceDescriptorParser {
    String serviceDescriptorFile;
    ServiceDescriptor serviceDescriptor;
    Element root;

    
    /** Creates a new instance of ValueObjectDescription */
    public ServiceDescriptorParser() {
    }
    
    
    public ServiceDescriptor parse(Model model, String initServiceDescriptorFile) {
        try {
            serviceDescriptorFile = initServiceDescriptorFile;
            
            System.out.println("serviceDescriptorFile: " + serviceDescriptorFile);
            URL configURL = new URL(serviceDescriptorFile);          
            InputStream configInputStream = configURL.openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            root = doc.getDocumentElement();
            System.out.println("nodename: " + root.getNodeName());
            
            serviceDescriptor = new ServiceDescriptor(model);
            
            String ejbName = DomUtil.getChildElementText(root, "EjbName");
            Assert.check(ejbName != null, "ejbName != null");
            serviceDescriptor.setEjbName(ejbName);

            String homeName = DomUtil.getChildElementText(root, "HomeName");
            // Assert.check(homeName != null, "homeName != null");
            serviceDescriptor.setHomeName(homeName);

            String remoteName = DomUtil.getChildElementText(root, "RemoteName");
            Assert.check(remoteName != null, "remoteName != null");
            serviceDescriptor.setRemoteName(remoteName);

            String ejbClassName = DomUtil.getChildElementText(root, "EjbClassName");
            Assert.check(ejbClassName != null, "ejbClassName != null");
            serviceDescriptor.setEjbClassName(ejbClassName);

            String description = DomUtil.getChildElementText(root, "Description");
            serviceDescriptor.setDescription(description);

            String ejbVersion = DomUtil.getChildElementText(root, "EjbVersion");
            serviceDescriptor.setEjbVersion(EjbVersionEnum.getEjbVersionEnum(ejbVersion));
            
            loadCrudObjects();

            serviceDescriptor.setMethods(MethodParser.loadMethods(root));
            
            return serviceDescriptor;
            
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

    private void loadCrudObjects() {
        ArrayList<CrudObject> objects = new ArrayList<CrudObject>();
        serviceDescriptor.setCrudObjects(objects);
        
        Element fieldsElement = DomUtil.getChildElement(root, "CrudObjects");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("CrudObject");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("CrudObject"), "nodeName.equals(\"CrudObject\")");


            String objectClass = DomUtil.getChildElementText(elem, "ObjectClass");
            String generateListMethods = DomUtil.getChildElementText(elem, "GenerateListMethods");
            String generateSearchMethod = DomUtil.getChildElementText(elem, "GenerateSearchMethod");
            String findByIdExceptions = DomUtil.getChildElementText(elem, "FindByIdExceptions");
            String saveExceptions = DomUtil.getChildElementText(elem, "SaveExceptions");

            CrudObject crudObject = new CrudObject();            
            
            crudObject.setName(objectClass);
            crudObject.setGenerateListMethods(convertToBooleanValue(generateListMethods, false));
            crudObject.setGenerateSearchMethod(convertToBooleanValue(generateSearchMethod, false));
            crudObject.setFindByIdExceptions(findByIdExceptions);
            crudObject.setSaveExceptions(saveExceptions);
            
            objects.add(crudObject); 
        }
        
     }
     
     protected boolean convertToBooleanValue(String value, boolean defaultValue) {
         if (value == null) {
             return defaultValue;
         }
         if ("true".equals(value.toLowerCase().trim()) || "yes".equals(value.toLowerCase().trim())) {
             return true;
         } else {
             return false;             
         }  
     }
    
}
