/* 
 * Prototype.java
 * Created on Dec 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * Copyright 2002-2005 Kevin Delargy.
 */
package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a method prototype
 *
 */
public class Prototype implements Displayable {
    String methodName;
    List<Parameter> parameters = new ArrayList<Parameter>();
    ClassDescriptor returnType;
    List<ClassDescriptor> exceptionList = new ArrayList<ClassDescriptor>();; 


	/**
	 * @return
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param string
	 */
	public void setMethodName(String string) {
		methodName = string;
	}

	/**
	 * @return
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * @param list
	 */
	public void setParameters(List<Parameter> list) {
		parameters = list;
	}

	/**
	 * @return
	 */
	public ClassDescriptor getReturnType() {
		return returnType;
	}

	/**
	 * @param descriptor
	 */
	public void setReturnType(ClassDescriptor descriptor) {
		returnType = descriptor;
	}
	
    
    public List<ClassDescriptor> getExceptionList() {
		return exceptionList;
	}
	public void setExceptionList(List<ClassDescriptor> exceptionList) {
		this.exceptionList = exceptionList;
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("Prototype", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "methodName: " + methodName); 
        displayBuffer.addLine(level+1, "returnType: " + returnType);
        
        for (Parameter parameter : parameters) {
            displayBuffer.append(parameter.display("", level+1, maxLevels, displayedObjects));
        }
 

        return displayBuffer.toString();
        
    }
    
    

}
