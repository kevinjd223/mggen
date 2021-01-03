/*
 * ServiceDescriptor.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.service;

import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a service.
 * Used to generate stateless session bean service interface. 
 * @author  kevind
 */
public class ServiceDescriptor implements Displayable {
    private Model model;
    private String ejbName;
    private String homeName;
    private String remoteName;
    private String ejbClassName;
    private String description;
    private EjbVersionEnum ejbVersion;
    
    private List<CrudObject> crudObjects = new ArrayList<CrudObject>();;
    private List<Method> methods = new ArrayList<Method>();

    /** 
     * Constructs a new instance of ServiceDescriptor 
     */
    public ServiceDescriptor(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
    
    public String getEjbName() {
        return ejbName;
    }    
    public void setEjbName(String newEjbName) {
        ejbName = newEjbName;
    }

    public String getHomeName() {
        return homeName;
    }    
    public void setHomeName(String newHomeName) {
        homeName = newHomeName;
    }

    public String getRemoteName() {
        return remoteName;
    }    
    public void setRemoteName(String newRemoteName) {
        remoteName = newRemoteName;
    }

    /**
     * The ejb service implementation. 
     * aka "service bean" 
     */
    public String getEjbClassName() {
        return ejbClassName;
    }    
    public void setEjbClassName(String newEjbClassName) {
        ejbClassName = newEjbClassName;
    }

    public String getDescription() {
        return description;
    }    
    public void setDescription(String newDescription) {
        description = newDescription;
    }
    
    

    public EjbVersionEnum getEjbVersion() {
		return ejbVersion;
	}

	public void setEjbVersion(EjbVersionEnum ejbVersion) {
		this.ejbVersion = ejbVersion;
	}

	public List<CrudObject> getCrudObjects() {
        return crudObjects;
    }    
    public void setCrudObjects(List<CrudObject> newCrudObjects) {
        crudObjects = newCrudObjects;
    }

    public List<Method> getMethods() {
        return methods;
    }
    public void setMethods(List<Method> newMethods) {
        methods = newMethods;
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ServiceDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "ejbName: " + ejbName);         
        displayBuffer.addLine(level+1, "homeName: " + homeName);         
        displayBuffer.addLine(level+1, "remoteName: " + remoteName);         
        displayBuffer.addLine(level+1, "ejbClassName: " + ejbClassName);         
        displayBuffer.addLine(level+1, "description: " + description);         
        displayBuffer.addLine(level+1, "ejbVersion: " + ejbVersion);         
    
        for (CrudObject serviceObject : crudObjects) {
            displayBuffer.append(serviceObject.display("", level+1, maxLevels, displayedObjects));
        }
        for (Method method : methods) {
            displayBuffer.append(method.display("", level+1, maxLevels, displayedObjects));
        }
        
        return displayBuffer.toString();
    }
    
}
