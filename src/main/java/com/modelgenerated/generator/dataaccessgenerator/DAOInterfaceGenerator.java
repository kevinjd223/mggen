/*
 * DAOInterfaceGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;


import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.QueryDescriptor;
import com.modelgenerated.modelmetadata.QueryTypeEnum;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;

/**
 *
 * @author  kevind
 */
public class DAOInterfaceGenerator extends JavaCodeBaseGenerator {
    /** Creates a new instance of DAOInterfaceGenerator */
    public DAOInterfaceGenerator() {
    }

    protected boolean shouldGenerate() {
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
        code.addLine("* Generated value object code");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
    }
    protected void generatePackage() {
        code.addLine();
        code.addLine("package " + getPackageName() + ";");
    }
    protected void generateImports() {
        code.addLine();
        
        String valueObjectInterfacePackage = objectDescriptor.getValueObjectInterface().getPackage();
        if (!valueObjectInterfacePackage.equals(getPackageName())) {
            code.addLine("import " + objectDescriptor.getValueObjectInterface().getFQN() + ";");            
        }
        String listInterfacePackage = objectDescriptor.getListInterface().getPackage();
        if (!listInterfacePackage.equals(getPackageName())) {
            code.addLine("import " + objectDescriptor.getListInterface().getFQN() + ";");            
        }
        
        code.addLine("import com.modelgenerated.foundation.dataaccess.DataAccessObject;");
        code.addLine("import com.modelgenerated.foundation.dataaccess.SearchCriteria;");
        code.addLine("import com.modelgenerated.foundation.dataaccess.TransactionContext;");
        code.addLine("import com.modelgenerated.foundation.dataaccess.UserContext;");
        code.addLine("import com.modelgenerated.foundation.dataaccess.ValueObject;");
        code.addLine("import com.modelgenerated.foundation.identity.Identity;");
        
        Model model = objectDescriptor.getModel();        
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            // todo: shouldn't be using persisted for this?
            if (field.getType() == FieldTypeEnum.CLASS) {
                ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
                if (childObjectDesciptor != null) {
                    String voInterface = childObjectDesciptor.getValueObjectInterface().getFQN();
                    Assert.check(voInterface != null, "voInterface != null");
                    code.addLine("import " + voInterface + ";");            
                }
            }
        }                
        code.addLine("import java.util.Map;");
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        code.addLine();
        code.addLine("public interface " + getClassName() + " extends DataAccessObject {");
        
        // add additional queries here
        generateQueries(objectDescriptor);
        code.addLine();
        generateSearch();
        code.addLine();
        
        code.addLine("}");
    }
    
    private void generateQueries(ObjectDescriptor objectDescriptor) {
        
        for (QueryDescriptor query : objectDescriptor.getQueries()) {
            Assert.check(query.getType() != null, "query.getType() != null");
            generateFindByQuery(objectDescriptor, query);
        }
        
        
    }
    
    private void generateFindByQuery(ObjectDescriptor objectDescriptor, QueryDescriptor query) {
        FieldDescriptor field = objectDescriptor.findField(query.getFieldName()); 
        Assert.check(field != null, "field != null, object name:" + objectDescriptor.getTableName() + ", fieldname:" + query.getFieldName());
        Logger.debug(this, field);
        
        // UNDONE: pass the field Object
        String methodName = query.getMethodName();
        ClassDescriptor classDescriptor = field.getClassDescriptor();        
        Assert.check(classDescriptor != null, "classDescriptor != null");
        Logger.debug(this, classDescriptor);
        String varName = field.getJavaVariableName();

        String valueObjectInterfaceName = objectDescriptor.getValueObjectInterface().getClassName();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        String returnType = null;
        if (query.getType() == QueryTypeEnum.FINDBY_SINGLE) { 
            returnType = valueObjectInterfaceName;
        } else if (query.getType() == QueryTypeEnum.FINDBY_MULTI) { 
            returnType = listInterfaceName;
        } else {            
            Assert.check(false, "unsupported QueryTypeEnum");
        }
        
        code.addLine("    public " + returnType + " " + methodName + "(UserContext userContext, " + field.getJavaType() + " " + varName + ");");
        code.addLine("    public " + returnType + " " + methodName + "(TransactionContext transactionContext, " + field.getJavaType() + " " + varName + ");");
        code.addLine("    public " + returnType + " " + methodName + "(TransactionContext transactionContext, " + field.getJavaType() + " " + varName + ", Map<Identity,ValueObject> loadedObjects);");
        
        
    }
    private void generateSearch() {
        //ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();

        code.addLine("    public " + listInterfaceName + " search(UserContext userContext, SearchCriteria searchCriteria, boolean deepCopy);");
        code.addLine("    public " + listInterfaceName + " search(TransactionContext transactionContext, SearchCriteria searchCriteria, boolean deepCopy);");
    }

    
    protected String getFullyQualifiedName() {
        return objectDescriptor.getDAOInterface().getFQN();
    }
    
}
