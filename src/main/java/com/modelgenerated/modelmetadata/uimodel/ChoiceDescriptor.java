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
public class ChoiceDescriptor implements Displayable {
    protected String value;
    protected String display;
    
    /** Creates a new instance of ScreenFieldDescriptor */
    public ChoiceDescriptor() {
    }

    public String getValue() {
        return value;
    }
    public void setValue(String newValue) {
        value = newValue;
    }
    
    public String getDisplay() {
        return display;
    }
    public void setDisplay(String newDisplay) {
        display = newDisplay;
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ScreenFieldDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "value: " + value); 
        displayBuffer.addLine(level+1, "display: " + display); 
        
        return displayBuffer.toString();
        
    }
}
