/*
 * Operation.java
 *
 * Created on April 30, 2003, 9:55 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  kevind
 * 
 * @deprecated
 */
public class Operation implements Displayable {
    private String protoType;
    
    public String getProtoType() {
        return protoType;
    }
    
    public void setPrototype(String newProtoType) {
        protoType = newProtoType;
    }

    
    
    /** Creates a new instance of QueryDescriptor */
    public Operation() {
    }
    
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("Operation", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "protoType: " + protoType); 
        
        return displayBuffer.toString();
    }    
    
    
}
