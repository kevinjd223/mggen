/*
 * SelectData.java
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
public class SelectData implements Displayable {
    
    protected String service;
    protected String method;
    protected String listName;
    protected String idName;
    protected String displayName;
    
    /** Creates a new instance of ScreenFieldDescriptor */
    public SelectData() {
    }

    public String getService() {
        return service;
    }
    public void setService(String newService) {
        service = newService;
    }
    
    public String getMethod() {
        return method;
    }
    public void setMethod(String newMethod) {
        method = newMethod;
    }
    
    public String getListName() {
        return listName;
    }
    public void setListName(String newListName) {
        listName = newListName;
    }
    
    public String getIdName() {
        return idName;
    }
    public void setIdName(String newIdName) {
        idName = newIdName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String newDisplayName) {
        displayName = newDisplayName;
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
        
        displayBuffer.addLine(level+1, "service: " + service); 
        displayBuffer.addLine(level+1, "method: " + method); 
        displayBuffer.addLine(level+1, "listName: " + listName); 
        displayBuffer.addLine(level+1, "idName: " + idName); 
        displayBuffer.addLine(level+1, "displayName: " + displayName); 
        
        return displayBuffer.toString();
        
    }
}
