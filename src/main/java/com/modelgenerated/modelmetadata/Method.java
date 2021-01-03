/*
 * Method.java
 *
 * Created on April 30, 2003, 9:55 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.util.Assert;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class Method implements Displayable {
    private String description;
    private Prototype prototype;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public Prototype getPrototype() {
        return prototype;
    }
    
    public void setPrototype(Prototype newPrototype) {
        prototype = newPrototype;
    }
    
    public String getMethodNameAndParameters(){
        StringBuffer str = new StringBuffer();
        String description = this.getDescription();
            
        Prototype prototype = this.getPrototype();
        Assert.check(prototype != null, "prototype != null" + description);
        ClassDescriptor returnType = prototype.getReturnType(); 
            
        str.append(returnType.getClassName());
        str.append(" ");
        str.append(prototype.getMethodName());
        str.append("(");
            
        // void save" + valueObjectClassName + "(UserContext userContext, " + valueObjectClassName + " " + valueObjectVariableName + ") throws RemoteException;");
        Iterator<Parameter> j = prototype.getParameters().iterator();
        while (j.hasNext()) {
            Parameter parameter = (Parameter)j.next();
                
            ClassDescriptor parameterType = parameter.getType();
            str.append(parameterType.getClassName());
            if (parameter.getArray()) {
                str.append("[]");
            }
            str.append(" ");
            str.append(parameter.getName());
                
            if (j.hasNext()) {
                str.append(", ");
            }   
        }
            
        str.append(")");
        
        return str.toString();
    }
    
    
    /** Creates a new instance of QueryDescriptor */
    public Method() {
    }
    
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map displayedObjects = new HashMap();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("Method", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "description: " + description); 
        displayBuffer.append(prototype.display("", level+1, maxLevels, displayedObjects));
        
        return displayBuffer.toString();
    }    
    
    
}
