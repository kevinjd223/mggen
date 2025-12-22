/*
 * ServiceInterfaceGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.servicegenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.generator.java.CommentGenerator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.Parameter;
import com.modelgenerated.modelmetadata.Prototype;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.modelmetadata.service.CrudObject;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class ServiceInterfaceGenerator {
    protected Model model;
    protected ServiceDescriptor serviceDescriptor;
    protected GeneratorConfig generatorConfig;
    protected CodeBuffer code;
    protected ClassDescriptor remoteInterfaceClass;
    
    /** Creates a new instance of ActionGenerator */
    public ServiceInterfaceGenerator() {
    }
    
    public void generate(Model model, ServiceDescriptor serviceDescriptor) {
        Assert.check(model != null, "model != null");        
        Assert.check(serviceDescriptor != null, "serviceDescriptor != null");        

        this.model = model;        
        this.serviceDescriptor = serviceDescriptor;        

        
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        code = new CodeBuffer();
        remoteInterfaceClass = new ClassDescriptor(serviceDescriptor.getRemoteName());
        
        generateFileContent();
        
        writeJavaFile(generatorConfig.getJavaCodeFolder(), remoteInterfaceClass, code);
        
    }
    
    
    protected void writeJavaFile(String baseFolder, ClassDescriptor classDescriptor, CodeBuffer codeBuffer) {
        String PATH_SEPARATOR = "/";
        
        String fullQualifiedClassName = classDescriptor.getFQN();
        String relativeClassName = fullQualifiedClassName.replaceAll("\\.", PATH_SEPARATOR);
        String fullyQualifiedPathAndFile = baseFolder + PATH_SEPARATOR + relativeClassName + ".java";

        Logger.debug(this, "fullQualifiedClassName: " + fullQualifiedClassName);
        Logger.debug(this, "relativeClassName: " + relativeClassName);
        Logger.debug(this, "fullyQualifiedPathAndFile: " + fullyQualifiedPathAndFile);
        
        String packagePath = StringUtil.fullyQualifiedNameToPackage(fullyQualifiedPathAndFile);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        FileUtil.writeFile(fullyQualifiedPathAndFile, codeBuffer.toString());

    }

    
    
    private void generateFileContent() {
        generateHeader();
        generateImports();     

        generateClassJavaDocs();
        generateClass();
    }

    protected void generateHeader() {
        code.addLine("/* " + remoteInterfaceClass.getClassName());
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
        code.addLine();
        code.addLine("package " + remoteInterfaceClass.getPackage() + ";");
        code.addLine();
    
    }
    
    protected void generateImports() {
    
        ImportGenerator importGenerator = new ImportGenerator();
        
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SearchCriteria");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");

        for (CrudObject crudObject : serviceDescriptor.getCrudObjects()) {
            ObjectDescriptor objectDescriptor = model.findObject(crudObject.getName());
            Assert.check(objectDescriptor != null, "objectDescriptor != null" + crudObject.getName());
            
            ClassDescriptor importedClass = objectDescriptor.getValueObjectInterface();
            importGenerator.addImport(importedClass.getFQN());
            
            if (crudObject.getGenerateListMethods() || crudObject.getGenerateSearchMethod()) {
                importedClass = objectDescriptor.getListInterface();
                importGenerator.addImport(importedClass.getFQN());
            }
            for (ClassDescriptor findException : crudObject.getFindByIdExceptionList()) {
                importGenerator.addImport(findException.getFQN());
            }
            for (ClassDescriptor saveException : crudObject.getSaveExceptionList()) {
                importGenerator.addImport(saveException.getFQN());
            }
        }
        
        for (Method method : serviceDescriptor.getMethods()) {
            Prototype prototype = method.getPrototype();
            ClassDescriptor returnType = prototype.getReturnType(); 

            importGenerator.addImport(returnType.getFQN());
            
            for (Parameter parameter : prototype.getParameters()) {
                ClassDescriptor parameterType = parameter.getType();
                importGenerator.addImport(parameterType.getFQN());
            }
            for (ClassDescriptor exception : prototype.getExceptionList()) {
                importGenerator.addImport(exception.getFQN());
            }
        }
        
        code.add(importGenerator.getImports(remoteInterfaceClass.getPackage()));
        code.addLine();
    }
    protected void generateClassJavaDocs() {
        CommentGenerator.writeJavaDocComment(code, "", serviceDescriptor.getDescription());
    }
    

    private void generateClass() {
        
    	code.add("public interface " + remoteInterfaceClass.getClassName());
    	code.addLine(" {");


        for (CrudObject crudObject : serviceDescriptor.getCrudObjects()) {
            ObjectDescriptor objectDescriptor = model.findObject(crudObject.getName());
            Assert.check(objectDescriptor != null, "objectDescriptor != null");
            
            ClassDescriptor valueObjectClassDescriptor = objectDescriptor.getValueObjectInterface();
            String valueObjectClassName = valueObjectClassDescriptor.getClassName();
            String valueObjectVariableName = valueObjectClassDescriptor.getJavaVariableName();

            ClassDescriptor listObjectClassDescriptor = objectDescriptor.getListInterface();
            String listObjectClassName = listObjectClassDescriptor.getClassName();
            String listObjectVariableName = listObjectClassDescriptor.getJavaVariableName();
            
            
            code.addLine();
            code.addLine("    // ****************************** ");
            code.addLine("    // crud methods for " + valueObjectClassName);
            code.addLine("    // ****************************** ");
            
            code.addLine("    /**");
            code.addLine("     * CRUD method to create a new " + valueObjectClassName + ".");
            code.addLine("     */");
            code.addLine("    " + valueObjectClassName + " new" + valueObjectClassName + "(UserContext userContext);");

            if (crudObject.getGenerateListMethods()) {
                code.addLine("    /**");
                code.addLine("     * CRUD method to create a new " + listObjectClassName + ".");
                code.addLine("     */");
                code.addLine("    " + listObjectClassName + " new" + listObjectClassName + "(UserContext userContext);");
            }

            code.addLine("    /**");
            code.addLine("     * CRUD method to read a " + valueObjectClassName + " by Id.");
            code.addLine("     */");
            code.addLine("    " + valueObjectClassName + " find" + valueObjectClassName + "(UserContext userContext, String idString)" + crudObject.getFindByIdThrowStatement() + ";");

            code.addLine("    /**");
            code.addLine("     * CRUD method to read a " + valueObjectClassName + " by Id as a String.");
            code.addLine("     */");
            code.addLine("    " + valueObjectClassName + " find" + valueObjectClassName + "(UserContext userContext, Identity id)" + crudObject.getFindByIdThrowStatement() + ";");

            code.addLine("    /**");
            code.addLine("     * CRUD method to update a " + valueObjectClassName + ".");
            code.addLine("     */");
            code.addLine("    void save" + valueObjectClassName + "(UserContext userContext, " + valueObjectClassName + " " + valueObjectVariableName + ");");
            if (crudObject.getGenerateSearchMethod()) {
                code.addLine("    /**");
                code.addLine("     * CRUD method to read/search for a list of " + valueObjectClassName + ".");
                code.addLine("     */");
                code.addLine("    " + listObjectClassName + " " + valueObjectVariableName + "Search(UserContext userContext, SearchCriteria searchCriteria);");
                code.addLine("    /**");
                code.addLine("     * CRUD method. Returns count of rows returned by searchCriterid.");
                code.addLine("     */");
                code.addLine("    int " + valueObjectVariableName + "SearchCount(UserContext userContext, SearchCriteria searchCriteria);");
            }
        }
        List<Method> methodList = serviceDescriptor.getMethods();
        if (methodList.size() > 0) {
            code.addLine();
            code.addLine("    // other methods ");
        }
        for (Method method : methodList) {
            CommentGenerator.writeJavaDocComment(code, "    ", method.getDescription());
            
            String prototype = method.getMethodNameAndParameters();
            code.add("    " + prototype);
        	List<ClassDescriptor> exceptionList = method.getPrototype().getExceptionList();
        	if (exceptionList.size() > 0) {
        		code.add(" throws ");
        		code.add(exceptionList.get(0).getClassName());
        		for (int exceptionIndex = 1; exceptionIndex < exceptionList.size(); exceptionIndex++) {
            		code.add(", ");
            		code.add(exceptionList.get(exceptionIndex).getClassName());
        		}
        	}
            code.addLine(";");
        }
        
        code.addLine();
        code.addLine("}");
    }
    
    
}
