/*
 * ScreenColumnDescriptor.java
 *
 * Created on December 20, 2003, 7:51 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.uimodel;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class ScreenColumnDescriptor implements Displayable {
    protected ScreenFieldDescriptor parent;

    protected String fieldName;
    protected String type;
    protected String objectField;
    protected boolean supportsUpdate;
    protected String lookupName;
    protected String lookupVariable;
    protected String heading;
    protected String link;
    protected String width;
    protected String style;
    protected String constantValue;
    protected String minPrecision;
    protected String maxPrecision;
    // used with type=ChooseObject
    protected String service;
    protected String listMethod;
    protected String objectClass;
    protected String subObjectDisplayField;
    //used with type=Enum
    protected String enumClass;
    
    protected int row; // for table rows that have multiple rows.
    protected List<ChoiceDescriptor> choices;

    
    
    
    public ScreenFieldDescriptor getParent() {
        return parent;
    }
    public void setParent(ScreenFieldDescriptor newParent) {
        parent = newParent;
    }
    
    
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String newFieldName) {
        fieldName = newFieldName;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String newType) {
        type = newType;
    }
    
    public String getObjectField() {
        return objectField;
    }
    public void setObjectField(String newObjectField) {
        objectField = newObjectField;
    }

    public boolean getSupportsUpdate() {
        return supportsUpdate;
    }
    public void setSupportsUpdate(boolean newSupportsUpdate) {
        supportsUpdate = newSupportsUpdate;
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
    
    public String getHeading() {
        return heading;
    }
    public void setHeading(String newHeading) {
        heading = newHeading;
    }
    
    public String getLink() {
        return link;
    }
    public void setLink(String newLink) {
        link = newLink;
    }
    
    public String getWidth() {
        return width;
    }
    public void setWidth(String newWidth) {
        width = newWidth;
    }
    
    public String getStyle() {
        return style;
    }
    public void setStyle(String newStyle) {
        style = newStyle;
    }
    
    public String getConstantValue() {
        return constantValue;
    }
    public void setConstantValue(String newConstantValue) {
        constantValue = newConstantValue;
    }

    public String getMinPrecision() {
        return minPrecision;
    }
    public void setMinPrecision(String string) {
        minPrecision = string;
    }

    public String getMaxPrecision() {
        return maxPrecision;
    }
    public void setMaxPrecision(String string) {
        maxPrecision = string;
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
    // in the future should be able to infer this
    public String getEnumClass() {
        return enumClass;
    }
    public void setEnumClass(String newEnumClass) {
        enumClass = newEnumClass;
    }
    

    
    public int getRow() {
        return row;
    }
    public void setRow(int newRow) {
        row = newRow;
    }
    
    public List<ChoiceDescriptor> getChoices() {
        return choices;
    }
    public void setChoices(List<ChoiceDescriptor> newChoices) {
        choices = newChoices;
    }
    
    
    public String getSubObjectDisplayField() {
        return subObjectDisplayField;
    }
    public void setSubObjectDisplayField(String newSubObjectDisplayField) {
        subObjectDisplayField = newSubObjectDisplayField;
    }
    
    // Displayable methods
    public String display() {
        return display ("");
    }

    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }

    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ScreenColumnDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "fieldName: " + fieldName); 
        displayBuffer.addLine(level+1, "type: " + type); 
        displayBuffer.addLine(level+1, "objectField: " + objectField); 
        displayBuffer.addLine(level+1, "supportsUpdate: " + supportsUpdate); 
        displayBuffer.addLine(level+1, "lookupName: " + lookupName); 
        displayBuffer.addLine(level+1, "lookupVariable: " + lookupVariable); 
        displayBuffer.addLine(level+1, "heading: " + heading);        
        displayBuffer.addLine(level+1, "link: " + link);        
        displayBuffer.addLine(level+1, "width: " + width);        
        displayBuffer.addLine(level+1, "style: " + style);        
        displayBuffer.addLine(level+1, "minPrecision: " + minPrecision);        
        displayBuffer.addLine(level+1, "maxPrecision: " + maxPrecision);        
        displayBuffer.addLine(level+1, "enumClass: " + enumClass);
        displayBuffer.addLine(level+1, "service: " + service);        
        displayBuffer.addLine(level+1, "listMethod: " + listMethod);        
        displayBuffer.addLine(level+1, "objectClass: " + objectClass);        
        displayBuffer.addLine(level+1, "subObjectDisplayField: " + subObjectDisplayField);
                
        
        if (choices == null) {
            displayBuffer.addLine(level+1, "choices: " + choices);
        } else {
            displayBuffer.addLine(level+1, "choices: ");
            for (ChoiceDescriptor choiceDescriptor : choices) {
                displayBuffer.append(choiceDescriptor.display("", level+2, maxLevels, displayedObjects));
            }
        }
        
        return displayBuffer.toString();
        
    }

}
