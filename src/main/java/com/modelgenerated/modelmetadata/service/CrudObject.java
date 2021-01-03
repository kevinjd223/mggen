/*
 * CrudObject.java
 *
 * Created on September 17, 2004, 6:30 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.service;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.util.StringUtil;
import com.modelgenerated.foundation.debug.DisplayBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author  kevind
 */
public class CrudObject implements Displayable {
    String name;
    boolean generateListMethods;
    boolean generateSearchMethod;
    String findByIdExceptions;
    String saveExceptions;
    
    /** Creates a new instance of ServiceObject */
    public CrudObject() {
    }
    
    public String getName() {
        return name;
    }    
    public void setName(String newName) {
        name = newName;
    }

    public boolean getGenerateListMethods() {
        return generateListMethods;
    }
    public void setGenerateListMethods(boolean newGenerateListMethods) {
        generateListMethods = newGenerateListMethods;
    }

    public boolean getGenerateSearchMethod() {
        return generateSearchMethod;
    }
    public void setGenerateSearchMethod(boolean newGenerateSearchMethod) {
        generateSearchMethod = newGenerateSearchMethod;
    }
    
    public String getFindByIdExceptions() {
		return findByIdExceptions;
	}
	public void setFindByIdExceptions(String findByIdExceptions) {
		this.findByIdExceptions = findByIdExceptions;
	}
	
	/**
	 * Returns list of exception that are thrown from the findById method
	 * @return list of ClassDescriptor
	 */	
	public List<ClassDescriptor> getFindByIdExceptionList() {
		return parseClassList(findByIdExceptions);
	}
	
	/**
	 * Returns the comma separated list of exception. 
	 * This list trims the canonical names down to just the class names without the paths. 
	 * @return
	 */
	public String getFindByIdThrowStatement() {		
		return getThrowExceptionStatement(this.getFindByIdExceptionList());
	}

	private String getThrowExceptionStatement(List<ClassDescriptor> exceptionList) {		
		StringBuilder str = new StringBuilder();
		if (exceptionList.size() > 0) {
			str.append(" throws ");
			str.append(exceptionList.get(0).getClassName());
		}
		for (int i = 1; i < exceptionList.size(); i++) {
			str.append(", ");
			str.append(exceptionList.get(1).getClassName());
		}
		return str.toString();
	}


	public String getSaveExceptions() {
		return saveExceptions;
	}
	public void setSaveExceptions(String saveExceptions) {
		this.saveExceptions = saveExceptions;
	}
	
	/**
	 * Returns list of exception that are thrown from the save method
	 * @return list of ClassDescriptor
	 */	
	public List<ClassDescriptor> getSaveExceptionList() {
		return parseClassList(findByIdExceptions);
	}

	/*
	 * converts a comma separated list into a list of ClassDescriptors
	 */
	public List<ClassDescriptor> parseClassList(String classList) {
		List<ClassDescriptor> list = new ArrayList<ClassDescriptor>();
		
		if (classList != null && !StringUtil.isEmpty(classList)) {
	        String[] classArray = classList.split(",");
	        Arrays.stream(classArray).forEach(x -> list.add(new ClassDescriptor(x)));
		}
		return list;
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("CrudObject", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "name: " + name);         
        
        return displayBuffer.toString();
        
    }
    
    

}
