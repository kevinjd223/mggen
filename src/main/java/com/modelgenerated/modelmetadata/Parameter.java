/*
 * Created on Dec 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * Copyright 2002-2005 Kevin Delargy.
 */
 
package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Parameter implements Displayable {
    ClassDescriptor type;
    String name;
    boolean array;

	/**
	 * @return
	 */
	public ClassDescriptor getType() {
		return type;
	}

	/**
	 * @param descriptor
	 */
	public void setType(ClassDescriptor newType) {
		type = newType;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string
	 */
	public void setName(String newName) {
		name = newName;
	}
    
    // Displayable interface methods
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("Parameter", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "type: " + type); 
        displayBuffer.addLine(level+1, "name: " + name);

        return displayBuffer.toString();
        
    }

	public boolean getArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}
    
    

}
