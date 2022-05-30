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
import com.modelgenerated.util.StringUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * Describes to reference from one object to another.
 * Could be one to one or one to many depending on ReferenceTypeEnum.
 * @author  kevind
 */
public class ReferenceDescriptor {
    private final ObjectDescriptor parentObject;
    private final String name;
    private final String description;
    private final ReferenceTypeEnum type;
    private final ClassDescriptor classDescriptor;
    private final ClassDescriptor targetClass;
    private final String targetMethod;
    private final ClassDescriptor searchCriteria;
    
    
    /** Creates a new instance of ReferenceDescriptor */
    public ReferenceDescriptor(ObjectDescriptor parentObject,
                               String name,
                               String description,
                               ReferenceTypeEnum type,
                               ClassDescriptor classDescriptor,
                               ClassDescriptor targetClass,
                               String targetMethod,
                               ClassDescriptor searchCriteria) {
        Assert.check(parentObject != null, "parentObject != null");
        this.parentObject = parentObject;
        this.name = name;
        this.description = description;
        this.type = type;
        this.classDescriptor = classDescriptor;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.searchCriteria = searchCriteria;
    }


    public String getName() {
        return name;
    }    

    public String getDescription() {
        return description;
    }    

    public ReferenceTypeEnum getType() {
        return type;
    }
    
    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
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
    
    public ClassDescriptor  getSearchCriteria() {
        return searchCriteria;
    }

}
