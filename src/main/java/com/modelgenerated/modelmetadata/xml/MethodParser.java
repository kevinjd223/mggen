/*
 * MethodParser.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;


import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Prototype;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parses the ObjectDescriptor xml element and return a list of methods from either the <Methods> of <ListMethods> element.
 *
 * @author  kevind
 */

public class MethodParser {
    
    public static List<Method> loadMethods(Element root) {
        return loadMethods(root, "Methods", "Method");
    }
    
    public static List<Method> loadListMethods(Element root) {
        return loadMethods(root, "ListMethods", "Method");
    }
     
    private static List<Method> loadMethods(Element root, String methodsTag, String methodTag) {
        List<Method> methodList = new ArrayList<Method>(); 

        Element queriesElement = DomUtil.getChildElement(root, methodsTag);
        if (queriesElement == null) {
            return methodList;
        }
        NodeList queryNodeList = queriesElement.getElementsByTagName(methodTag);
        if (queryNodeList == null) {
            return methodList;
        }
        for (int i = 0; i < queryNodeList.getLength(); i++) {
            Element elem = (Element)queryNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Method"), "nodeName.equals(Method)");

            String description = DomUtil.getChildElementText(elem, "Description");
            String protoTypeString = DomUtil.getChildElementText(elem, "Prototype");
            
            
            PrototypeParser prototypeParser = new PrototypeParser();
            Prototype prototype = prototypeParser.parse(protoTypeString);

            Method method = new Method();
            method.setDescription(description);
            method.setPrototype(prototype);

            methodList.add(method); 
        }     
        
        return methodList;
    }
    

    
}
