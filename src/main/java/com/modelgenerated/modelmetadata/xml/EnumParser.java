/*
 * EnumParser.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;

import com.modelgenerated.generator.GeneratorException;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.enums.EnumDescriptor;
import com.modelgenerated.modelmetadata.enums.EnumValueDescriptor;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
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
public class EnumParser {
    protected EnumDescriptor enumDescriptor;
    protected String valueObjectInterface;

    /** Creates a new instance of ValueObjectDescription */
    public EnumParser() {
    }
    
    
    public EnumDescriptor parse(Model model, String enumDescriptorFileLocation) {
        try {
            enumDescriptor = new EnumDescriptor(model);
    
            System.out.println("objectDescriptorFileLocation: " + enumDescriptorFileLocation);
            URL configURL = new URL(enumDescriptorFileLocation);          
            InputStream configInputStream = configURL.openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            Element root = doc.getDocumentElement();
            System.out.println("nodename: " + root.getNodeName());
            
            String implementation = DomUtil.getChildElementText(root, "Implementation");
            Assert.check(implementation != null, "implementation != null");
            
            if (implementation != null) {
                enumDescriptor.setImplementation(new ClassDescriptor(implementation));
            }            

            String description = DomUtil.getChildElementText(root, "Description");
            enumDescriptor.setDescription(description);

            loadValues(root, enumDescriptorFileLocation);
            
            return enumDescriptor;
            
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

    
    private void loadValues(Element root, String objectDescriptorFileLocation) {
        final String ENUMVALUES = "EnumValues";
        final String ENUMVALUE = "EnumValue";
        
        
        List<EnumValueDescriptor> valuesList = new ArrayList<EnumValueDescriptor>(); 
        enumDescriptor.setValues(valuesList);

        Element queriesElement = DomUtil.getChildElement(root, ENUMVALUES);
        if (queriesElement == null) {
            return;
        }
        NodeList queryNodeList = queriesElement.getElementsByTagName(ENUMVALUE);
        if (queryNodeList == null) {
            return;
        }
        for (int i = 0; i < queryNodeList.getLength(); i++) {
            Element elem = (Element)queryNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals(ENUMVALUE), "nodeName.equals(ENUMVALUE)");

            String key = DomUtil.getChildElementText(elem, "Key"); 
            String description = DomUtil.getChildElementText(elem, "Display");

            EnumValueDescriptor enumValueDescriptor = new EnumValueDescriptor(enumDescriptor);
            enumValueDescriptor.setKey(key);
            enumValueDescriptor.setDescription(description);

            valuesList.add(enumValueDescriptor); 
        }
    }
    

    
}
