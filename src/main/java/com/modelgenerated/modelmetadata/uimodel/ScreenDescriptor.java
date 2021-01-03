/*
 * ScreenDescriptor.java
 *
 * Created on December 20, 2003, 7:50 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.uimodel;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import com.modelgenerated.modelmetadata.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
/**
 *
 * @deprecated
 */
public class ScreenDescriptor implements Displayable {
    protected Model model;

    protected boolean shouldGenerate;

    protected String actionName;
    protected String screenType;
    protected String formDataName;
    protected String folder;
    protected String controllerClass;
    protected String controllerBaseClass;

    protected String tilesDefName;
    protected String tilesDefBase;
    protected String resourcePrefix;
    protected String jspAddFile;
    protected String jspSearchFile;
    protected String jspUpdateFile;
    protected String jspViewFile;
    protected String menuName;
    protected String successLink;
    protected String closeLink;
    
    protected String valueObjectClass;
    protected String service;
    protected String findMethod;
    protected String saveMethod;
    protected String newMethod;
    protected String searchMethod;

    protected String parentAttribute;
    protected String parentService;
    protected String parentFindMethod;
    protected String parentObjectId;
    
    protected List fieldList;
    
    /** Creates a new instance of ScreenDescriptor */
    public ScreenDescriptor() {
    }
    
    public Model getModel() {
        return model;
    }
    public void setModel(Model newModel) {
        model = newModel;
    }

    public boolean getShouldGenerate() {
        return shouldGenerate;
    }
    public void setShouldGenerate(boolean newShouldGenerate) {
        shouldGenerate = newShouldGenerate;
    }
    
    public String getActionName() {
        return actionName;
    }
    public void setActionName(String newActionName) {
        actionName = newActionName;
    }
    
    public String getScreenType() {
        return screenType;
    }
    public void setScreenType(String newScreenType) {
        screenType = newScreenType;
    }
    
    //protected String formDataName;
    public String getFormDataName() {
        return formDataName;
    }
    public void setFormDataName(String newFormDataName) {
        formDataName = newFormDataName;
    }
    
    //protected String folder;
    public String getFolder() {
        return folder;
    }
    public void setFolder(String newFolder) {
        folder = newFolder;
    }
    
    //protected String controllerClass;
    public String getControllerClass() {
        return controllerClass;
    }
    public void setControllerClass(String newControllerClass) {
        controllerClass = newControllerClass;
    }
    
    //protected String controllerBaseClass;
    public String getControllerBaseClass() {
        return controllerBaseClass;
    }
    public void setControllerBaseClass(String newControllerBaseClass) {
        controllerBaseClass = newControllerBaseClass;
    }
    
    // protected String tilesDefName;
    public String getTilesDefName() {
        return tilesDefName;
    }
    public void setTilesDefName(String newTilesDefName) {
        tilesDefName = newTilesDefName;
    }
    
    // protected String tilesDefBase;
    public String getTilesDefBase() {
        return tilesDefBase;
    }
    public void setTilesDefBase(String newTilesDefBase) {
        tilesDefBase = newTilesDefBase;
    }
    
    // protected String resourcePrefix;
    public String getResourcePrefix() {
        return resourcePrefix;
    }
    public void setResourcePrefix(String newResourcePrefix) {
        resourcePrefix = newResourcePrefix;
    }
    
    public String getJspAddFile() {
        return jspAddFile;
    }
    public void setJspAddFile(String newJspAddFile) {
        jspAddFile = newJspAddFile;
    }
    
    public String getJspUpdateFile() {
        return jspUpdateFile;
    }
    public void setJspUpdateFile(String newJspUpdateFile) {
        jspUpdateFile = newJspUpdateFile;
    }
    
    public String getJspSearchFile() {
        return jspSearchFile;
    }
    public void setJspSearchFile(String newJspSearchFile) {
        jspSearchFile = newJspSearchFile;
    }
    
    public String getJspViewFile() {
        return jspViewFile;
    }
    public void setJspViewFile(String newJspViewFile) {
        jspViewFile = newJspViewFile;
    }
    
    public String getMenuName() {
        return menuName;
    }
    public void setMenuName(String newMenuName) {
        menuName = newMenuName;
    }

    public String getSuccessLink() {
        return successLink;
    }
    public void setSuccessLink(String newSuccessLink) {
        successLink = newSuccessLink;
    }

    public String getCloseLink() {
        return closeLink;
    }
    public void setCloseLink(String newCloseLink) {
        closeLink = newCloseLink;
    }
    
    public String getService() {
        return service;
    }
    public void setService(String newService) {
        service = newService;
    }
    
    public String getValueObjectClass() {
        return valueObjectClass;
    }
    public void setValueObjectClass(String newValueObjectClass) {
        valueObjectClass = newValueObjectClass;
    }
    
    public String getFindMethod() {
        return findMethod;
    }
    public void setFindMethod(String newFindMethod) {
        findMethod = newFindMethod;
    }
    
    public String getSaveMethod() {
        return saveMethod;
    }
    public void setSaveMethod(String newSaveMethod) {
        saveMethod = newSaveMethod;
    }
    
    public String getNewMethod() {
        return newMethod;
    }
    public void setNewMethod(String newNewMethod) {
        newMethod = newNewMethod;
    }

    public String getSearchMethod() {
        return searchMethod;
    }
    public void setSearchMethod(String newSearchMethod) {
        searchMethod = newSearchMethod;
    }

    public String getParentAttribute() {
        return parentAttribute;
    }
    public void setParentAttribute(String newParentAttribute) {
        parentAttribute = newParentAttribute;
    }

    public String getParentService() {
        return parentService;
    }
    public void setParentService(String newParentService) {
        parentService = newParentService;
    }

    public String getParentFindMethod() {
        return parentFindMethod;
    }
    public void setParentFindMethod(String newParentFindMethod) {
        parentFindMethod = newParentFindMethod;
    }

    public String getParentObjectId() {
        return parentObjectId;
    }
    public void setParentObjectId(String newParentObjectId) {
        parentObjectId = newParentObjectId;
    }
    
    public List getFieldList() {
        return fieldList;
    }
    public void setFieldList(List newFieldList) {
        fieldList = newFieldList;
    }

    // Displayable methods
    public String display() {
        return display ("");
    }

    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }

    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ScreenDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "shouldGenerate: " + shouldGenerate); 
        displayBuffer.addLine(level+1, "actionName: " + actionName); 
        displayBuffer.addLine(level+1, "screenType: " + screenType); 
        displayBuffer.addLine(level+1, "formDataName: " + formDataName); 
        displayBuffer.addLine(level+1, "folder: " + folder); 
        displayBuffer.addLine(level+1, "controllerClass: " + controllerClass); 
        displayBuffer.addLine(level+1, "tilesDefName: " + tilesDefName); 
        displayBuffer.addLine(level+1, "resourcePrefix: " + resourcePrefix); 
        displayBuffer.addLine(level+1, "jspAddFile: " + jspAddFile); 
        displayBuffer.addLine(level+1, "jspSearchFile: " + jspSearchFile); 
        displayBuffer.addLine(level+1, "jspUpdateFile: " + jspUpdateFile); 
        displayBuffer.addLine(level+1, "jspViewFile: " + jspViewFile); 
        displayBuffer.addLine(level+1, "menuName: " + menuName); 
        displayBuffer.addLine(level+1, "valueObjectClass: " + valueObjectClass); 
        displayBuffer.addLine(level+1, "service: " + service); 
        displayBuffer.addLine(level+1, "findMethod: " + findMethod); 
        displayBuffer.addLine(level+1, "saveMethod: " + saveMethod); 
        displayBuffer.addLine(level+1, "newMethod: " + newMethod); 
        displayBuffer.addLine(level+1, "searchMethod: " + searchMethod); 
        displayBuffer.addLine(level+1, "parentAttribute: " + parentAttribute); 
        displayBuffer.addLine(level+1, "parentService: " + parentService); 
        displayBuffer.addLine(level+1, "parentFindMethod: " + parentFindMethod); 
        displayBuffer.addLine(level+1, "parentObjectId: " + parentObjectId); 
    
        if (fieldList != null) { 
            Iterator i = fieldList.iterator();
            while (i.hasNext()) {
                ScreenFieldDescriptor fieldDescriptor = (ScreenFieldDescriptor)i.next();
                displayBuffer.append(fieldDescriptor.display("", level+1, maxLevels, displayedObjects));
            }
        }
        
        return displayBuffer.toString();
        
    }
}
