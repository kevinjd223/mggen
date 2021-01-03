/*
 * ValueObjectGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;


import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.CodeGenerator;
import com.modelgenerated.generator.GeneratorEventFacade;
import com.modelgenerated.generator.GeneratorEventListener;
import com.modelgenerated.generator.GeneratorEventSeverityEnum;
import com.modelgenerated.generator.ReportedGeneratorEventException;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;

/**
 *
 *
 * TODO: move to generator.java package. 
 * @author  kevind
 */
abstract class JavaCodeBaseGenerator implements CodeGenerator {
    CodeBuffer code;
    ObjectDescriptor objectDescriptor;
    Model model;
    GeneratorEventListener eventListener;
    
    /** Creates a new instance of ValueObjectGenerator */
    public JavaCodeBaseGenerator() {
    }    
    
    public void generate(String rootPath, ObjectDescriptor initObjectDescriptor) {
        Logger.debug(this, "*******************************");
        Logger.debug(this, "rootPath: " + rootPath);
        Logger.debug(this, "class: " + this.getClass().getName());
        //Logger.debug(this, initObjectDescriptor);        
        //Logger.debug(this, "*******************************");

        
        objectDescriptor = initObjectDescriptor;        
        if (!shouldGenerate()) {
            Logger.debug(this, "should not generate");
            return;
        }
        model = objectDescriptor.getModel();
        
        code = new CodeBuffer();
        Logger.debug(this, "generate");
        genClassFileContent();
        Logger.debug(this, "done generate");
        
        //Logger.debug(this, code.toString());
        
        String relativePath = getPackageName().replaceAll("\\.", "/");
        String packagePath = rootPath + relativePath;
        String fileName = packagePath + "/" + getClassName() + ".java";
        Logger.debug(this, "packagePath: " + packagePath);
        Logger.debug(this, "fileName: " + fileName);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        FileUtil.writeFile(fileName, code.toString());

    }
    
    protected boolean shouldGenerate() {
        Logger.debug(this, "in base class shouldGenerate()");
        if (objectDescriptor.getImplementationName() == null) {
            return false;
        }
        Model model = objectDescriptor.getModel();
        
        String packagesToGenerate = model.getPackagesToGenerate();
        Assert.check(packagesToGenerate != null, "packagesToGenerate != null");
        
        String packageName = objectDescriptor.getImplementationName().getPackage(); 
        Assert.check(packageName != null, "packageName != null");        
        
        return packageName.startsWith(packagesToGenerate);
    }
    
    
    protected void genClassFileContent() {
        try {
            Assert.check(eventListener != null, "eventListener != null");
            generateHeader();
            generatePackage();
            generateImports();
            generateClassJavaDocs();        
            generateClass();        
            
        } catch (ReportedGeneratorEventException e) {
            String message = this.getClass().getName();
            message += " : Error generating content for: " + objectDescriptor.getTableName();
            GeneratorEventFacade.sendEvent(eventListener, GeneratorEventSeverityEnum.ERROR, message);
        } catch (Throwable e) {
            String message = this.getClass().getName();
            message += " : Error generating content for: " + objectDescriptor.getTableName();
            GeneratorEventFacade.sendEvent(eventListener, GeneratorEventSeverityEnum.ERROR, message);
            e.printStackTrace();
        }
        
    }

    /**
     *  Converts first letter of name to lower case and returns the result.
     */
    public String toJavaVariableName(String name) {
        Assert.check(name != null, "name != null");
        Assert.check(name.length() != 0, "name.length() != 0");
        
        if (name.length() == 1) {
            return name.toLowerCase();
        } else {
            return name.substring(0,1).toLowerCase() + name.substring(1);
        }
    }
    
    /**
     *  Converts a fully qualified class name to classname by removing the package name.
     *  TODO: should be static or private??
     */
    public String fullyQualifiedNameToClassName(String fqn) {
        return fqn.substring(fqn.lastIndexOf(".")+1);
    }
    
    /**
     *  Converts a fully qualified class name to package name by removing the class name.
     */
    public String fullyQualifiedNameToPackage(String fqn) {
        Logger.debug(this, "FQN: " + fqn);
        return fqn.substring(0, fqn.lastIndexOf("."));
    }
    
    protected String getClassName() {
        return fullyQualifiedNameToClassName(getFullyQualifiedName());
    }
    protected String getPackageName() {
        return fullyQualifiedNameToPackage(getFullyQualifiedName());
    }

    protected abstract String getFullyQualifiedName();

    protected abstract void generateHeader();
    protected abstract void generatePackage();
    protected abstract void generateImports();
    protected abstract void generateClassJavaDocs();
    protected abstract void generateClass();
    
    public void setEventListener(GeneratorEventListener newEventListener) {
        eventListener = newEventListener;
    }    
    
}
