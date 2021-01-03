/*
 * QueryDescriptor.java
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
 */
public class QueryDescriptor implements Displayable {
    private QueryTypeEnum type;
    private String fieldName;
    private String methodName;
    private String orderBy;
    
    public QueryTypeEnum getType() {
        return type;
    }
    
    public void setType(QueryTypeEnum newType) {
        type = newType;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String newFieldName) {
        fieldName = newFieldName;
    }

    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String newMethodName) {
        methodName = newMethodName;
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(String newOrderBy) {
        orderBy = newOrderBy;
    }
    
    
    
    /** Creates a new instance of QueryDescriptor */
    public QueryDescriptor() {
    }
    
    public String display() {
        return display ("");
    }
    
    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("QueryDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "type: " + type); 
        displayBuffer.addLine(level+1, "fieldName: " + fieldName); 
        displayBuffer.addLine(level+1, "methodName: " + methodName); 
        displayBuffer.addLine(level+1, "orderBy: " + orderBy); 
        
        return displayBuffer.toString();
    }    
    
    
}
