/* Model.java
 *
 * Copyright 2002-2024 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.modelmetadata.enums.EnumDescriptor;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.util.Assert;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the domain model of the application.
 */
public class Model {
    private String packagesToGenerate;
    private String copyrightNotice;
    private Map<String, ObjectDescriptor> objects = new HashMap<String, ObjectDescriptor>();
    private Map<String, ObjectDescriptor> externalObjects = new HashMap<String, ObjectDescriptor>();
    private Map<String, EnumDescriptor> enums = new HashMap<String, EnumDescriptor>();
    private Map<String, ServiceDescriptor> services = new HashMap<String, ServiceDescriptor>();

    /** Creates a new instance of ValueObjectDescription */
    public Model() {
    }
    
    
    public String getPackagesToGenerate() {
        return packagesToGenerate;
    }
    public void setPackagesToGenerate(String newPackagesToGenerate) {
        packagesToGenerate = newPackagesToGenerate;
    }
    
    public String getCopyrightNotice() {
        return copyrightNotice;
    }
    public void setCopyrightNotice(String newCopyrightNotice) {
        copyrightNotice = newCopyrightNotice;
    }
    
    /**
     * Adds an objectDescriptor to the model
     */
    public void addObject(ObjectDescriptor objectDescriptor) {
    	Assert.check(objectDescriptor != null, "objectDescriptor != null");
    	
    	ClassDescriptor classDescriptor = objectDescriptor.getValueObjectInterface();
    	Assert.check(classDescriptor != null, "classDescriptor != null");
    	
        String objectName = classDescriptor.getFQN();
    	Assert.check(objectName != null, "objectName != null");

    	ObjectDescriptor previous = objects.put(objectName, objectDescriptor);
        Assert.check(previous == null, "Duplicate field \"" + objectName + "\" found in Model");
    }
    public Map<String, ObjectDescriptor> getObjects() {
        return objects;
    }

    /**
     * Adds an externaObjectDescriptor to the model
     * ExternalObject are not generated, but are needed because this model refernences them
     */
    public void addExternalObject(ObjectDescriptor objectDescriptor) {
    	Assert.check(objectDescriptor != null, "objectDescriptor != null");
    	
    	ClassDescriptor classDescriptor = objectDescriptor.getValueObjectInterface();
    	Assert.check(classDescriptor != null, "classDescriptor != null");
    	
        String objectName = classDescriptor.getFQN();
    	Assert.check(objectName != null, "objectName != null");

    	ObjectDescriptor previous = externalObjects.put(objectName, objectDescriptor);
        Assert.check(previous == null, "Duplicate field \"" + objectName + "\" found in Model");
    }
    public Map<String, ObjectDescriptor> getExternalObjects() {
        return externalObjects;
    }
    
    /**
     * Find and object by the fully qualified interfaceName.
     * First looks in objects that are part of this model and if not found checks external objects  
     */
    public ObjectDescriptor findObject(String fullyQualifiedInterfaceName) {
    	
    	ObjectDescriptor objectDescriptor = objects.get(fullyQualifiedInterfaceName);
    	if (objectDescriptor == null) {
    		objectDescriptor = externalObjects.get(fullyQualifiedInterfaceName);
    	}
    	
        return objectDescriptor;
    }

    /**
     * Adds an Enum to the Model. 
     */
    public void addEnum(EnumDescriptor enumDescriptor) {
    	Assert.check(enumDescriptor != null, "enumDescriptor != null");
    	
    	ClassDescriptor classDescriptor = enumDescriptor.getImplementation();
    	Assert.check(classDescriptor != null, "classDescriptor != null");
    	
        String enumName = classDescriptor.getFQN();
        
        Object previous = enums.put(enumName, enumDescriptor); 
    	Assert.check(enumName != null, "enumName != null");

        Assert.check(previous == null, "Duplicate Enum \"" + enumName + "\" found in Model");
    }
    public Map<String, EnumDescriptor> getEnums() {
        return enums;
    }


    public void addService(ServiceDescriptor serviceDescriptor) {
    	Assert.check(serviceDescriptor != null, "serviceDescriptor != null");
    	
        String remoteName = serviceDescriptor.getRemoteName();
    	Assert.check(remoteName != null, "remoteName != null");
    	
    	ServiceDescriptor previous = services.put(remoteName, serviceDescriptor); 

        Assert.check(previous == null, "Duplicate Service \"" + remoteName + "\" found in Model");
    }

    /**
     * Return map of the service in the model.
     */
    public Map<String, ServiceDescriptor> getServices() {
        return services;
    }

}
