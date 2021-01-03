/*
 * ClassDescriptor.java
 *
 * Created on March 13, 2003, 10:56 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;


import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a java class name. It has methods for getting different 
 * properties of the name.
 * Is immutable. 
 *
 * @author  kevind
 */
public class ClassDescriptor implements Displayable {
    private String fullyQualifiedName;
    
    /** Creates a new instance of ClassName */
    public ClassDescriptor(String initFullyQualifiedName) {
        fullyQualifiedName = initFullyQualifiedName == null ? "" : initFullyQualifiedName.trim();
    }
    
    /**
     * Returns the fully qualified class name. 
     */    
    public String getFQN() {
        return fullyQualifiedName;
    }
    
    /**
     * Returns a default java variable name base on the class name. 
     */
    public String getJavaVariableName() {
        if (getClassName().length() == 1) {
            return getClassName().toLowerCase();
        } else {
            return getClassName().substring(0,1).toLowerCase() + getClassName().substring(1);
        }
    }
    
    /**
     *  Converts a fully qualified class name to classname by removing the package name.
     */
    public String getClassName() {
        return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".")+1);
    }
    
    /**
     *  Converts a fully qualified class name to package name by removing the class name.
     */
    public String getPackage() {
        int lasDotIndex = fullyQualifiedName.lastIndexOf(".");
        if (lasDotIndex == -1) { 
            return "";            
        } else {
            return fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf("."));
        }
    }  
    
    public String toString() {
        return fullyQualifiedName;
    }
    
    // Displayable interface methods
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ClassDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "fullyQualifiedName: " + fullyQualifiedName); 

        return displayBuffer.toString();
        
    }
    
}
