/*
 * ServiceBeanBaseGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.servicegenerator;

import com.modelgenerated.foundation.EjbVersionEnum;
import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Model;
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

/**
 * Generates Service base class with Crud methods
 * 
 * @author  kevind
 */
public class ServiceCrudBaseGenerator {
    protected GeneratorConfig generatorConfig;
    protected CodeBuffer code;
    protected Model model;
    protected ServiceDescriptor serviceDescriptor;
    protected ClassDescriptor beanBaseClass;
    protected ClassDescriptor remoteInterfaceClass;
    
    /** Creates a new instance of ActionGenerator */
    public ServiceCrudBaseGenerator() {
    }
    
    public void generate(Model model, ServiceDescriptor serviceDescriptor) {
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        code = new CodeBuffer();
        this.model = model;        
        this.serviceDescriptor = serviceDescriptor;        

        beanBaseClass = new ClassDescriptor(serviceDescriptor.getEjbClassName());
        remoteInterfaceClass = new ClassDescriptor(serviceDescriptor.getRemoteName());
        
        generateFileContent();        

        writeJavaFile(generatorConfig.getJavaCodeFolder(), beanBaseClass, code);
        
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

        generateClass();
    }

    protected void generateHeader() {
        code.addLine("/* " + beanBaseClass.getClassName());
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
        code.addLine();
        code.addLine("package " + beanBaseClass.getPackage() + ";");
        code.addLine();
    
    }
    
    protected void generateImports() {
    
        ImportGenerator importGenerator = new ImportGenerator();
        
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessLocator");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.JitLoadingSwitchVisitor");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SearchCriteria");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        importGenerator.addImport("com.modelgenerated.foundation.identity.IdentityBuilder");

        if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
            //importGenerator.addImport("javax.ejb.EJB");
			//importGenerator.addImport("javax.ejb.Stateless");
			importGenerator.addImport("javax.ejb.TransactionAttribute");
			importGenerator.addImport("javax.ejb.TransactionAttributeType");
        } else {
            importGenerator.addImport("java.rmi.RemoteException");
            importGenerator.addImport("javax.ejb.SessionBean");
        }
        
        for (CrudObject crudObject : serviceDescriptor.getCrudObjects()) {
            ObjectDescriptor objectDescriptor = model.findObject(crudObject.getName());
            Assert.check(objectDescriptor != null, "objectDescriptor != null");
            
            ClassDescriptor importedClass = objectDescriptor.getValueObjectInterface();
            importGenerator.addImport(importedClass.getFQN());            
            
            importedClass = objectDescriptor.getDAOInterface();
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
        
        code.add(importGenerator.getImports(beanBaseClass.getPackage()));
        code.addLine();        
    }

    private void generateClass() {
    	// TODO: don't make it abstract if there are no methods
        code.add("public class " + beanBaseClass.getClassName());
        code.addLine("  {");

        for (CrudObject crudObject : serviceDescriptor.getCrudObjects()) {
            ObjectDescriptor objectDescriptor = model.findObject(crudObject.getName());
            Assert.check(objectDescriptor != null, "objectDescriptor != null");
            
            ClassDescriptor daoClassDescriptor = objectDescriptor.getDAOInterface();
            String daoClassName = daoClassDescriptor.getClassName();
            String daoVariableName = daoClassDescriptor.getJavaVariableName();
            
            ClassDescriptor valueObjectClassDescriptor = objectDescriptor.getValueObjectInterface();
            String valueObjectClassName = valueObjectClassDescriptor.getClassName();
            String valueObjectVariableName = valueObjectClassDescriptor.getJavaVariableName();

            ClassDescriptor listObjectClassDescriptor = objectDescriptor.getListInterface();
            String listObjectClassName = listObjectClassDescriptor.getClassName();
            String listObjectVariableName = listObjectClassDescriptor.getJavaVariableName();
            
            code.addLine("    // crud methods for " + valueObjectClassName);
            if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
            	code.addLine("    @TransactionAttribute(TransactionAttributeType.SUPPORTS)");
            }
        	//code.addLine("    @Override");
            code.addLine("    public " + valueObjectClassName + " new" + valueObjectClassName + "(UserContext userContext) {");
            code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
            code.addLine("        " + valueObjectClassName + " " + valueObjectVariableName +  " = (" + valueObjectClassName + ")" + daoVariableName + ".newValueObject();");
            code.addLine("        " + valueObjectVariableName + ".setUserContext(userContext);");
            code.addLine("        return " + valueObjectVariableName + ";");
            code.addLine("    }");
            code.addLine();

            if (crudObject.getGenerateListMethods()) {
                if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
                	code.addLine("    @TransactionAttribute(TransactionAttributeType.SUPPORTS)");
                }
            	//code.addLine("    @Override");
                code.addLine("    public " + listObjectClassName + " new" + listObjectClassName + "(UserContext userContext) {");
                code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
                code.addLine("        return (" + listObjectClassName + ")" + daoVariableName + ".newListObject();");
                code.addLine("    }");
                code.addLine();
            }
            
            if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
            	code.addLine("    @TransactionAttribute(TransactionAttributeType.SUPPORTS)");
            }
        	//code.addLine("    @Override");
            code.addLine("    public " + valueObjectClassName + " find" + valueObjectClassName + "(UserContext userContext, String idString)" + crudObject.getFindByIdThrowStatement() + " {");            
            code.addLine("        Identity id = IdentityBuilder.createIdentity(idString);");
            code.addLine("        return find" + valueObjectClassName + "(userContext, id);");
            code.addLine("    }");
            code.addLine();

            if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
            	code.addLine("    @TransactionAttribute(TransactionAttributeType.SUPPORTS)");
            }
        	//code.addLine("    @Override");
            code.addLine("    public " + valueObjectClassName + " find" + valueObjectClassName + "(UserContext userContext, Identity id)" + crudObject.getFindByIdThrowStatement() + " {");
            code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
            code.addLine("        return (" + valueObjectClassName + ")" + daoVariableName + ".find(userContext, id);");
            code.addLine("    }");
            code.addLine();

            if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
            	code.addLine("    @TransactionAttribute(TransactionAttributeType.REQUIRED)");
            }
        	//code.addLine("    @Override");
            code.addLine("    public void save" + valueObjectClassName + "(UserContext userContext, " + valueObjectClassName + " " + valueObjectVariableName + ") {");
            code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
            code.addLine("        JitLoadingSwitchVisitor.disableJitLoading(" + valueObjectVariableName + ");");            
            code.addLine("        " + daoVariableName + ".save(userContext, " + valueObjectVariableName + ");");
            code.addLine("    }");
            code.addLine();

            if (crudObject.getGenerateSearchMethod()) {
                if (EjbVersionEnum.EJB3 == serviceDescriptor.getEjbVersion()) {
                	code.addLine("    @TransactionAttribute(TransactionAttributeType.SUPPORTS)");
                }
            	//code.addLine("    @Override");
                code.addLine("    public " + listObjectClassName + " " + valueObjectVariableName + "Search(UserContext userContext, SearchCriteria searchCriteria) {");
                code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
                code.addLine("        return (" + listObjectClassName + ")" + daoVariableName + ".search(userContext, searchCriteria, false);");
                code.addLine("    }");
                code.addLine();
                code.addLine("    public int " + valueObjectVariableName + "SearchCount(UserContext userContext, SearchCriteria searchCriteria) {");
                code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class);");
                code.addLine("        return " + daoVariableName + ".searchCount(userContext, searchCriteria);");
                code.addLine("    }");
                code.addLine();
            }
        }        
        
        code.addLine("}");
    }
    
    

    
}
