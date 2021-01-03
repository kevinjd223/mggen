/*
 * ScreenFieldDescriptor.java
 *
 * Created on December 20, 2003, 7:51 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.uimodel;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class ScreenFieldDescriptor implements Displayable {
    protected ScreenDescriptor screenDescriptor;
    protected String fieldName;
    protected String promptValue;
    protected String type;
    protected boolean supportsUpdate;
    protected boolean drawPrompt;
    protected String lookupName;
    protected String lookupVariable;
    protected String searchCriteriaClass;
    protected String searchCriteriaConstant;
    protected String searchMethod;
    protected String link;
    protected List columns;
    protected List choices;
    protected SelectData selectData;
    // used with type=ChooseObject
    protected String service;
    protected String listMethod;
    protected String objectClass;
    protected String subObjectDisplayField;
    //used with type=Enum
    protected String enumClass;
    

    
    public ScreenDescriptor getScreenDescriptor() {
        return screenDescriptor;
    }
    public void setScreenDescriptor(ScreenDescriptor newScreenDescriptor) {
        screenDescriptor = newScreenDescriptor;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String newFieldName) {
        fieldName = newFieldName;
    }
    
    public String getPromptValue() {
        return promptValue;
    }
    public void setPromptValue(String newPromptValue) {
        promptValue = newPromptValue;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String newType) {
        type = newType;
    }
    
    public boolean getSupportsUpdate() {
        return supportsUpdate;
    }
    public void setSupportsUpdate(boolean newSupportsUpdate) {
        supportsUpdate = newSupportsUpdate;
    }

    public boolean getDrawPrompt() {
        return drawPrompt;
    }
    public void setDrawPrompt(boolean newDrawPrompt) {
        drawPrompt = newDrawPrompt;
    }

    public String getLookupName() {
        return lookupName;
    }
    public void setLookupName(String newLookupName) {
        lookupName = newLookupName;
    }
    
    public String getLookupVariable() {
        return lookupVariable;
    }
    public void setLookupVariable(String newLookupVariable) {
        lookupVariable = newLookupVariable;
    }
    
    public String getSearchCriteriaClass() {
        return searchCriteriaClass;
    }
    public void setSearchCriteriaClass(String newSearchCriteriaClass) {
        searchCriteriaClass = newSearchCriteriaClass;
    }
    
    public String getSearchCriteriaConstant() {
        return searchCriteriaConstant;
    }
    public void setSearchCriteriaConstant(String newSearchCriteriaConstant) {
        searchCriteriaConstant = newSearchCriteriaConstant;
    }
    
    public String getSearchMethod() {
        return searchMethod;
    }
    public void setSearchMethod(String newSearchMethod) {
        searchMethod = newSearchMethod;
    }
    
    public String getLink() {
        return link;
    }
    public void setLink(String newLink) {
        link = newLink;
    }
    
    public List getColumns() {
        return columns;
    }
    public void setColumns(List newColumns) {
        columns = newColumns;
    }
    
    public List getChoices() {
        return choices;
    }
    public void setChoices(List newChoices) {
        choices = newChoices;
    }
    
    public SelectData getSelectData() {
        return selectData;
    }
    public void setSelectData(SelectData newSelectData) {
        selectData = newSelectData;
    }
    
    public String getService() {
        return service;
    }
    public void setService(String newService) {
        service= newService;
    }
    public String getListMethod() {
        return listMethod;
    }
    public void setListMethod(String newListMethod) {
        listMethod = newListMethod;
    }
    // in the future should be able to infer this
    public String getObjectClass() {
        return objectClass;
    }
    public void setObjectClass(String newObjectClass) {
        objectClass = newObjectClass;
    }
    public String getSubObjectDisplayField() {
        return subObjectDisplayField;
    }
    public void setSubObjectDisplayField(String newSubObjectDisplayField) {
        subObjectDisplayField = newSubObjectDisplayField;
    }
    
    // in the future should be able to infer this
    public String getEnumClass() {
        return enumClass;
    }
    public void setEnumClass(String newEnumClass) {
        enumClass = newEnumClass;
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ScreenFieldDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "fieldName: " + fieldName); 
        displayBuffer.addLine(level+1, "promptValue: " + promptValue); 
        displayBuffer.addLine(level+1, "type: " + type); 
        displayBuffer.addLine(level+1, "supportsUpdate: " + supportsUpdate); 
        displayBuffer.addLine(level+1, "drawPrompt: " + drawPrompt); 
        displayBuffer.addLine(level+1, "lookupName: " + lookupName); 
        displayBuffer.addLine(level+1, "lookupVariable: " + lookupVariable); 
        displayBuffer.addLine(level+1, "searchCriteriaClass: " + searchCriteriaClass);        
        displayBuffer.addLine(level+1, "searchCriteriaConstant: " + searchCriteriaConstant);        
        displayBuffer.addLine(level+1, "searchMethod: " + searchMethod);        
        displayBuffer.addLine(level+1, "link: " + link);        
        displayBuffer.addLine(level+1, "service: " + service);        
        displayBuffer.addLine(level+1, "listMethod: " + listMethod);        
        displayBuffer.addLine(level+1, "objectClass: " + objectClass);        
        displayBuffer.addLine(level+1, "subObjectDisplayField: " + subObjectDisplayField);        
        displayBuffer.addLine(level+1, "enumClass: " + enumClass);        

        if (columns == null) {
            displayBuffer.addLine(level+1, "columns: " + columns);
        } else {
            displayBuffer.addLine(level+1, "columns: ");
            Iterator i = columns.iterator();
            while (i.hasNext()) {
                ScreenColumnDescriptor screenColumnDescriptor = (ScreenColumnDescriptor)i.next();
                displayBuffer.append(screenColumnDescriptor.display("", level+2, maxLevels, displayedObjects));
            }
        }
        
        if (choices == null) {
            displayBuffer.addLine(level+1, "choices: " + choices);
        } else {
            displayBuffer.addLine(level+1, "choices: ");
            Iterator i = choices.iterator();
            while (i.hasNext()) {
                ChoiceDescriptor choiceDescriptor = (ChoiceDescriptor)i.next();
                displayBuffer.append(choiceDescriptor.display("", level+2, maxLevels, displayedObjects));
            }
        }
        if (selectData == null) {
            displayBuffer.addLine(level+1, "selectData: " + selectData);
        } else {
            displayBuffer.append(selectData.display("selectData", level+1, maxLevels, displayedObjects));
        }
        
        return displayBuffer.toString();
        
    }
}
