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
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class ScreenListDescriptor implements Displayable {
    protected String name;
    protected String type;
    protected String objectField;
    protected String lookupName;
    protected String lookupVariable;
    
    /** Creates a new instance of ScreenFieldDescriptor */
    public ScreenListDescriptor() {
    }

    public String getName() {
        return name;
    }
    public void setName(String newName) {
        name = newName;
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
        
        displayBuffer.addLine(level+1, "name: " + name); 
        displayBuffer.addLine(level+1, "type: " + type); 
        displayBuffer.addLine(level+1, "objectField: " + objectField); 
        
        return displayBuffer.toString();
        
    }
}
