/*
 * ServiceGenerator.java
 *
 * Created on December 22, 2003, 10:54 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.servicegenerator;

import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.service.EjbVersionEnum;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.util.Assert;

/**
 *
 * @author  kevind
 */
public class ServiceGenerator {
    /** Creates a new instance of uiGenerator */
    public ServiceGenerator() {
    }
    
    public void generate(Model model, ServiceDescriptor serviceDescriptor) {        
    	Assert.check(serviceDescriptor != null, "serviceDescriptor != null");
    	Assert.check(serviceDescriptor.getEjbVersion() != null, "serviceDescriptor.getEjbVersion() != null");

    	System.out.println("serviceDescriptor.getEjbVersion(): " + serviceDescriptor.getEjbVersion()); 

    	ServiceInterfaceGenerator serviceInterfaceGenerator = new ServiceInterfaceGenerator();
        serviceInterfaceGenerator.generate(model, serviceDescriptor);
        
        ServiceCrudBaseGenerator serviceBeanBaseGenerator = new ServiceCrudBaseGenerator();
        serviceBeanBaseGenerator.generate(model, serviceDescriptor);
        
        if (EjbVersionEnum.EJB2 == serviceDescriptor.getEjbVersion()) {
            ServiceHomeGenerator serviceHomeGenerator = new ServiceHomeGenerator();
            serviceHomeGenerator.generate(model, serviceDescriptor);
        }
    }
    
}
