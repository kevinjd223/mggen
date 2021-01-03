/*
 * TestDataGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.StringUtil;

/**
 *
 * @author  kevind
 */
public class TestDataGenerator extends JavaCodeBaseGenerator {
    /** Creates a new instance of DAOInterfaceGenerator */
    public TestDataGenerator() {
    }

    protected boolean shouldGenerate() {
        System.out.println("shouldGenerate: " + objectDescriptor.getValueObjectInterface().getClassName()); 
        if (!super.shouldGenerate()) {
            return false;
        }
        if (objectDescriptor.getTestDataClass() == null) {
        	return false;
        }
        
        return true;
    }
    
    protected void generateHeader() {
        code.addLine("/* " + getClassName() + ".java");
        code.addLine("* Generated DAO Test Code");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
        System.out.println("GenerateTestData: " + getClassName()); 
        
    }
    protected void generatePackage() {
        code.addLine();
        code.addLine("package " + getPackageName() + ";");
    }
    protected void generateImports() {
        Logger.debug(this, "generateImports()");

        ImportGenerator importGenerator = new ImportGenerator();

        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessLocator");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SubObjectHelper");
        importGenerator.addImport("com.modelgenerated.foundation.factory.Factory");
        importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        importGenerator.addImport("com.modelgenerated.foundation.identity.IdentityBuilder");
        importGenerator.addImport("com.modelgenerated.foundation.logging.Logger");
        
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE)) {
            importGenerator.addImport("com.modelgenerated.util.DateUtil");
        }
        /*
        Iterator i = objectDescriptor.getPersistedClassFieldClasses();
        while (i.hasNext()) {
            ClassDescriptor classDescriptor = (ClassDescriptor)i.next();            
            importGenerator.addImport(classDescriptor.getFQN());                    
            importGenerator.addImport(classDescriptor.getFQN() + "TestData");
            
        }
        */
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE)) {
            importGenerator.addImport("java.util.Date");
        }

        if (objectDescriptor.getPersisted()) {
            ClassDescriptor daoClass = objectDescriptor.getDAOInterface();
            importGenerator.addImport(daoClass.getFQN());
        }

        importGenerator.addImport(objectDescriptor.getValueObjectInterface().getFQN());            
		importGenerator.addImport(objectDescriptor.getListInterface().getFQN());            

		code.add(importGenerator.getImports(getPackageName()));
        code.addLine();        
        
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        Logger.debug(this, "generateClass()");
        code.addLine();
        code.addLine("public class " + getClassName() + " {");
        code.addLine();
        
        // add additional queries here
        generateConstructor();
        code.addLine();
        generateGetSample();
        /*
        don't create these methods. Make the tests use the service. 
        generateGetList();
        if (objectDescriptor.getPersisted()) {
            generateSave();
        }
        */
        
        code.addLine("}");

        Logger.debug(this, code.toString());

    }
    
    protected void generateConstructor() {
        code.addLine("    public " + getClassName() + "() {");
        code.addLine("    }");
        code.addLine();
    }
    
    protected void generateGetSample() {
        String valueObjectInterface = getValueObjectInterfaceName();
        String valueObjectVariable = toJavaVariableName(getValueObjectInterfaceName());
        
        code.addLine("    public static " + valueObjectInterface + " getSample(UserContext userContext) {");
        code.addLine();
        
        //Person person = (Person)Factory.createObject(Person.class.getName());
        code.add    ("        " + valueObjectInterface +" " + valueObjectVariable);
        code.addLine(" = (" + valueObjectInterface + ")Factory.createObject(" + valueObjectInterface + ".class.getName());");
        code.addLine("        " + valueObjectVariable + ".setUserContext(userContext);");
        code.addLine();        

        // create the object identity
        code.addLine("        Identity id = IdentityBuilder.createIdentity();");
        
        code.addLine("        " + valueObjectVariable + ".setId(id);");                    
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
            	// do nothing
            } else if (field.getType() == FieldTypeEnum.STRING) {
            	if (field.getSize() > field.getName().length() + 36) {
                    code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "(\"" + toJavaVariableName(field.getName()) +  "\" + id.toString());");            
            	} else {
                	if (field.getSize() < 10) {
                        code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "(\"" + toJavaVariableName(field.getName()).substring(0, field.getSize()) +  "\");");            
                	} else if (field.getSize() < 44) {
                        code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "((\"" + toJavaVariableName(field.getName()).substring(0, 3) +  "\" + id.toString()).substring(0, " + (field.getSize() - 3) + "));");            
                	} else {
                        code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "(\"" + toJavaVariableName(field.getName()).substring(0, 7) +  "\" + id.toString());");            
                	}
            	}
            		
            } else if (field.getType() == FieldTypeEnum.DATE) {
                code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "(DateUtil.parseDate(\"02/09/2000\"));");                
            } else if (field.getType() == FieldTypeEnum.BOOLEAN) {
                code.addLine("        " + valueObjectVariable + ".set" + field.getName() + "(Boolean.TRUE);");                
            }
        }


        code.addLine();        
        code.addLine("        return " + valueObjectVariable + ";");
        code.addLine("    }");    
    }
    
    protected void generateGetList() {
        String valueObjectListInterface = getValueObjectListInterfaceName();
        String valueObjectVariable = toJavaVariableName(getValueObjectListInterfaceName());
        
        code.addLine("    public static " + valueObjectListInterface + " getList() {");
        code.addLine();
        
        //Person person = (Person)Factory.createObject(Person.class.getName());
        code.add    ("        " + valueObjectListInterface +" " + valueObjectVariable);
        code.addLine(" = (" + valueObjectListInterface + ")Factory.createObject(" + valueObjectListInterface + ".class.getName());");
        code.addLine();        

        code.addLine("        return " + valueObjectVariable + ";");
        code.addLine("    }");    
    }
    
    protected void generateSave() {
        
        ClassDescriptor daoClassDescriptor = objectDescriptor.getDAOInterface();
        String daoClassName = daoClassDescriptor.getClassName();
        String daoVariableName = daoClassDescriptor.getJavaVariableName();
                
        ClassDescriptor valueObjectClassDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectClassName = valueObjectClassDescriptor.getClassName();
        String valueObjectVariableName = valueObjectClassDescriptor.getJavaVariableName();
    
        //ClassDescriptor listObjectClassDescriptor = objectDescriptor.getListInterface();
        //String listObjectClassName = listObjectClassDescriptor.getClassName();
        //String listObjectVariableName = listObjectClassDescriptor.getJavaVariableName();
    
        code.addLine("    public static void save(UserContext userContext, " + valueObjectClassName + " " + valueObjectVariableName + ") {");
        code.addLine("        " + daoClassName + " " + daoVariableName + "= (" + daoClassName + ")DataAccessLocator.findDAO(" + valueObjectClassName + ".class.getName());");
        code.addLine("        " + daoVariableName + ".save(userContext, " + valueObjectVariableName + ");");
        code.addLine("    }");
        code.addLine();
    }
    
    
    protected String getFullyQualifiedName() {
        return objectDescriptor.getTestDataClass().getFQN();
    }

    protected String getValueObjectInterfaceName() {
        return objectDescriptor.getValueObjectInterface().getClassName();
    }
    
    protected String getValueObjectListInterfaceName() {
        return objectDescriptor.getListInterface().getClassName();
    }
    
    
}
