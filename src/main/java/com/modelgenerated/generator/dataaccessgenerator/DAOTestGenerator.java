/*
 * DAOTestGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;


import com.modelgenerated.generator.GeneratorEventFacade;
import com.modelgenerated.generator.GeneratorEventSeverityEnum;
import com.modelgenerated.generator.ReportedGeneratorEventException;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;

/**
 *
 * @author  kevind
 */
public class DAOTestGenerator extends JavaCodeBaseGenerator {
    /** Creates a new instance of DAOInterfaceGenerator */
    public DAOTestGenerator() {
    }

    protected boolean shouldGenerate() {
        System.out.println("shouldGenerate: " + objectDescriptor.getValueObjectInterface().getClassName()); 
        if (!super.shouldGenerate()) {
            return false;
        }
        if (!objectDescriptor.getPersisted()) {
            return false;
        }
        
        return true;
    }
    
    protected void generateHeader() {
        code.addLine("/* " + getClassName() + ".java");
        code.addLine("* Generated DAO Test Code blah");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
        
        System.out.println("GenerateDAOTest: " + getClassName()); 

    }
    protected void generatePackage() {
        code.addLine();
        code.addLine("package " + getPackageName() + ";");
    }
    protected void generateImports() {
        Logger.debug(this, "generateImports()");
        
        ImportGenerator importGenerator = new ImportGenerator();
        
        //String valueObjectInterfacePackage = objectDescriptor.getValueObjectInterface().getPackage();
        importGenerator.addImport(objectDescriptor.getValueObjectInterface().getFQN());            

        //String listInterfacePackage = objectDescriptor.getListInterface().getPackage();
        importGenerator.addImport(objectDescriptor.getListInterface().getFQN());            
        
        
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessLocator");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SubObjectHelper");
        importGenerator.addImport("com.modelgenerated.foundation.factory.Factory");
        importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        importGenerator.addImport("com.modelgenerated.foundation.identity.IdentityBuilder");
        importGenerator.addImport("com.modelgenerated.foundation.logging.Logger");
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE) || objectDescriptor.hasFieldType(FieldTypeEnum.DATETIME)) {
            importGenerator.addImport("com.modelgenerated.util.DateUtil");
        }
        for (ClassDescriptor classDescriptor : objectDescriptor.getPersistedClassFieldClasses()) {
            
            ObjectDescriptor objectDescriptor = model.findObject(classDescriptor.getFQN());
            importGenerator.addImport(classDescriptor.getFQN());
            ClassDescriptor testDataClass = objectDescriptor.getTestDataClass();
            importGenerator.addImport(testDataClass.getFQN());                    
        }

        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS) {
                ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
                if (childObjectDesciptor != null) {
                    String daoInterface = childObjectDesciptor.getDAOInterface().getFQN();
                    Assert.check(daoInterface != null, "voInterface != null");
                    importGenerator.addImport(daoInterface);
                }
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                importGenerator.addImport(field.getClassDescriptor().getFQN());
            }
        }                
        
        importGenerator.addImport("java.text.ParseException");
        importGenerator.addImport("java.text.SimpleDateFormat");
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE) || objectDescriptor.hasFieldType(FieldTypeEnum.DATETIME)) {
            importGenerator.addImport("java.util.Date");
        }
        importGenerator.addImport("junit.framework.*");
        
        code.add(importGenerator.getImports(objectDescriptor.getDAOInterface().getPackage()));
        code.addLine();        
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        Logger.debug(this, "generateClass()");
        code.addLine();
        code.addLine("public class " + getClassName() + " extends TestCase {");
        code.addLine();
        
        // add additional queries here
        generateConstructor();
        generateMain();
        generateSuite();

        generateSaveTest();
        code.addLine();
        generateDeleteTest();
        code.addLine();
        generateEmptyTest();
        code.addLine();
        
        code.addLine("}");

        Logger.debug(this, code.toString());

    }
    
    protected void generateConstructor() {
        code.addLine("    public " + getClassName() + "(java.lang.String testName) {");
        code.addLine("        super(testName);");
        code.addLine("    }");
        code.addLine();
    }
    
    protected void generateMain() {
        code.addLine("    public static void main(java.lang.String[] args) {");
        code.addLine("        junit.textui.TestRunner.run(suite());");
        code.addLine("    }");
        code.addLine();
    }
    
    protected void generateSuite() {
        ClassDescriptor testClassDescriptor = objectDescriptor.getDAOTestClass();
        code.addLine("    public static Test suite() {");
        code.addLine("        TestSuite suite = new TestSuite(" + testClassDescriptor.getClassName() + ".class);");
        code.addLine("        return suite;");
        code.addLine("    }");
        code.addLine();
    }
    
    protected void generateSaveTest() {
        Logger.debug(this, "generateSaveTest()");
        code.addLine("    public void xtestSave() {");
        
        ClassDescriptor daoClassDescriptor = objectDescriptor.getDAOInterface();
        ClassDescriptor voClassDescriptor = objectDescriptor.getValueObjectInterface();
        // get a DAO interface
        code.add    ("        " + daoClassDescriptor.getClassName() + " " + daoClassDescriptor.getJavaVariableName());
        code.addLine(" = (" + daoClassDescriptor.getClassName() + ")DataAccessLocator.findDAO(" + voClassDescriptor.getClassName() + ".class.getName());");
        code.addLine();

        // create the object identity
        code.addLine("        Identity id = IdentityBuilder.createIdentity();");
        code.addLine("        UserContext userContext = new UserContext();");
        code.addLine("        userContext.setUserName(\"user\");");
        code.addLine();
        
        createVariables(1);
        code.addLine();
        
        String valueObjectVariable1 = voClassDescriptor.getJavaVariableName() + "1";
        //Person person = (Person)Factory.createObject(Person.class.getName());
        code.add    ("        " + voClassDescriptor.getClassName() +" " + valueObjectVariable1);
        code.addLine(" = (" + voClassDescriptor.getClassName() + ")Factory.createObject(" + voClassDescriptor.getClassName() + ".class.getName());");
        code.addLine();
        

        setValues(1);
        code.addLine("        Logger.debug(this, " + valueObjectVariable1 + ");");
        code.addLine();
        
        // personDAO.save(userContext, person);
        code.addLine("        " + daoClassDescriptor.getJavaVariableName() + ".save(userContext, " + valueObjectVariable1 + ");");
        code.addLine();
                

        String valueObjectVariable2 = voClassDescriptor.getJavaVariableName() + "2";
        //Person person2 = (Person)personDAO.find(userContext, id);
        code.add    ("        " + voClassDescriptor.getClassName() +"  " + valueObjectVariable2);
        code.addLine(" = (" + voClassDescriptor.getClassName() + ")" + daoClassDescriptor.getJavaVariableName() + ".find(userContext, id);");
        
        //Logger.debug(this, person2);
        code.addLine("        Logger.debug(this, " + valueObjectVariable2 + ");");
        code.addLine();

        
        createAsserts(1, 2);
        code.addLine();

        createVariables(2);
        code.addLine();
        
        setValues(2);
        
        // personDAO.save(userContext, person);
        code.addLine("        " + daoClassDescriptor.getJavaVariableName() +".save(userContext, " + valueObjectVariable2 + ");");
        code.addLine();
                

        String valueObjectVariable3 = voClassDescriptor.getJavaVariableName() + "3";
        //Person person2 = (Person)personDAO.find(userContext, id);
        code.add    ("        " + voClassDescriptor.getClassName() +"  " + valueObjectVariable3);
        code.addLine(" = (" + voClassDescriptor.getClassName() + ")" + daoClassDescriptor.getJavaVariableName() + ".find(userContext, id);");

        //Logger.debug(this, person2);
        code.addLine("        Logger.debug(this, " + valueObjectVariable3 + ");");
        code.addLine();
        
        createAsserts(2, 3);
        code.addLine();

        
        code.addLine("    }");
        code.addLine();
    }

    private void setValues(int suffix) {
        code.addLine("        " + toJavaVariableName(getValueObjectInterfaceName()) + suffix + ".setId(id);");                    
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
            	// do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else {
                code.addLine("        " + toJavaVariableName(getValueObjectInterfaceName()) + suffix + ".set" + field.getName() + "(" + toJavaVariableName(field.getName()) + suffix + ");");            
            }
        }
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            for (FieldDescriptor field : baseObjectDescriptor.getPersistedFields()) {
                code.addLine("        " + toJavaVariableName(getValueObjectInterfaceName()) + suffix + ".set" + field.getName() + "(" + toJavaVariableName(field.getName()) + suffix + ");");            
            }
        }

    }

    private void createVariables(int suffix) {
        int integerValue = 42 + suffix;
        boolean booleanValue = true;
        // create variables for the initial object attributes
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
            	// do nothing
            } else if (field.getType() == FieldTypeEnum.STRING) {
                String value = field.getName() + suffix;
                if (value.length() > field.getSize()) {
                    value = value.substring(0, field.getSize()-1) + suffix;
                }
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = \"" + value + "\";");
                
            } else if (field.getType() == FieldTypeEnum.TEXT) {
                String value = field.getName() + suffix;
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = \"" + value + "\";");
                
            } else if (field.getType() == FieldTypeEnum.DATE) {
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = DateUtil.parseDate(\"02/09/2000\");");
                
            } else if (field.getType() == FieldTypeEnum.DATETIME) {
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = DateUtil.parseDate(\"09/01/2000\");");
                
            } else if (field.getType() == FieldTypeEnum.DOUBLE) {
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = new Double(123.456);");
                
            } else if (field.getType() == FieldTypeEnum.BOOLEAN) {
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = new Boolean(" + booleanValue + ");");
                booleanValue = !booleanValue;
			} else if (field.getType() == FieldTypeEnum.INT) {
				code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = " + integerValue++ + ";");
                
			} else if (field.getType() == FieldTypeEnum.INTEGER) {
				code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = new Integer(" + integerValue++ + ");");
                
            } else if (field.getType() == FieldTypeEnum.CLASS) {
                if (field.getUseInTest()) {
                    ObjectDescriptor objectDescriptor = model.findObject(field.getClassDescriptor().getFQN());
                    ClassDescriptor testDataClass = objectDescriptor.getTestDataClass();
                    code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = " + testDataClass.getClassName() + ".getSample();");
                }
                
            } else if (field.getType() == FieldTypeEnum.IDENTITY) {
                code.addLine("        //undone: generate object");
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = IdentityBuilder.createIdentity();");
                
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                // TODO
                code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = null;");

            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else {
                String message = "Invalid Type. Class: " + objectDescriptor.getValueObjectInterface().getFQN() + " Field: " + field.getName() + " Type: " + field.getType();
                GeneratorEventFacade.sendEvent(eventListener, GeneratorEventSeverityEnum.ERROR, message);
                throw new ReportedGeneratorEventException(message);
            }            
        }
        // base class 
        //UNDONE: this is duplicate.
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            for (FieldDescriptor field : baseObjectDescriptor.getPersistedFields()) {
                if (!StringUtil.isEmpty(field.getSql())) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.STRING) {
                    code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = \"" + field.getName() + suffix + "\";");

                } else if (field.getType() == FieldTypeEnum.DATE) {
                    code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = DateUtil.parseDate(\"02/09/2000\");");
                    
                } else if (field.getType() == FieldTypeEnum.DATETIME) {
                    code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = DateUtil.parseDate(\"09/01/2000\");");

				} else if (field.getType() == FieldTypeEnum.INT) {
					code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = " + integerValue++ + ";");

				} else if (field.getType() == FieldTypeEnum.INTEGER) {
					code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = " + integerValue++ + ";");

                } else if (field.getType() == FieldTypeEnum.CLASS) {
                    if (field.getUseInTest()) {
                        code.addLine("        " + field.getJavaType() + " " + toJavaVariableName(field.getName()) + suffix + " = " + field.getJavaType() + "TestData.getSample();");
                    }
                } else if (field.getType() == FieldTypeEnum.ENUM) {
                    // TODO
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    // do nothing
                } else {
                    String message = "Invalid Type. Class: " + objectDescriptor.getValueObjectInterface().getFQN() + " Field: " + field.getName() + " Type: " + field.getType();
                    GeneratorEventFacade.sendEvent(eventListener, GeneratorEventSeverityEnum.ERROR, message);
                    throw new ReportedGeneratorEventException(message);
                }            
            }
        }
    }
    
    private void createAsserts(int suffix1, int suffix2) {
        code.addLine("        assertTrue(\"id should equal id\", " + toJavaVariableName(getValueObjectInterfaceName()) + suffix2 + ".getId().equals(id));");
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            String varObject = toJavaVariableName(getValueObjectInterfaceName());
            String varField = toJavaVariableName(field.getName());

            if (!StringUtil.isEmpty(field.getSql())) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.CLASS) { 
                if (field.getUseInTest()) {
                    code.addLine("        assertTrue(\"" + varField + "Id should equal " + varField + "Id" + suffix1 + "\", SubObjectHelper.getSubObjectId(" + varObject + suffix2 + ", \"" + field.getName() + "Id\").equals(" + varField + suffix1 + ".getId()));");
                }
            } else if (field.getType() == FieldTypeEnum.INT) {
                code.addLine("        assertTrue(\"" + varField + " should equal " + varField + suffix1 + "\", " + varObject + suffix2 + ".get" + field.getName() + "() == " + varField + suffix1 + ");");
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                // TODO
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else {                 
                code.addLine("        assertTrue(\"" + varField + " should equal " + varField + suffix1 + "\", " + varObject + suffix2 + ".get" + field.getName() + "().equals(" + varField + suffix1 + "));");
            }
        }
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            for (FieldDescriptor field : baseObjectDescriptor.getPersistedFields()) {
                String varObject = toJavaVariableName(getValueObjectInterfaceName());
                String varField = toJavaVariableName(field.getName());

                if (!StringUtil.isEmpty(field.getSql())) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.CLASS) {                     
                    if (field.getUseInTest()) {
                        code.addLine("        assertTrue(\"" + varField + "Id should equal " + varField + "Id" + suffix1 + "\", SubObjectHelper.getSubObjectId(" + varObject + suffix2 + ", \"" + field.getName() + "Id\").equals(" + varField + suffix1 + ".getId()));");
                    }
                } else if (field.getType() == FieldTypeEnum.ENUM) {
                    // TODO
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    // do nothing
                } else {
                    code.addLine("        assertTrue(\"" + varField + " should equal " + varField + suffix1 + "\", " + varObject + suffix2 + ".get" + field.getName() + "().equals(" + varField + suffix1 + "));");
                }
            }
        }        
        
    }

    protected void generateDeleteTest() {
        code.addLine("    public void xtestDelete() {");
        
        ClassDescriptor daoClassDescriptor = objectDescriptor.getDAOInterface();
        // get a DAO interface
        code.add    ("        " + daoClassDescriptor.getClassName() +" " + daoClassDescriptor.getJavaVariableName());
        code.addLine(" = (" + daoClassDescriptor.getClassName() + ")DataAccessLocator.findDAO(" + getValueObjectInterfaceName() + ".class.getName());");
        code.addLine();

        // create the object identity
        code.addLine("        Identity id = IdentityBuilder.createIdentity();");
        code.addLine("        UserContext userContext = new UserContext();");
        code.addLine("        userContext.setUserName(\"user\");");
        code.addLine();
        
        createVariables(1);
        code.addLine();
        
        String valueObjectVariable1 = toJavaVariableName(getValueObjectInterfaceName()) + "1";
        //Person person = (Person)Factory.createObject(Person.class.getName());
        code.add    ("        " + getValueObjectInterfaceName() +" " + valueObjectVariable1);
        code.addLine(" = (" + getValueObjectInterfaceName() + ")Factory.createObject(" + getValueObjectInterfaceName() + ".class.getName());");
        code.addLine();
        

        setValues(1);
        code.addLine("        Logger.debug(this, " + valueObjectVariable1 + ");");
        code.addLine();
        
        // personDAO.save(userContext, person);
        code.addLine("        " + daoClassDescriptor.getJavaVariableName() +".save(userContext, " + valueObjectVariable1 + ");");
        code.addLine();
                

        String valueObjectVariable2 = toJavaVariableName(getValueObjectInterfaceName()) + "2";
        //Person person2 = (Person)personDAO.find(userContext, id);
        code.add    ("        " + getValueObjectInterfaceName() +"  " + valueObjectVariable2);
        code.addLine(" = (" + getValueObjectInterfaceName() + ")" + daoClassDescriptor.getJavaVariableName() + ".find(userContext, id);");
        code.addLine("        assertTrue(\"" + valueObjectVariable2 + " should not be null\", " + valueObjectVariable2 + " != null);");
        
        //Logger.debug(this, person2);
        code.addLine("        Logger.debug(this, " + valueObjectVariable2 + ");");
        code.addLine();

    
        code.addLine("        " + valueObjectVariable2 + ".setIsDeleted(true);");
        code.addLine();

        // personDAO.save(userContext, person2);
        code.addLine("        " + daoClassDescriptor.getJavaVariableName() +".save(userContext, " + valueObjectVariable2 + ");");
        code.addLine();

        String valueObjectVariable3 = toJavaVariableName(getValueObjectInterfaceName()) + "3";
        //Person person2 = (Person)personDAO.find(userContext, id);
        code.add    ("        " + getValueObjectInterfaceName() +"  " + valueObjectVariable3);
        code.addLine(" = (" + getValueObjectInterfaceName() + ")" + daoClassDescriptor.getJavaVariableName() + ".find(userContext, id);");
        code.addLine("        assertTrue(\"" + valueObjectVariable3 + " should be null\", " + valueObjectVariable3 + " == null);");
        
        
        code.addLine("    }");
    
    }
    
    protected void generateEmptyTest() {
        code.addLine("    public void testEmptyTest() {");        
        code.addLine("        // empty tests to prevent failure because of no tests");        
        code.addLine("    }");
    
    }
    
    protected String getFullyQualifiedName() {
        return objectDescriptor.getDAOTestClass().getFQN();
    }

    protected String getValueObjectInterfaceName() {
        return objectDescriptor.getValueObjectInterface().getClassName();
    }
    
}
