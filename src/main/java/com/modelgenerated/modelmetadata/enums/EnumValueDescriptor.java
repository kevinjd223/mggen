/*
 * FieldDescriptor.java
 *
 * Created on January 30, 2003, 5:12 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.enums;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class EnumValueDescriptor implements Displayable {
    private EnumDescriptor parentObject;
    private String key;
    private String description;
    
    /** Creates a new instance of FieldDescriptor */
    public EnumValueDescriptor(EnumDescriptor parentObject) {
        this.parentObject = parentObject;
    }
    
    public String getKey() {
        return key;
    }    
    public void setKey(String newName) {
        key = newName;
    }
    
    public String getDescription() {
        return description;
    }    
    public void setDescription(String newDescription) {
        description = newDescription;
    }
    
    // Displayable
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("FieldDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "name: " + key); 
        displayBuffer.addLine(level+1, "description: " + description); 
        
        return displayBuffer.toString();
    }    
    
}
