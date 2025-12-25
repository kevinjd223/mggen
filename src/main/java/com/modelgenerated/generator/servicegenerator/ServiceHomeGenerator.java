/*
 * ServiceHomeGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.servicegenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.service.ServiceDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.java.CommentGenerator;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;

/**
 *
 * @author  kevind
 */
public class ServiceHomeGenerator {
    protected Model model;
    protected ServiceDescriptor serviceDescriptor;
    protected GeneratorConfig generatorConfig;
    protected CodeBuffer code;
    protected ClassDescriptor homeInterfaceClass;
    
    /** Creates a new instance of ActionGenerator */
    public ServiceHomeGenerator() {
    }
    
    public void generate(Model model, ServiceDescriptor serviceDescriptor) {
        Assert.check(model != null, "model != null");        
        Assert.check(serviceDescriptor != null, "serviceDescriptor != null");        

        this.model = model;        
        this.serviceDescriptor = serviceDescriptor;        

        
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        code = new CodeBuffer();
        homeInterfaceClass = new ClassDescriptor(serviceDescriptor.getHomeName());        
        generateFileContent();
        
        writeJavaFile(generatorConfig.getJavaCodeFolder(), homeInterfaceClass, code);
        
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
        code.addLine("/* " + homeInterfaceClass.getClassName());
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
        code.addLine();
        code.addLine("package " + homeInterfaceClass.getPackage() + ";");
        code.addLine();
    
    }
    
    protected void generateImports() {
    
        ImportGenerator importGenerator = new ImportGenerator();
        
        importGenerator.addImport("java.rmi.RemoteException");
        importGenerator.addImport("javax.ejb.CreateException");
        
        code.add(importGenerator.getImports(homeInterfaceClass.getPackage()));
        code.addLine();
    }
    protected void generateClassJavaDocs() {
        StringBuilder comment = new StringBuilder();
        ClassDescriptor serviceClass = new ClassDescriptor(serviceDescriptor.getEjbClassName());

        comment.append("Model generated EJB Home class for the EJB service class {@link ");
        comment.append(serviceClass.getFQN());
        comment.append(" ");
        comment.append(serviceClass.getClassName()); 
        comment.append("}.");
        code.addLine();
        CommentGenerator.writeJavaDocComment(code, "", comment.toString());
    }
    

    private void generateClass() {
        code.addLine("public interface " + homeInterfaceClass.getClassName() + " extends javax.ejb.EJBHome {");
        code.addLine();

        ClassDescriptor remoteInterfaceClass = new ClassDescriptor(serviceDescriptor.getRemoteName());
        code.addLine("    public " + remoteInterfaceClass.getClassName() + " create() throws RemoteException, CreateException;");

        code.addLine();
        code.addLine("}");
    }    
}
