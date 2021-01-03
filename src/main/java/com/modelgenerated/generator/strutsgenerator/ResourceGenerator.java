/*
 * ResourceGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenFieldDescriptor;
import com.modelgenerated.util.Assert;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author  kevind
 */
public class ResourceGenerator {
    protected GeneratorConfig generatorConfig;
    protected ScreenDescriptor screenDescriptor;
    protected ResourceUpdater resourceUpdater;
    protected String fullyQualifiedName;

    /** Creates a new instance of ValueObjectGenerator */
    public ResourceGenerator() {
    }
    
    public void generate(ScreenDescriptor screenDescriptor) {
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        this.screenDescriptor = screenDescriptor;        
        
        resourceUpdater = new ResourceUpdater();
        
        fullyQualifiedName = generatorConfig.getResourceFile();
        Logger.debug(this, "fullyQualifiedName: " + fullyQualifiedName);
        String packagePath = fullyQualifiedNameToPackage(fullyQualifiedName);
        Logger.debug(this, "packagePath: " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        generateFileContent();
        

    }

    
    private void generateFileContent() {
        Logger.debug(this, screenDescriptor);

        String resourcePrefix = screenDescriptor.getResourcePrefix();
        String pageTitleResource = null;
        String pageTitle = null;
        
        if ("add".equals(screenDescriptor.getScreenType()))  {
            pageTitleResource = resourcePrefix + ".pagetitle.add";
            pageTitle = "Add " + screenDescriptor.getFolder();
        } else if ("update".equals(screenDescriptor.getScreenType()))  {
            pageTitleResource = resourcePrefix + ".pagetitle.update";
            pageTitle = "Update " + screenDescriptor.getFolder();
        } else if ("view".equals(screenDescriptor.getScreenType()))  {
            pageTitleResource= resourcePrefix + ".pagetitle.view";
            pageTitle = "View " + screenDescriptor.getFolder();
        } else if ("report".equals(screenDescriptor.getScreenType()))  {
            pageTitleResource = resourcePrefix + ".pagetitle.report";
            pageTitle = screenDescriptor.getFolder() + " Report";
        }
        
        resourceUpdater.addResourceString(fullyQualifiedName, pageTitleResource, pageTitle);
        
        // add error messages
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();
            
            String type = screenFieldDescriptor.getType();
            if (type.equals("Date")) { 
                String fieldName = screenFieldDescriptor.getFieldName();
                String error = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase();
                String message = fieldName + " date must be in the form \"{0}\"<br>";
                resourceUpdater.addResourceString(fullyQualifiedName, error, message);
            } else if (type.equals("Double") || type.equals("int")) {
                String fieldName = screenFieldDescriptor.getFieldName();
                String error = screenDescriptor.getResourcePrefix() + ".error." + fieldName.toLowerCase() + "notnumeric";
                String message = fieldName + " must be numeric<br>";
                resourceUpdater.addResourceString(fullyQualifiedName, error, message);
            }
        }        
        
        // add prompts
        i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();
            
            String type = screenFieldDescriptor.getType();
            if ("Link".equals(type)) {
                
            } else {
                String fieldName = screenFieldDescriptor.getFieldName();
                fieldName = fieldName.toLowerCase();
                String prompt = screenDescriptor.getResourcePrefix() + ".prompt." + fieldName;
                String promptValue = screenFieldDescriptor.getPromptValue();
                
                resourceUpdater.addResourceString(fullyQualifiedName, prompt, promptValue);
            }
        }        
    }
    
    public String fullyQualifiedNameToPackage(String fqn) {
        // note this is different than the one in JavaCodeBaseGenerator
        Logger.debug(this, "FQN: " + fqn);
        return fqn.substring(0, fqn.lastIndexOf("/"));
    }
    
    
}
