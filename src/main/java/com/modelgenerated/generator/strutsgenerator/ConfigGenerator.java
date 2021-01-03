/*
 * TableGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.uimodel.ScreenFieldDescriptor;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author  kevind
 */
public class ConfigGenerator {
    protected GeneratorConfig generatorConfig;
    protected String formName;
    protected CodeBuffer formCode;
    protected String actionName;
    protected CodeBuffer actionCode;
    protected ScreenDescriptor screenDescriptor;
    
    /** Creates a new instance of ValueObjectGenerator */
    public ConfigGenerator() {
    }
    
    public void generate(ScreenDescriptor initScreenDescriptor) {
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        formCode = new CodeBuffer();
        actionCode = new CodeBuffer();
        screenDescriptor = initScreenDescriptor;        
        
        generateFileContent();
        
        Logger.debug(this, actionCode.toString());
        
        String fullyQualifiedName = generatorConfig.getStrutsConfigFile();
        Logger.debug(this, "fullyQualifiedName: " + fullyQualifiedName);
        String packagePath = fullyQualifiedNameToPackage(fullyQualifiedName);
        Logger.debug(this, "packagePath: " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        
        ConfigUpdater configUpdater = new ConfigUpdater();

        configUpdater.update(fullyQualifiedName, formName, formCode.toString(), actionName, actionCode.toString());
        
    }

    private void generateFileContent() {
        Logger.debug(this, screenDescriptor);
        generateForm();
        generateAction();        
    }
    
    protected void generateForm() {
        String indent = "                    ";
        formName = screenDescriptor.getFormDataName();

        formCode.addLine("    <form-bean      name='" + formName + "'");
        formCode.addLine(indent + "dynamic='true'");
        formCode.addLine(indent + "type='org.apache.struts.action.DynaActionForm'>");

        formCode.addLine(indent + "<form-property name='id' type='java.lang.String'/>");
        if (!StringUtil.isEmpty(screenDescriptor.getParentObjectId())) {            
            String parentObjectIdConstant = screenDescriptor.getParentObjectId();
            formCode.addLine(indent + "<form-property name='" + parentObjectIdConstant + "' type='java.lang.String'/>");                
        }
        
        Iterator i = screenDescriptor.getFieldList().iterator();
        while (i.hasNext()) {
            ScreenFieldDescriptor screenFieldDescriptor = (ScreenFieldDescriptor)i.next();
            String fieldName = screenFieldDescriptor.getFieldName();
            
            String fieldType = screenFieldDescriptor.getType();
            
            // undone: everything else maps to string.
            if ("Boolean".equals(fieldType)) { 
                formCode.addLine(indent + "<form-property name='" + convertToJavaVariableName(fieldName) + "' type='java.lang.Boolean'/>");
            } else if ("List".equals(fieldType)) { 
                formCode.addLine(indent + "<form-property name='" + convertToJavaVariableName(fieldName) + "' type='java.util.List'/>");
            } else if ("Link".equals(fieldType)) { 
                // do nothing
            } else if ("ChooseObject".equals(fieldType)) { 
                formCode.addLine(indent + "<form-property name='" + convertToJavaVariableName(fieldName) + "Id' type='java.lang.String'/>");
            } else {
                formCode.addLine(indent + "<form-property name='" + convertToJavaVariableName(fieldName) + "' type='java.lang.String'/>");
            }
        }        
        formCode.addLine(indent + "<form-property name=\"action\" type=\"java.lang.String\"/>");
        formCode.addLine(indent + "<form-property name=\"submitLink\" type=\"java.lang.String\"/>");

        
        formCode.addLine(indent + "</form-bean>");
        formCode.addLine();
    }
    
    protected void generateAction() {
        String indent = "                ";
        actionName = "/" + screenDescriptor.getActionName();
        String controller = screenDescriptor.getControllerClass();
        String tilesDefName = screenDescriptor.getTilesDefName();

    
        actionCode.addLine("    <action     path='" + actionName + "'");
        actionCode.addLine(indent + "type='" + controller + "'");
        actionCode.addLine(indent + "name='" + formName + "'");
        actionCode.addLine(indent + "scope='request'");
        actionCode.addLine(indent + "input='" + tilesDefName + "'");
        actionCode.addLine(indent + "parameter='method'>");
        actionCode.addLine(indent + "<forward name=\"Success\" path=\"" + screenDescriptor.getSuccessLink() + "\" redirect=\"true\"/>");
        actionCode.addLine("    </action>");
    
    }

    
    
    
    public String fullyQualifiedNameToPackage(String fqn) {
        // note this is different than the one in JavaCodeBaseGenerator
        Logger.debug(this, "FQN: " + fqn);
        return fqn.substring(0, fqn.lastIndexOf("/"));
    }
    
    private String convertToJavaVariableName(String variableName) {
        Assert.check(variableName != null, "variableName != null");
        if (variableName.length() == 1) {
            return variableName.toLowerCase();
        } else {
            return variableName.substring(0,1).toLowerCase() + variableName.substring(1);
        }
    }
    

    
    
}
