/*
 * ReferenceDescriptor.java
 *
 * Created on January 30, 2003, 5:12 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.util.Assert;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author  kevind
 */
public class ReferenceDescriptor implements Displayable {
    private ObjectDescriptor parentObject;
    private String name;
    private String description;
    private ClassDescriptor classDescriptor; 
    private ReferenceTypeEnum type; 
    private ClassDescriptor targetClass; 
    private String targetMethod; 
    private ClassDescriptor searchCriteria; 
    
    
    /** Creates a new instance of FieldDescriptor */
    public ReferenceDescriptor(ObjectDescriptor parentObject) {
        Assert.check(parentObject != null, "parentObject != null");
        this.parentObject = parentObject;
    }
    
    public String getName() {
        return name;
    }    
    public void setName(String newName) {
        name = newName;
    }
    
    public String getDescription() {
        return description;
    }    
    public void setDescription(String newDescription) {
        description = newDescription;
    }
    
    public ReferenceTypeEnum getType() {
        return type;
    }
    
    public void setType(ReferenceTypeEnum newType) {
        type = newType;
    }
    
    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }
    
    public void setClassDescriptor(ClassDescriptor newClassDescriptor) {
        classDescriptor = newClassDescriptor;
    }
    
    public String getJavaType() {
        return getClassDescriptor().getClassName();
    }

    public String getJavaVariableName() {
        // undone: this code should move to stringutils
        if (name.length() == 1) {
            return name.toLowerCase();
        } else {
            return name.substring(0,1).toLowerCase() + name.substring(1);
        }
    }  

    
        public ClassDescriptor getTargetClass() {
        return targetClass;
    }
    
    public void setTargetClass(ClassDescriptor newTargetClass) {
        targetClass = newTargetClass;
    }
    public ObjectDescriptor getTargetObjectDescriptor() {
        Model model = parentObject.getModel();

        String fullyQualifiedTargetClass = targetClass.getFQN();
        Assert.check(fullyQualifiedTargetClass != null, "fullyQualifiedTargetClass != null");
        ObjectDescriptor objectDescriptor = model.findObject(fullyQualifiedTargetClass);
        Assert.check(objectDescriptor != null, "objectDescriptor != null - " + fullyQualifiedTargetClass);
        
        return objectDescriptor;
        
    }

    
    public String getTargetMethod() {
        return targetMethod;
    }
    
    public void setTargetMethod(String newTargetMethod) {
        targetMethod = newTargetMethod;
    }
    
    public ClassDescriptor  getSearchCriteria() {
        return searchCriteria;
    }
    
    public void setSearchCriteria(ClassDescriptor  newSearchCriteria) {
        searchCriteria = newSearchCriteria;
    }
    

    
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("ReferenceDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "name: " + name); 
        displayBuffer.addLine(level+1, "description: " + description); 
        displayBuffer.addLine(level+1, "type: " + type);
        displayBuffer.addLine(level+1, "targetMethod: " + targetMethod);
        if (classDescriptor != null) { 
            displayBuffer.addLine(level+1, "classDescriptor: " + classDescriptor.getFQN()); 
        }
        if (targetClass != null) {
            displayBuffer.addLine(level+1, "targetClass: " + targetClass.getFQN()); 
        }
        if (searchCriteria != null) { 
            displayBuffer.addLine(level+1, "searchCriteria: " + searchCriteria.getFQN()); 
        }
        return displayBuffer.toString();
    }    
    
}
