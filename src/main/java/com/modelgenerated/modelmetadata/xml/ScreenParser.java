/*
 * ScreenParser.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.xml;


import com.modelgenerated.generator.GeneratorException;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.uimodel.ChoiceDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenColumnDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenFieldDescriptor;
import com.modelgenerated.modelmetadata.uimodel.SelectData;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.DomUtil;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
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
 * This object parses ScreenDescriptor from xml files.
 *
 * It parses an XML file that looks likes this.
 * 
 *
 *
 * @author  kevind
 */

public class ScreenParser {
    String screenDescriptorFile;
    ScreenDescriptor screenDescriptor;
    Element root;

    
    /** Creates a new instance of ValueObjectDescription */
    public ScreenParser() {
    }
    
    
    public ScreenDescriptor parse(Model model, String screenDescriptorFileLocation, boolean shouldGenerate) {
        try {
            screenDescriptorFile = screenDescriptorFileLocation;
            
            System.out.println("screenDescriptorFileLocation: " + screenDescriptorFileLocation);
            URL configURL = new URL(screenDescriptorFileLocation);          
            InputStream configInputStream = configURL.openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();                       
            Document doc = db.parse(configInputStream);

            root = doc.getDocumentElement();
            System.out.println("nodename: " + root.getNodeName());
            
            screenDescriptor = new ScreenDescriptor();
            screenDescriptor.setModel(model);
            screenDescriptor.setShouldGenerate(shouldGenerate);
            
            String actionName = DomUtil.getChildElementText(root, "ActionName");
            Assert.check(actionName != null, "actionName != null");
            screenDescriptor.setActionName(actionName);

            String screenType = DomUtil.getChildElementText(root, "ScreenType");
            Assert.check(screenType != null, "screenType != null");
            screenDescriptor.setScreenType(screenType);

            // <FormData>formData</FormData>
            String formData = DomUtil.getChildElementText(root, "FormData");
            Assert.check(formData != null, "formData != null");
            screenDescriptor.setFormDataName(formData);

            // <Folder>folder</Folder>
            String folder = DomUtil.getChildElementText(root, "Folder");
            Assert.check(folder != null, "folder != null");
            screenDescriptor.setFolder(folder);

            // <Controller>controller</Controller>
            String controller = DomUtil.getChildElementText(root, "Controller");
            Assert.check(controller != null, "controller != null");
            screenDescriptor.setControllerClass(controller);

            // <ControllerBaseClass>controller</ControllerBaseClass>
            String controllerBaseClass = DomUtil.getChildElementText(root, "ControllerBaseClass");
            Assert.check(controllerBaseClass != null, "controllerBaseClass != null");
            screenDescriptor.setControllerBaseClass(controllerBaseClass);

            // <TilesDefName>tilesDefName</TilesDefName>
            String tilesDefName = DomUtil.getChildElementText(root, "TilesDefName");
            Assert.check(tilesDefName != null, "tilesDefName != null");
            screenDescriptor.setTilesDefName(tilesDefName);

            String tilesDefBase = DomUtil.getChildElementText(root, "TilesDefBase");
            Assert.check(tilesDefBase != null, "tilesDefBase != null");
            screenDescriptor.setTilesDefBase(tilesDefBase);

            // <ResourcePrefix>resourcePrefix</ResourcePrefix>
            String resourcePrefix = DomUtil.getChildElementText(root, "ResourcePrefix");
            Assert.check(resourcePrefix != null, "resourcePrefix != null");
            screenDescriptor.setResourcePrefix(resourcePrefix);

            // <JspAdd>jspAdd</jspAdd>
            String jspAdd = DomUtil.getChildElementText(root, "JspAdd");
            //Assert.check(jspAdd != null, "jspAdd != null");
            screenDescriptor.setJspAddFile(jspAdd);

            // <JspSearch>jspSearch</jspSearch>
            String jspSearch = DomUtil.getChildElementText(root, "JspSearch");
            //Assert.check(jspSearch != null, "jspSearch != null");
            screenDescriptor.setJspSearchFile(jspSearch);

            // <JspView>jspView</jspView>
            String jspView = DomUtil.getChildElementText(root, "JspView");
            //Assert.check(jspView != null, "jspView != null");
            screenDescriptor.setJspViewFile(jspView);

            // <JspUpdate>jspUpdate</jspUpdate>
            String jspUpdate = DomUtil.getChildElementText(root, "JspUpdate");
            //Assert.check(jspUpdate != null, "jspUpdate != null");
            screenDescriptor.setJspUpdateFile(jspUpdate);

            // <MenuName>menuName</MenuName>
            String menuName = DomUtil.getChildElementText(root, "MenuName");
            //Assert.check(menuName != null, "menuName != null");
            screenDescriptor.setMenuName(menuName);

            // <SuccessLink>jobupdatebilling.do?method=viewUpdate</SuccessLink>
            String successLink = DomUtil.getChildElementText(root, "SuccessLink");
            //Assert.check(successLink != null, "successLink != null");
            screenDescriptor.setSuccessLink(successLink);

            // <CloseLink>jobupdatebilling.do?method=viewUpdate</CloseLink>
            String closeLink = DomUtil.getChildElementText(root, "CloseLink");
            //Assert.check(closeLink != null, "closeLink != null");
            screenDescriptor.setCloseLink(closeLink);
            
            // <ValueObject>valueObject</ValueObject>
            String valueObjectClass = DomUtil.getChildElementText(root, "ValueObjectClass");
            Assert.check(valueObjectClass != null, "valueObjectClass != null");
            screenDescriptor.setValueObjectClass(valueObjectClass);

            // <Service>service</Service>
            String service = DomUtil.getChildElementText(root, "Service");
            Assert.check(service != null, "service != null");
            screenDescriptor.setService(service);

            // <FindMethod>findMethod</FindMethod>
            String findMethod = DomUtil.getChildElementText(root, "FindMethod");
            Assert.check(findMethod != null, "findMethod != null");
            screenDescriptor.setFindMethod(findMethod);

            // <SaveMethod>saveMethod</SaveMethod>
            String saveMethod = DomUtil.getChildElementText(root, "SaveMethod");
            Assert.check(saveMethod != null, "saveMethod != null");
            screenDescriptor.setSaveMethod(saveMethod);

            // <NewMethod>newMethod</NewMethod>
            String newMethod = DomUtil.getChildElementText(root, "NewMethod");
            Assert.check(newMethod != null, "newMethod != null");
            screenDescriptor.setNewMethod(newMethod);

            // <NewMethod>newMethod</NewMethod>
            String searchMethod = DomUtil.getChildElementText(root, "SearchMethod");
            screenDescriptor.setSearchMethod(searchMethod);


            String parentAttribute = DomUtil.getChildElementText(root, "ParentAttribute");
            screenDescriptor.setParentAttribute(parentAttribute);

            String parentService = DomUtil.getChildElementText(root, "ParentService");
            screenDescriptor.setParentService(parentService);

            String parentFindMethod = DomUtil.getChildElementText(root, "ParentFindMethod");
            screenDescriptor.setParentFindMethod(parentFindMethod);

            String parentObjectId = DomUtil.getChildElementText(root, "ParentObjectId");
            screenDescriptor.setParentObjectId(parentObjectId);
            
            loadFields();
            
            return screenDescriptor;
            
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

    private void loadFields() {

        ArrayList fields = new ArrayList();
        Element fieldsElement = DomUtil.getChildElement(root, "Fields");
        NodeList fieldsNodeList = fieldsElement.getElementsByTagName("Field");
        for (int i = 0; i < fieldsNodeList.getLength(); i++) {
            Element elem = (Element)fieldsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Field"), "nodeName.equals(Field)");


            String fieldName = DomUtil.getChildElementText(elem, "FieldName"); 
            String promptValue = DomUtil.getChildElementText(elem, "Prompt"); 
            String type = DomUtil.getChildElementText(elem, "Type");
            String lookupName = DomUtil.getChildElementText(elem, "LookupName");
            String lookupVariable = DomUtil.getChildElementText(elem, "LookupVariable");
            String searchCriteriaClass = DomUtil.getChildElementText(elem, "SearchCriteriaClass");
            String searchCriteriaConstant = DomUtil.getChildElementText(elem, "SearchCriteriaConstant");
            String searchMethod = DomUtil.getChildElementText(elem, "SearchMethod");
            String link = DomUtil.getChildElementText(elem, "Link");
            String drawPrompt = DomUtil.getChildElementText(elem, "DrawPrompt");
            String service = DomUtil.getChildElementText(elem, "Service");
            String listMethod = DomUtil.getChildElementText(elem, "ListMethod");
            String objectClass = DomUtil.getChildElementText(elem, "ObjectClass");
            String subObjectDisplayField = DomUtil.getChildElementText(elem, "SubObjectDisplayField");
            String supportsUpdateString = DomUtil.getChildElementText(elem, "SupportsUpdate");
            String enumClass = DomUtil.getChildElementText(elem, "EnumClass");
            boolean supportsUpdate = true; // default to true.
            if (supportsUpdateString != null) {
                if ("NO".equals(supportsUpdateString.toUpperCase()) || "FALSE".equals(supportsUpdateString.toUpperCase())) {
                    supportsUpdate = false;
                }
            }
 
            ScreenFieldDescriptor screenFieldDescriptor = new ScreenFieldDescriptor();
            screenFieldDescriptor.setFieldName(fieldName);
            screenFieldDescriptor.setPromptValue(promptValue);
            screenFieldDescriptor.setType(type);
            screenFieldDescriptor.setLookupName(lookupName);
            screenFieldDescriptor.setLookupVariable(lookupVariable);
            screenFieldDescriptor.setSearchCriteriaClass(searchCriteriaClass);
            screenFieldDescriptor.setSearchCriteriaConstant(searchCriteriaConstant);
            screenFieldDescriptor.setSearchMethod(searchMethod);
            screenFieldDescriptor.setLink(link);
            screenFieldDescriptor.setDrawPrompt(!("no".equals(drawPrompt)));
            screenFieldDescriptor.setService(service);
            screenFieldDescriptor.setListMethod(listMethod);
            screenFieldDescriptor.setObjectClass(objectClass);
            screenFieldDescriptor.setSubObjectDisplayField(subObjectDisplayField);
            screenFieldDescriptor.setEnumClass(enumClass);
            screenFieldDescriptor.setSupportsUpdate(supportsUpdate);
            
            /*            
            Element choicesElement = DomUtil.getChildElement(elem, "Choices");
            if (choicesElement != null) {
                NodeList choiceNodeList = choicesElement.getElementsByTagName("Field");
                for (int i = 0; i < choiceNodeList.getLength(); i++) {
                    
                    
                }
            }
            */
            // TODO: Use an enum for ScreenFieldTypes
            if ("List".equals(type)) {
                screenFieldDescriptor.setColumns(loadColumns(screenFieldDescriptor, elem));
            }

            if ("Select".equals(type)) {
                screenFieldDescriptor.setSelectData(loadSelectData(screenFieldDescriptor, elem));
            }


            screenFieldDescriptor.setChoices(loadChoices(elem));
            
            
            fields.add(screenFieldDescriptor); 
            //ject previous = fields.put(name, screenFieldDescriptor); 
            //sert.check(previous == null, "Duplicate field \"" + name + "\" found in " + screenDescriptorFile);

        }
        
        screenDescriptor.setFieldList(fields);
     }

    
    private List loadColumns(ScreenFieldDescriptor parent, Element fieldElement) {

        ArrayList columns = new ArrayList();
        Element columnsElement = DomUtil.getChildElement(fieldElement, "Columns");
        NodeList columnsNodeList = columnsElement.getElementsByTagName("Column");
        for (int i = 0; i < columnsNodeList.getLength(); i++) {
            Element elem = (Element)columnsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Column"), "nodeName.equals(Column)");


            String type = DomUtil.getChildElementText(elem, "Type"); 
            String fieldName = DomUtil.getChildElementText(elem, "FieldName"); 
            String lookupName = DomUtil.getChildElementText(elem, "LookupName");
            String lookupVariable = DomUtil.getChildElementText(elem, "LookupVariable");
            String link = DomUtil.getChildElementText(elem, "Link");
            String heading = DomUtil.getChildElementText(elem, "Heading");
            String supportsUpdateString = DomUtil.getChildElementText(elem, "SupportsUpdate");
            boolean supportsUpdate = false; // default to false.
            if (supportsUpdateString != null) {
                if ("YES".equals(supportsUpdateString.toUpperCase()) || "TRUE".equals(supportsUpdateString.toUpperCase())) {
                    supportsUpdate = true;
                }
            }
            String width = DomUtil.getChildElementText(elem, "Width");
            String style = DomUtil.getChildElementText(elem, "Style");
            String constantValue = DomUtil.getChildElementText(elem, "ConstantValue");
            String minPrecision = DomUtil.getChildElementText(elem, "MinPrecision");
            String maxPrecision = DomUtil.getChildElementText(elem, "MaxPrecision");
            String service = DomUtil.getChildElementText(elem, "Service");
            String listMethod = DomUtil.getChildElementText(elem, "ListMethod");
            String objectClass = DomUtil.getChildElementText(elem, "ObjectClass");
            String enumClass = DomUtil.getChildElementText(elem, "EnumClass");
            String rowString = DomUtil.getChildElementText(elem, "Row");
            String subObjectDisplayField = DomUtil.getChildElementText(elem, "SubObjectDisplayField");

            int row = 0;
            if (rowString != null && rowString.length() > 0) {
                try { 
                    Integer temp = new Integer(rowString);
                    row = temp.intValue();
                } catch (NumberFormatException e) {
                    // TODO: report error
                }
            }
 
            ScreenColumnDescriptor screenColumnDescriptor = new ScreenColumnDescriptor();
            screenColumnDescriptor.setParent(parent);
            screenColumnDescriptor.setType(type);
            screenColumnDescriptor.setFieldName(fieldName);
            screenColumnDescriptor.setLookupName(lookupName);
            screenColumnDescriptor.setLookupVariable(lookupVariable);
            screenColumnDescriptor.setHeading(heading);
            screenColumnDescriptor.setLink(link);
            screenColumnDescriptor.setSupportsUpdate(supportsUpdate);
            screenColumnDescriptor.setWidth(width);
            screenColumnDescriptor.setStyle(style);
            screenColumnDescriptor.setMinPrecision(minPrecision);
            screenColumnDescriptor.setMaxPrecision(maxPrecision);
            screenColumnDescriptor.setService(service);
            screenColumnDescriptor.setListMethod(listMethod);
            screenColumnDescriptor.setObjectClass(objectClass);
            screenColumnDescriptor.setEnumClass(enumClass);
            screenColumnDescriptor.setConstantValue(constantValue);
            screenColumnDescriptor.setRow(row);
            screenColumnDescriptor.setChoices(loadChoices(elem));
            screenColumnDescriptor.setSubObjectDisplayField(subObjectDisplayField); 
            
            columns.add(screenColumnDescriptor); 
        }
        
        return columns;
     }

    
    private List loadChoices(Element fieldElement) {

        ArrayList choices = new ArrayList();
        Element columnsElement = DomUtil.getChildElement(fieldElement, "Choices");
        if (columnsElement == null) {
            return null;
        }
        NodeList columnsNodeList = columnsElement.getElementsByTagName("Choice");
        if (columnsNodeList == null) {
            return null;
        }

        for (int i = 0; i < columnsNodeList.getLength(); i++) {
            Element elem = (Element)columnsNodeList.item(i);
            String nodeName = elem.getNodeName();
            Assert.check(nodeName.equals("Choice"), "nodeName.equals(Choice)");


            String value = DomUtil.getChildElementText(elem, "Value"); 
            String display = DomUtil.getChildElementText(elem, "Display"); 
 
            ChoiceDescriptor choiceDescriptor = new ChoiceDescriptor();
            choiceDescriptor.setValue(value);
            choiceDescriptor.setDisplay(display);
            
            choices.add(choiceDescriptor); 
        }
        
        return choices;
     }

    
    private SelectData loadSelectData(ScreenFieldDescriptor parent, Element fieldElement) {

        SelectData selectData = new SelectData();
        Element selectDataElement = DomUtil.getChildElement(fieldElement, "SelectData");

        selectData.setService(DomUtil.getChildElementText(selectDataElement, "Service")); 
        selectData.setMethod(DomUtil.getChildElementText(selectDataElement, "Method")); 
        selectData.setListName(DomUtil.getChildElementText(selectDataElement, "ListName"));
        selectData.setIdName(DomUtil.getChildElementText(selectDataElement, "IdName"));
        selectData.setDisplayName(DomUtil.getChildElementText(selectDataElement, "DisplayName"));
        
        return selectData;
     }

    
}
