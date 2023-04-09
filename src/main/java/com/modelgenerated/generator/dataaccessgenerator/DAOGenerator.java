/*
 * DAOGenerator.java
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
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.JoinDescriptor;
import com.modelgenerated.modelmetadata.JoinList;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.QueryDescriptor;
import com.modelgenerated.modelmetadata.QueryTypeEnum;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.modelmetadata.ReferenceTypeEnum;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class DAOGenerator extends JavaCodeBaseGenerator {
    // SQL-Server and mysql use different characters to encapsulate 
	// sql server
	//public static final String OPENENCAPSULATE = "[";
	//public static final String CLOSEENCAPSULATE = "]";
	// mysql
	//public static final String OPENENCAPSULATE = "`";
	//public static final String CLOSEENCAPSULATE = "`";
	// don't allow reserved words as table names
	public static final String OPENENCAPSULATE = "";
	public static final String CLOSEENCAPSULATE = "";

	public static String openEncapsulate = OPENENCAPSULATE;
	public static String closeEncapsulate = CLOSEENCAPSULATE;
	
	
    /** Creates a new instance of ValueObjectGenerator */
    public DAOGenerator() {
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
        code.addLine("* Generated Data Access Object Implementation");
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
        
		ImportGenerator importGenerator = new ImportGenerator();
        
        String valueObjectInterfacePackage = objectDescriptor.getValueObjectInterface().getPackage();
        if (!valueObjectInterfacePackage.equals(getPackageName())) {
			importGenerator.addImport(objectDescriptor.getValueObjectInterface().getFQN());            
        }
        String daoInterfacePackage = objectDescriptor.getDAOInterface().getPackage();
        if (!daoInterfacePackage.equals(getPackageName())) {
			importGenerator.addImport(objectDescriptor.getDAOInterface().getFQN());            
        }
        String listInterfacePackage = objectDescriptor.getListInterface().getPackage();
        if (!listInterfacePackage.equals(getPackageName())) {
			importGenerator.addImport(objectDescriptor.getListInterface().getFQN());            
        }
        for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
            ObjectDescriptor childObjectDesciptor = referenceDescriptor.getTargetObjectDescriptor();
            String daoInterface = childObjectDesciptor.getDAOInterface().getFQN();
            String voInterface = childObjectDesciptor.getValueObjectInterface().getFQN();
			String listInterface = childObjectDesciptor.getListInterface().getFQN();
			importGenerator.addImport(daoInterface);            
			importGenerator.addImport(voInterface);            
			importGenerator.addImport(listInterface);            
        }                

        //Logger.debug(this, "generating imports for: " + objectDescriptor.getImplementationName().getFQN()); 
        //System.out.println("generating imports for: " + objectDescriptor.getImplementationName().getFQN()); 
        Model model = objectDescriptor.getModel();
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
        
		importGenerator.addImport("com.modelgenerated.audit.Audit");
        importGenerator.addImport("com.modelgenerated.audit.AuditUtil");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.AbstractDataAccessObject");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessLocator");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessException");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessExceptionDuplicate");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.JDBCUtil");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.JitLoadingSwitchVisitor");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ObjectNotLoadedException");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ResultSetWrapper");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SearchCriteria");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SearchCriteriaBase");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.SubObjectHelper");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.TransactionContext");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ValueObject");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ValueObjectList");
        importGenerator.addImport("com.modelgenerated.foundation.factory.Factory");
        importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        importGenerator.addImport("com.modelgenerated.foundation.identity.IdentityBuilder");
        importGenerator.addImport("com.modelgenerated.foundation.logging.Logger");
        importGenerator.addImport("com.modelgenerated.util.Assert");
        importGenerator.addImport("com.modelgenerated.util.StringUtil");
        importGenerator.addImport("java.sql.Connection");
        importGenerator.addImport("java.sql.PreparedStatement");
        importGenerator.addImport("java.sql.ResultSet");
        importGenerator.addImport("java.sql.SQLException");        
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE)
            ||objectDescriptor.hasFieldType(FieldTypeEnum.DATETIME)) {
            importGenerator.addImport("java.util.Date");
        }
        importGenerator.addImport("java.util.HashMap");
        importGenerator.addImport("java.util.Iterator");
        importGenerator.addImport("java.util.Map");
        
        for (ClassDescriptor classDescriptor : objectDescriptor.getClassFieldClasses()) {
            importGenerator.addImport(classDescriptor.getFQN());                    
        }
        for (ClassDescriptor classDescriptor : objectDescriptor.getReferencedClasses()) {
            importGenerator.addImport(classDescriptor.getFQN());  
        }
        
		code.add(importGenerator.getImports(objectDescriptor.getDAOImplementation().getPackage()));
		code.addLine();        
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        code.addLine();
        code.addLine("public class " + getClassName() + " extends AbstractDataAccessObject implements " + objectDescriptor.getDAOInterface().getClassName() + " {");

        
        generateVariables();
        generateConstructor();
        
        generateNewValueObject();
        code.addLine();
        generateNewListObject();        
        code.addLine();
        
        FieldDescriptor idField = new FieldDescriptor(null);
        idField.setName("id");
        idField.setColumnName("id");
        idField.setType(FieldTypeEnum.IDENTITY);
        idField.setClassDescriptor(new ClassDescriptor("com.modelgenerated.foundation.identity.Identity"));
        generateFind("find", idField, QueryTypeEnum.FINDBY_SINGLE, "ValueObject", null);
        generateFindForUpdate();
        generateGetListFromResultSet();
        code.addLine();
        generatePreSaveNew();
        code.addLine();
        generatePreSaveExisting();
        code.addLine();
        generateSaveNew();
        code.addLine();
        generateSaveNewAudit();
        code.addLine();
        generateSaveExisting();
        code.addLine();
        generateSaveExistingAudit();
        code.addLine();
        generateSaveDelete();
        code.addLine();
        generateSaveDeleteAudit();
        code.addLine();
        generateQueries(objectDescriptor);
        code.addLine();
        generateUserContextSearch();
        code.addLine();
        generateSearch();
        code.addLine();
        generateUserContextSearchCount();
        code.addLine();
        generateSearchCount();
        code.addLine();
        generateLoadChildrenMethod();
        code.addLine();
        generateSaveChildrenMethod();
        
        code.addLine("}");
    }
    
    private void generateVariables() {
        code.addLine("    String sqlSelect;");        
        code.addLine("    String sqlSelectWithoutJoin;");        
        code.addLine("    String sqlInsert;");        
        code.addLine("    String sqlUpdate;");        
        code.addLine();        
    }
    private void generateConstructor() {

        code.addLine("    public " + getClassName() + "() {");
        code.addLine("        Logger.debug(this, \"XXXXXXXXXXXXXXXXXXXXXX Constructor: " + getClassName() + "\");");
        code.addLine("        StringBuilder sql = new StringBuilder();");        
        generateSelect(true);
        code.addLine("        sqlSelect = sql.toString();");        
        code.addLine();        

        code.addLine("        sql = new StringBuilder();");        
        generateSelect(false);
        code.addLine("        sqlSelectWithoutJoin = sql.toString();");        
        code.addLine();        

        code.addLine("        sql = new StringBuilder();");        
        generateInsert();
        code.addLine("        sqlInsert = sql.toString();");        
        code.addLine();        
        
        code.addLine("        sql = new StringBuilder();");        
        generateUpdate();
        code.addLine("        sqlUpdate = sql.toString();");        
        code.addLine();        
        
        code.addLine("    }");        
    }
    private void generateSelect(boolean withJoins) {
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        
        String objectTableAlias = objectDescriptor.getTableAlias();
        if (objectTableAlias == null) {
            objectTableAlias = "obj";
        }
        String baseTableAlias = "base";
        if (baseObjectDescriptor != null) {
            if (baseObjectDescriptor.getTableAlias() != null) {
                baseTableAlias = baseObjectDescriptor.getTableAlias();;
            }
        }
        
        code.add("        sql.append(\"" + objectTableAlias + ".id");
        
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine(",\");");
            code.addLine("        sql.append(\"" + objectTableAlias + ".createdDate,\");");
            code.addLine("        sql.append(\"" + objectTableAlias + ".createdBy,\");");
            code.addLine("        sql.append(\"" + objectTableAlias + ".modifiedDate,\");");
            code.add("        sql.append(\"" + objectTableAlias + ".modifiedBy");
        }
        
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
                if (withJoins) { 
	                code.addLine(",\");");
	                code.add("        sql.append(");
	                String[] strLines = field.getSql().split("\n");
	                strLines = removeEmptyLines(field.getSql().split("\n"));
	                for (int index = 0; index < strLines.length; index++) {
		                if (index > 0) {
	                		code.add("\t\t+ ");
	                    }
	            		code.add("\"");                	
	            		code.add(strLines[index]);                	
		                if (index < strLines.length-1) {
		                	code.addLine("\"");
		                }
		            }
                } else {
                    code.addLine(",\");");
                    code.add("        sql.append(\"null");            
                }
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
            	// Do nothing. We'll add the joins later. 
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                if (withJoins) { 
	            	Logger.debug(this, "field.getCoalesce1():" + field.getCoalesce1());
	            	Logger.debug(this, "field.getCoalesce2():" + field.getCoalesce2());
	            	FieldDescriptor coalesceField1 = objectDescriptor.findField(field.getCoalesce1());
	            	FieldDescriptor coalesceField2 = objectDescriptor.findField(field.getCoalesce2());
	            	
	                String alias1 = coalesceField1.getAlias();                    
	                String alias2 = coalesceField2.getAlias();                    
	                code.addLine(",\");");
	                code.add("        sql.append(\"coalesce(" + alias1 + "." + coalesceField1.getRealColumnName());            
	                code.add("," + alias2 + "." + coalesceField2.getRealColumnName());            
	                code.add(") ");            
	                code.add(field.getName());            
                } else {
                    code.addLine(",\");");
                    code.add("        sql.append(\"null");            
                }
            } else {
                code.addLine(",\");");
                code.add("        sql.append(\"" + objectTableAlias + "." + field.getRealColumnName());            
            }
            
        }       
        if (baseObjectDescriptor != null) {
            for (FieldDescriptor field : baseObjectDescriptor.getPersistedFields()) {
                if (field.getType() == FieldTypeEnum.CLASS) {
                    code.addLine(",\");");
                    code.add("        sql.append(\"" + baseTableAlias + "." + field.getName() + "Id");
                } else {
                    code.addLine(",\");");
                    code.add("        sql.append(\"" + baseTableAlias + "." + field.getName());
                }
            }
        }
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
            	// do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                code.addLine(",\");");
                if (withJoins) { 
                    //String joinFieldName = field.getJoinField();
                    //FieldDescriptor joinField = objectDescriptor.findField(joinFieldName);
                    //ObjectDescriptor joinedObject = getObjectDescriptor(joinedField.getClassDescriptor().getFQN());
                    String alias = field.getAlias();                    
                    code.add("        sql.append(\"" + alias + "." + field.getRealColumnName());            
                } else {            
                    FieldDescriptor joinedField = field.getJoinedFieldDescriptor();
                    if (joinedField == null) {
                    	String message = "Error generating DAO " + objectDescriptor.getDAOImplementation() 
                    		+ " join field " + field.getName() + "not found"; 
                        GeneratorEventFacade.sendEvent(eventListener, GeneratorEventSeverityEnum.ERROR, message);
                        throw new ReportedGeneratorEventException(message);
                    }
                    if (joinedField.getType() == FieldTypeEnum.DATE || joinedField.getType() == FieldTypeEnum.DATETIME) {
                        code.add("        sql.append(\"cast(null as DATETIME)");            
                    } else {
                        code.add("        sql.append(\"null");            
                    }
                }
            }            
        }               
        code.addLine(" \");");            
    }

    private String[] removeEmptyLines(String[] inputArray) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < inputArray.length; index++) {
            if (!StringUtil.allWhiteSpace(inputArray[index])) {
            	list.add(inputArray[index]);
            }
        }
        return list.toArray(new String[0]);
    }
    
    
    
    private void generateInsert() {
        code.add    ("        sql.append(\"insert " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + " (id");
        if (objectDescriptor.getMultiTenant()) {
            code.addLine(",\");");
            code.add("        sql.append(\"tid");
        }
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine(",\");");
            code.addLine("        sql.append(\"createdDate,\");");
            code.addLine("        sql.append(\"createdBy,\");");
            code.addLine("        sql.append(\"modifiedDate,\");");
            code.add    ("        sql.append(\"modifiedBy");
        }
        
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
            	// do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else {
                code.addLine(",\");");
                code.add("        sql.append(\"" + field.getColumnName());
            }
        }        
        code.addLine(")\");");
        
        code.add("        sql.append(\"values (?");        
        if (objectDescriptor.getMultiTenant()) {
            code.add(", ?");
        }
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.add(", ?, ?, ?, ?");
        }
        Iterator<FieldDescriptor> i = objectDescriptor.getPersistedFields().iterator();
        i.hasNext(); // Skip the first persisted field?? This is Id I guess??
        while (i.hasNext()) {
            FieldDescriptor field = (FieldDescriptor)i.next();
            if (!StringUtil.isEmpty(field.getSql())) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else {
                code.add(", ");
                code.add("?");
            }
        }        
        code.addLine(")\");");
    }
    
    private void generateUpdate() {
        code.addLine("        sql.append(\"update " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + " set \");");
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine("        sql.append(\"createdDate = ?,\");");                    
            code.addLine("        sql.append(\"createdBy = ?,\");");                    
            code.addLine("        sql.append(\"modifiedDate = ?,\");");                    
            code.addLine("        sql.append(\"modifiedBy = ?,\");");                    
        }
        
        int index = 0;
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else {
                if (index++ != 0) {
                    code.addLine(" = ?,\");");                    
                }
                code.add("        sql.append(\"" + field.getColumnName());
            }
        }
        code.addLine(" = ? \");");
        
        code.addLine("        sql.append(\"where id = ?\");");
    }
    
    private void generateNewValueObject() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectVariable = toJavaVariableName(valueObjectInterface);
        
        code.addLine("    public ValueObject newValueObject() {");        
        code.addLine("        " + valueObjectInterface + " " + valueObjectVariable + " = (" + valueObjectInterface + ")Factory.createObject(" + valueObjectInterface + ".class);");
        code.addLine("        Identity id = IdentityBuilder.createIdentity();");
        code.addLine("        " + valueObjectVariable + ".setId(id);");
        code.addLine("        return " + valueObjectVariable + ";");
        code.addLine("    }");
    }
    
    private void generateNewListObject() {
        ClassDescriptor listObjectDescriptor = objectDescriptor.getListInterface();
        String listInterface = listObjectDescriptor.getClassName();
        String listVariable = toJavaVariableName(listInterface);
        
        code.addLine("    public ValueObjectList newListObject() {");        
        code.addLine("        " + listInterface + " " + listVariable + " = (" + listInterface + ")Factory.createObject(" + listInterface + ".class);");
        code.addLine("        return " + listVariable + ";");
        code.addLine("    }");
    }

    private void generateFind(String methodName, FieldDescriptor findField, QueryTypeEnum queryType, String returnType, String orderBy) {
        //ClassDescriptor classDescriptor = findField.getClassDescriptor();        
        //Assert.check(classDescriptor != null, "classDescriptor != null");
        //Logger.debug(this, classDescriptor);
        String varName = findField.getJavaVariableName();
        Assert.check(varName != null, "varName != null");
        String columnName = findField.getColumnName();
        Assert.check(columnName != null, "columnName != null");

        String listInterfaceName = objectDescriptor.getListInterface().getClassName();

        code.addLine("    public " + returnType + " " + methodName + "(TransactionContext transactionContext, " + findField.getJavaType() + " " + varName + ") {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(" + varName + " != null, \"" + varName + " != null\");");
        code.addLine("        Map<Identity,ValueObject> loadedObjects = new HashMap<Identity,ValueObject>();");
        code.addLine("        return " + methodName + "(transactionContext," + varName + ", loadedObjects);");
        code.addLine("    }");
        code.addLine();        
        
        code.addLine("    public " + returnType + " " + methodName + "(TransactionContext transactionContext, " + findField.getJavaType() + " " + varName + ", Map<Identity,ValueObject> loadedObjects) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(" + varName + " != null, \"" + varName + " != null\");");
        code.addLine("        PreparedStatement statement = null;");
        code.addLine("        ResultSet resultSet = null;");                
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();       
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();

        String objectTableAlias = objectDescriptor.getTableAlias();
        if (objectTableAlias == null) {
            objectTableAlias = "obj";
        }
        String baseTableAlias = "base";
        if (baseObjectDescriptor != null) {
            if (baseObjectDescriptor.getTableAlias() != null) {
                baseTableAlias = baseObjectDescriptor.getTableAlias();;
            }
        }
        
        code.addLine("            StringBuilder sql = new StringBuilder();");
        code.addLine("            sql.append(\"select \");");
        code.addLine("            sql.append(sqlSelect);");
        code.addLine("            sql.append(\"from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + " as " + objectTableAlias + " \");");
        if (baseObjectDescriptor != null) {
            code.addLine("            sql.append(\"inner join " +  baseObjectDescriptor.getTableName() + " as " + baseTableAlias + " on " + objectTableAlias + ".id = " + baseTableAlias + ".id \");");            
        }
        addJoins(objectDescriptor);

        code.addLine("            // objectTableAlias: " + objectTableAlias);
        code.addLine("            // columnName: " + columnName);
        code.addLine("            // realname: " + findField.getRealColumnName());
        String whereOrAnd = "";
        if (objectDescriptor.getMultiTenant()) {
        	code.addLine("            sql.append(\"where " + objectTableAlias + ".tid = ? \");");
            whereOrAnd = "and ";
        } else {
            whereOrAnd = "where ";
        }
        code.addLine("            sql.append(\"" + whereOrAnd
                + ((findField.getType() == FieldTypeEnum.READONLYJOIN) ? findAliasForJoinColumn(findField) : objectTableAlias)
                + "." + findField.getRealColumnName() + " = ? \");");

        if (orderBy != null) { 
            code.addLine("            sql.append(\"order by  " + objectTableAlias + "." + orderBy + "\");");
        }
       
        
        
        code.addLine("            Logger.debug(this, \"select from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + "\");");
        code.addLine("            Logger.debug(this, sql);");
        code.addLine("            statement = connection.prepareStatement(sql.toString());");
        code.addLine();
        
        code.addLine();
        int index = 1;
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", transactionContext.getUserContext().getTenantId(), false);");
        }
        if (findField.getType() == FieldTypeEnum.IDENTITY) {
            code.addLine("            statement.setBytes(" + index++ + ", " + varName + ".getByteValue());");
        } else if (findField.getType() == FieldTypeEnum.CLASS) {
            code.addLine("            statement.setBytes(" + index++ + ", " + varName + ".getId().getByteValue());");
        } else if (findField.getType() == FieldTypeEnum.STRING) {
            code.addLine("            statement.setString(" + index++ + ", " + varName + ");");
        } else if (findField.getType() == FieldTypeEnum.READONLYJOIN) {
            code.addLine("            // name: " + findField.getName());
            code.addLine("            // realname: " + findField.getRealColumnName());
            code.addLine("            // varName: " + varName);
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", " + varName + ", false);");
        } else {
            Assert.check(false, "type not supported for find " + findField.getType());
        }
        
        code.addLine("            resultSet = statement.executeQuery();");                

        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceVariable = toJavaVariableName(listInterfaceName);
        String valueObjectInterfaceName = valueObjectDescriptor.getClassName();
        String valueObjectVariable = toJavaVariableName(valueObjectInterfaceName);
        
        code.addLine("            " + listInterfaceName + " " + listInterfaceVariable +  " = get" + listInterfaceName +  "FromResultSet(transactionContext, resultSet, loadedObjects);");

        
        if (queryType == QueryTypeEnum.FINDBY_SINGLE) { 
            code.addLine("            Assert.check(" + listInterfaceVariable + ".size() <= 1, \"" + listInterfaceVariable + ".size() <= 1\");");

            code.addLine("            " + valueObjectInterfaceName +  " "  + valueObjectVariable +  " = null;");
            code.addLine("            if (" + listInterfaceVariable +  ".size() == 1) {");
            code.addLine("                " + valueObjectVariable +  " = (" + valueObjectInterfaceName + ")" + listInterfaceVariable + ".get(0);");
            code.addLine("                loadedObjects.put(" + valueObjectVariable +  ".getId(), " + valueObjectVariable + " );");
            code.addLine("                loadChildren(transactionContext, " + valueObjectVariable + ", loadedObjects);");
            code.addLine("            }");
            code.addLine();
            code.addLine("            return " + valueObjectVariable +  ";");
        } else {
            code.addLine("            Iterator i = " + listInterfaceVariable + ".iterator();");
            code.addLine("            while (i.hasNext()) {");
            code.addLine("                " + valueObjectInterfaceName + " " + valueObjectVariable + " = (" + valueObjectInterfaceName + ")i.next();");
            code.addLine("                loadChildren(transactionContext, " + valueObjectVariable + ", loadedObjects);");
            code.addLine("            }");

            code.addLine("            return " + listInterfaceVariable +  ";");
        }
        
        
        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException (\"Error finding record \", e);");
        code.addLine("        } finally {");
        code.addLine("            try {");
        code.addLine("                if (resultSet != null) {");                
        code.addLine("                    resultSet.close();");                
        code.addLine("                }");                
        code.addLine("                if (statement != null) {");
        code.addLine("                    statement.close();");
        code.addLine("                }");                
        code.addLine("            } catch (SQLException e) {");
        code.addLine("                Logger.error(this, \"Error in finally statement\");");
        code.addLine("                Logger.error(this, e);");
        code.addLine("            } ");
        code.addLine("        } ");
        code.addLine("    }");
    }

    private String addJoins(ObjectDescriptor objectDescriptor) {
        JoinList joinList = objectDescriptor.getJoins();

        for (JoinDescriptor joinDescriptor : joinList) {
            code.add("            sql.append(\"left outer join ");
            code.add(openEncapsulate +  joinDescriptor.rightTable + closeEncapsulate + " as " + joinDescriptor.rightAlias);
            code.add(" on (" + objectDescriptor.getTableAlias() + ".tid = " + joinDescriptor.rightAlias + ".tid");
            code.add(" and " + joinDescriptor.leftAlias + "." + joinDescriptor.leftColumn);
            code.addLine(" = " + joinDescriptor.rightAlias + "." + joinDescriptor.rightColumn + ") \");");
        }

        return null;
    }

    private String findAliasForJoinColumn(FieldDescriptor findField) {
        JoinList joinList = findField.getJoins();
        return joinList.get(joinList.size() - 1).rightAlias;
    }

    
    private void generateFindForUpdate() {
        code.addLine("    public ValueObject findForUpdate(TransactionContext transactionContext, Identity id) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(id != null, \"id != null\");");
        code.addLine("        PreparedStatement statement = null;");
        code.addLine("        ResultSet resultSet = null;");                
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();       
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();

        String objectTableAlias = objectDescriptor.getTableAlias();
        if (objectTableAlias == null) {
            objectTableAlias = "obj";
        }
        String baseTableAlias = "base";
        if (baseObjectDescriptor != null) {
            if (baseObjectDescriptor.getTableAlias() != null) {
                baseTableAlias = baseObjectDescriptor.getTableAlias();;
            }
        }
        
        code.addLine("            StringBuilder sql = new StringBuilder();");
        code.addLine("            sql.append(\"select \");");
        code.addLine("            sql.append(sqlSelectWithoutJoin);");
        code.addLine("            sql.append(\"from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + " as " + objectTableAlias + " \");");
        if (baseObjectDescriptor != null) {
            code.addLine("            sql.append(\"inner join " +  baseObjectDescriptor.getTableName() + " as " + baseTableAlias + " on " + objectTableAlias + ".id = " + baseTableAlias + ".id \");");            
        }

        code.addLine("            sql.append(\"where " + objectTableAlias + ".id = ? \");");
       
        
        
        //code.addLine("            Logger.debug(this, \"sql: \" + sql.toString());");
        code.addLine("            Logger.debug(this, \"select without joins from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate +"\");");
        code.addLine("            Logger.debug(this, sql);");
        
        code.addLine("            statement = connection.prepareStatement(sql.toString());");
        code.addLine();
        
        code.addLine();
        code.addLine("            statement.setBytes(1, id.getByteValue());");
        
        
        code.addLine("            resultSet = statement.executeQuery();");                

        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        String listInterfaceVariable = toJavaVariableName(listInterfaceName);
        String valueObjectInterfaceName = valueObjectDescriptor.getClassName();
        String valueObjectVariable = toJavaVariableName(valueObjectInterfaceName);
        
        code.addLine("            Map<Identity,ValueObject> loadedObjects = new HashMap<Identity,ValueObject>();");
        code.addLine("            " + listInterfaceName + " " + listInterfaceVariable +  " = get" + listInterfaceName +  "FromResultSet(transactionContext, resultSet, loadedObjects);");

        
        code.addLine("            Assert.check(" + listInterfaceVariable + ".size() <= 1, \"" + listInterfaceVariable + ".size() <= 1\");");

        code.addLine("            " + valueObjectInterfaceName +  " "  + valueObjectVariable +  " = null;");
        code.addLine("            if (" + listInterfaceVariable +  ".size() == 1) {");
        code.addLine("                " + valueObjectVariable +  " = (" + valueObjectInterfaceName + ")" + listInterfaceVariable + ".get(0);");
        code.addLine("            }");
        code.addLine();
        code.addLine("            return " + valueObjectVariable +  ";");
        
        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException (\"Error finding record \", e);");
        code.addLine("        } finally {");
        code.addLine("            try {");
        code.addLine("                if (resultSet != null) {");                
        code.addLine("                    resultSet.close();");                
        code.addLine("                }");                
        code.addLine("                if (statement != null) {");
        code.addLine("                    statement.close();");
        code.addLine("                }");                
        code.addLine("            } catch (SQLException e) {");
        code.addLine("                Logger.error(this, \"Error in finally statement\");");
        code.addLine("                Logger.error(this, e);");
        code.addLine("            } ");
        code.addLine("        }");
        code.addLine("    }");
    }
    
    private void generateGetListFromResultSet() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        String listInterfaceVariable = toJavaVariableName(listInterfaceName);

        
        code.addLine("    public " + listInterfaceName + " get" + listInterfaceName +  "FromResultSet(TransactionContext transactionContext, ResultSet resultSet, Map<Identity,ValueObject> loadedObjects) {");
        code.addLine("        try {");
        
        // The next line generated should look like this:
        //     PersonList personList = (PersonList)Factory.createObject(PersonList.class.getName());
        code.add    ("            " + listInterfaceName + " " + listInterfaceVariable);
        code.addLine(" = (" + listInterfaceName + ")Factory.createObject(" + listInterfaceName + ".class);");
        code.addLine();
        

        code.addLine("            while (resultSet.next()) {");

        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectVariable = valueObjectDescriptor.getJavaVariableName();

        code.addLine("                Identity returnedId = IdentityBuilder.createIdentity(resultSet.getBytes(1));");
        
        code.addLine("                " + valueObjectInterface + " " + valueObjectVariable + " = (" + valueObjectInterface + ")loadedObjects.get(returnedId);");
        code.addLine("                if (" + valueObjectVariable + " == null) {");
        
        // The next line generated should look like this:
        //     person = (Person)Factory.createObject(Person.class);
        code.addLine("                    " + valueObjectVariable + " = (" + valueObjectInterface + ")Factory.createObject(" + valueObjectInterface + ".class);");
        code.addLine("                    " + valueObjectVariable + ".setUserContext(transactionContext.getUserContext());");
        code.addLine();
        code.addLine("                    " + valueObjectVariable + ".setId(returnedId);");

        int index = 2;
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine("                    " + valueObjectVariable + ".setCreatedDate(resultSet.getTimestamp(2) == null ? null : new java.util.Date(resultSet.getTimestamp(2).getTime()));");
            code.addLine("                    " + valueObjectVariable + ".setCreatedBy(resultSet.getString(3));");
            code.addLine("                    " + valueObjectVariable + ".setModifiedDate(resultSet.getTimestamp(4) == null ? null : new java.util.Date(resultSet.getTimestamp(4).getTime()));");
            code.addLine("                    " + valueObjectVariable + ".setModifiedBy(resultSet.getString(5));");
            index = 6;
        }
        
        index = generateSetLines(objectDescriptor, valueObjectVariable, index);
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            index = generateSetLines(baseObjectDescriptor, valueObjectVariable, index);
        }
        index = generateReadOnlySetLines(objectDescriptor, valueObjectVariable, index);
        code.addLine();
        

        code.addLine("                    " + valueObjectVariable + ".setIsNew(false);");
        code.addLine("                    " + valueObjectVariable + ".setIsDirty(false);");
        code.addLine("                    loadedObjects.put(returnedId, " + valueObjectVariable + ");");
        code.addLine("                }");
        code.addLine("                " + listInterfaceVariable + ".add(" + valueObjectVariable + ");");
        code.addLine("            }");

        code.addLine("            " + listInterfaceVariable + ".setIsFullyLoaded(true);");
        code.addLine("            return " + listInterfaceVariable + ";");
        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException (\"Error finding record \", e);");
        code.addLine("        }");
        code.addLine("    }");
    }

    private int generateSetLines(ObjectDescriptor objectDescriptor, String valueObjectVariable, int startIndex) {
        int index = startIndex;
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (field.getType() == FieldTypeEnum.CLASS) {
                code.addLine("                    SubObjectHelper.setSubObjectId(" + valueObjectVariable + ", \"" + field.getName() + "Id\", resultSet.getBytes(" + index + "));");
                index++;
			} else if (field.getType() == FieldTypeEnum.INT) {
				code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.getInt(" + index + "));");
				index++;
            } else if (field.getType() == FieldTypeEnum.IDENTITY) {
                String variableBytesName = field.getJavaVariableName() + "bytes";
                code.addLine("                    byte[] " + variableBytesName + " = resultSet.getBytes(" + index + ");");
                code.addLine("                    if (" + variableBytesName + " != null) {");
                code.addLine("                        Identity " + field.getJavaVariableName() + " = IdentityBuilder.createIdentity(" + variableBytesName + ");");
                code.addLine("                        " + valueObjectVariable + ".set" + field.getName() + "(" + field.getJavaVariableName() + ");");
                code.addLine("                    }");
                index++;
			} else if (field.getType() == FieldTypeEnum.BOOLEAN) {
				code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(ResultSetWrapper.getBoolean(resultSet, " + index + "));");
				index++;
			} else if (field.getType() == FieldTypeEnum.DOUBLE) {
				code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(ResultSetWrapper.getDouble(resultSet, " + index + "));");
				index++;
			} else if (field.getType() == FieldTypeEnum.INTEGER) {
				code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(ResultSetWrapper.getInteger(resultSet, " + index + "));");
				index++;
            } else if (field.getType() == FieldTypeEnum.TEXT) {
                code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.getString(" + index + "));");
                index++;
            } else if (field.getType() == FieldTypeEnum.DATETIME) {
                code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.getTimestamp(" + index + ") == null ? null : new java.util.Date(resultSet.getTimestamp(" + index + ").getTime()));");
                index++;
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.getString(" + index + "));");
                index++;
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.get" + field.getJavaType() + "(" + index + "));");
                index++;
            } else {
                code.addLine("                    " + valueObjectVariable + ".set" + field.getName() + "(resultSet.get" + field.getType() + "(" + index + "));");
                index++;
            }
        }
        return index;
    }
    private int generateReadOnlySetLines(ObjectDescriptor objectDescriptor, String valueObjectVariable, int startIndex) {
        int index = startIndex;
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // todo: for now only supporting strings
                FieldDescriptor joinedField = field.getJoinedFieldDescriptor();
                if (joinedField.getType() == FieldTypeEnum.ENUM) { 
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.getString(" + index + "));");
                } else if (joinedField.getType() == FieldTypeEnum.IDENTITY) { 
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.getBytes(" + index + "));");
                } else if (joinedField.getType() == FieldTypeEnum.INTEGER || joinedField.getType() == FieldTypeEnum.INT) {
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.getInt(" + index + "));");
                } else if (joinedField.getType() == FieldTypeEnum.DOUBLE) { 
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", ResultSetWrapper.getDouble(resultSet, " + index + "));");
                } else if (joinedField.getType() == FieldTypeEnum.DATE) {
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.getDate(" + index + ") == null ? null : new java.util.Date(resultSet.getDate(" + index + ").getTime()));");
                } else if (joinedField.getType() == FieldTypeEnum.DATETIME) {
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.getTimestamp(" + index + ") == null ? null : new java.util.Date(resultSet.getTimestamp(" + index + ").getTime()));");
                } else {
                    code.addLine("                    SubObjectHelper.setReadOnlyVariable(" + valueObjectVariable + ", \"" + field.getName() + "\", resultSet.get" + field.getJavaType() + "(" + index + "));");
                }
                index++;
            } else {
                // do nothing
            }
        }
        return index;
    }
    
    private void generatePreSaveNew() {
        //ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        //String valueObjectInterface = valueObjectDescriptor.getClassName();
        //String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        code.addLine("    public void preSaveNew(TransactionContext transactionContext, ValueObject valueObject) {");

        code.addLine("    }");
    }
    private void generatePreSaveExisting() {
        //ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        //String valueObjectInterface = valueObjectDescriptor.getClassName();
        //String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        code.addLine("    public void preSaveExisting(TransactionContext transactionContext, ValueObject valueObject) {");

        code.addLine("    }");
    }
    private void generateSaveNew() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        code.addLine("    public void saveNew(TransactionContext transactionContext, ValueObject valueObject) {");

        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
        code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");
        code.addLine("        PreparedStatement statement = null;");
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();
        
        // String tableName = objectDescriptor.getTableName();
        generateInsert(valueObjectName, objectDescriptor);
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            generateInsert(valueObjectName, baseObjectDescriptor);
        }

        code.addLine("        } catch (SQLException e) {");
        code.addLine("            // Mysql uses 1062 and SqlServer uses 2601 for duplicates");
        code.addLine("            if (e.getErrorCode() == 1062 || e.getErrorCode() == 2601) {");
        code.addLine("                throw new DataAccessExceptionDuplicate(\"Error saving new record\", e);");
        code.addLine("            } else {");
        code.addLine("                throw new DataAccessException(\"Error saving new record\", e);");
        code.addLine("            } ");
        code.addLine("        } finally {");
        code.addLine("            try {");
        code.addLine("                if (statement != null) {");
        code.addLine("                    statement.close();");
        code.addLine("                }");                
        code.addLine("            } catch (SQLException e) {");
        code.addLine("                Logger.error(this, \"Error in finally statement\");");
        code.addLine("                Logger.error(this, e);");
        code.addLine("            } ");
        code.addLine("        }");        
        code.addLine("    }");
    }
    
    private void generateInsert(String valueObjectName, ObjectDescriptor objectDescriptor) {
        code.addLine("            Logger.debug(this, \"sql: \" + sqlInsert);");
        code.addLine("            statement = connection.prepareStatement(sqlInsert);");
        code.addLine();
        
        code.addLine("            JDBCUtil.setStatement(statement, 1, " + valueObjectName + ".getId(), false);");
        int index = 2;
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", transactionContext.getUserContext().getTenantId(), false);");
        }
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", " + valueObjectName + ".getCreatedDate(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", " + valueObjectName + ".getCreatedBy(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", " + valueObjectName + ".getModifiedDate(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, " + index++ + ", " + valueObjectName + ".getModifiedBy(), true);");
        }
 
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.CLASS) {
                code.add    ("            JDBCUtil.setStatement(statement, ");
                code.add    (index++ + ", SubObjectHelper.getSubObjectId(" + valueObjectName);
                code.addLine(", \"" + field.getName() + "Id\"), " + field.getNullable() + ");");
            /*
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                String varName = field.getJavaVariableName();
                code.addLine("            " + field.getJavaType() + " " + varName + " = " + valueObjectName + ".get" + field.getName() + "();");
                
                code.addLine("            if (" + varName + " != null) { ");
                code.addLine("                JDBCUtil.setStatement(statement, " + index + ", " + varName + ".toString(), 30);");
                code.addLine("            } else { ");
                code.addLine("                JDBCUtil.setStatement(statement, " + index + ", (String)null, 30);");
                code.addLine("            }");                
                index++;
            */
            } else if (field.getType() == FieldTypeEnum.STRING) {
                code.add    ("            JDBCUtil.setStatement(statement, ");
                code.add    (index++ + ", ");
                code.add    (valueObjectName + ".get" + field.getName() + "(), ");
                code.addLine(field.getNullable() + ", \"" + valueObjectName + "." + field.getName() + "\", " + field.getSize() + ");");
            } else {
                code.add    ("            JDBCUtil.setStatement(statement, ");
                code.add    (index++ + ", ");
                code.add    (valueObjectName + ".get" + field.getName() + "(), ");
                code.addLine(field.getNullable() + ");");
            }
        }        
        
        code.addLine("            statement.execute();");
        code.addLine();
    }
    
    
    private void generateSaveExisting() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        code.addLine("    public void saveExisting(TransactionContext transactionContext, ValueObject existingValueObject, ValueObject valueObject) {");

        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
        code.addLine("        if (valueObject.getIsDirty()) {");

        code.addLine("            " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");
        code.addLine("            PreparedStatement statement = null;");
        code.addLine("            try {");
        code.addLine("                Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("                Assert.check(connection != null, \"connection != null\");");
        code.addLine();

        //String tableName = objectDescriptor.getTableName();
        generateUpdate(valueObjectName, objectDescriptor);
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            generateUpdate(valueObjectName, baseObjectDescriptor);
        }

        code.addLine("            } catch (SQLException e) {");
        code.addLine("                // Mysql uses 1062 and SqlServer uses 2601 for duplicates");
        code.addLine("                if (e.getErrorCode() == 1062 || e.getErrorCode() == 2601) {");
        code.addLine("                    throw new DataAccessExceptionDuplicate(\"Error saving existing record\", e);");
        code.addLine("                } else {");
        code.addLine("                    throw new DataAccessException(\"Error saving existing  record\", e);");
        code.addLine("                } ");
        code.addLine("            } finally {");
        code.addLine("                try {");
        code.addLine("                    if (statement != null) {");
        code.addLine("                        statement.close();");
        code.addLine("                    }");                
        code.addLine("                } catch (SQLException e) {");
        code.addLine("                    Logger.error(this, \"Error in finally statement\");");
        code.addLine("                    Logger.error(this, e);");
        code.addLine("                } ");
        code.addLine("            }");        
        code.addLine("        }");        
        code.addLine("    }");
    }
    
    private void generateUpdate(String valueObjectName, ObjectDescriptor objectDescriptor) {
        code.addLine("            Logger.debug(this, \"sql: \" + sqlUpdate);");
        code.addLine("            statement = connection.prepareStatement(sqlUpdate);");
        code.addLine();
        
        
        int index = 1;
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine("            JDBCUtil.setStatement(statement, 1, " + valueObjectName + ".getCreatedDate(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, 2, " + valueObjectName + ".getCreatedBy(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, 3, " + valueObjectName + ".getModifiedDate(), true);");
            code.addLine("            JDBCUtil.setStatement(statement, 4, " + valueObjectName + ".getModifiedBy(), true);");
            index = 5;            
        }
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
            if (!StringUtil.isEmpty(field.getSql())) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.CLASS) {
                code.add    ("            JDBCUtil.setStatement(statement, ");
                code.add    (index++ + ", SubObjectHelper.getSubObjectId(" + valueObjectName);
                code.addLine(", \"" + field.getName() + "Id\"), " + field.getNullable() + ");");
            /*
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                String varName = field.getJavaVariableName();
                code.addLine("            " + field.getJavaType() + " " + varName + " = " + valueObjectName + ".get" + field.getName() + "();");
                
                code.addLine("            if (" + varName + " != null) { ");
                code.addLine("                JDBCUtil.setStatement(statement, " + index + ", " + varName + ".toString(), 30);");
                code.addLine("            } else { ");
                code.addLine("                JDBCUtil.setStatement(statement, " + index + ", (String)null, 30);");
                code.addLine("            }");                
                index++;
            */
            } else {
                code.add    ("            JDBCUtil.setStatement(statement, ");
                code.add    (index++ + ", ");
                code.add    (valueObjectName + ".get" + field.getName() + "(), ");
                code.addLine(field.getNullable() + ");");
            }
        }
        
        code.addLine("            // where clause");
        code.addLine("            JDBCUtil.setStatement(statement, " + index + ", " + valueObjectName + ".getId(), false);");
        code.addLine();
        
        
        code.addLine("            statement.execute();");
    }

    
    private void generateSaveDelete() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        code.addLine("    public void saveDelete(TransactionContext transactionContext, ValueObject valueObject) {");

        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
        code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");
        code.addLine("        PreparedStatement statement = null;");
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();

        // String tableName = objectDescriptor.getTableName();
        generateDelete(valueObjectName, objectDescriptor);
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            generateDelete(valueObjectName, baseObjectDescriptor);
        }

        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException(\"Error deleting record\", e);");
        code.addLine("        } finally {");
        code.addLine("            try {");
        code.addLine("                if (statement != null) {");
        code.addLine("                    statement.close();");
        code.addLine("                }");                
        code.addLine("            } catch (SQLException e) {");
        code.addLine("                Logger.error(this, \"Error in finally statement\");");
        code.addLine("                Logger.error(this, e);");
        code.addLine("            } ");
        code.addLine("        }");        
        code.addLine("    }");
    }
    
    private void generateDelete(String valueObjectName, ObjectDescriptor objectDescriptor) {
        code.addLine("            StringBuilder sql = new StringBuilder();");
        code.addLine("            sql.append(\"delete from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + "\");");
        code.addLine("            sql.append(\" where id = ?\");");
        code.addLine("            Logger.debug(this, \"sql: \" + sql.toString());");
        code.addLine("            statement = connection.prepareStatement(sql.toString());");
        code.addLine();
        
        code.addLine("            // where clause");
        code.addLine("            JDBCUtil.setStatement(statement, 1, " + valueObjectName + ".getId(), false);");
        code.addLine();
        
        code.addLine("            statement.execute();");
    }
    
    private void generateSaveNewAudit() {
        code.addLine("    public void saveNewAuditData(TransactionContext transactionContext, ValueObject valueObject) {");

        if (objectDescriptor.getSupportsAudit()) { 
            ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
            String valueObjectInterface = valueObjectDescriptor.getClassName();

            String tableName = objectDescriptor.getTableName();
            String valueObjectName = valueObjectDescriptor.getJavaVariableName();

            code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
            code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
            code.addLine();
            code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");

            code.addLine();
            code.addLine("        AuditUtil auditUtil = new AuditUtil();");        
            code.addLine("        Audit audit = auditUtil.createAuditRecord(transactionContext, \"add\");");        
            code.addLine();

            for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
                if (!StringUtil.isEmpty(field.getSql())) {
                	// do nothing
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.COALESCE) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.CLASS) {
                    String variableName = field.getJavaVariableName() + "Id";
                    code.addLine("        Identity " + variableName + " = SubObjectHelper.getSubObjectId(" + valueObjectName + ", \"" + field.getName() + "Id\");");
                    code.addLine("        auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", " + null + ", " + variableName + ");");
                    code.addLine();
                } else {                    
                    String variableName = field.getJavaVariableName();
                    code.addLine("        " + field.getJavaType() + " " + variableName + " = " + valueObjectName + ".get" + field.getName() + "();");
                    code.addLine("        auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", null, " + variableName + ");");
                } 

            }
        }
        
        code.addLine("    }");
    }

    private void generateSaveExistingAudit() {
        code.addLine("    public void saveExistingAuditData(TransactionContext transactionContext, ValueObject existingValueObject, ValueObject valueObject) {");

        if (objectDescriptor.getSupportsAudit()) { 
            ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
            String valueObjectInterface = valueObjectDescriptor.getClassName();

            String tableName = objectDescriptor.getTableName();
            String newValueObjectName = "new" + valueObjectDescriptor.getClassName();
            String oldValueObjectName = "old" + valueObjectDescriptor.getClassName();

            code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
            code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
            code.addLine();
            code.addLine("        if (valueObject.getIsDirty()) {");
            code.addLine("            " + valueObjectInterface + " " + newValueObjectName + " = (" + valueObjectInterface + ")valueObject;");
            code.addLine("            " + valueObjectInterface + " " + oldValueObjectName + " = (" + valueObjectInterface + ")existingValueObject;");

            code.addLine();
            code.addLine("            AuditUtil auditUtil = new AuditUtil();");        
            code.addLine("            Audit audit = auditUtil.createAuditRecord(transactionContext, \"update\");");        
            code.addLine();

            for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
                if (!StringUtil.isEmpty(field.getSql())) {
                	// do nothing
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.COALESCE) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.CLASS) {
                    String newVariableName = "new" + field.getName() + "Id";
                    String oldVariableName = "old" + field.getName() + "Id";
                    code.addLine("            Identity " + oldVariableName + " = SubObjectHelper.getSubObjectId(" + oldValueObjectName + ", \"" + field.getName() + "Id\");");
                    code.addLine("            Identity " + newVariableName + " = SubObjectHelper.getSubObjectId(" + newValueObjectName + ", \"" + field.getName() + "Id\");");
                    code.addLine("            if (auditUtil.compareValuesDiffer(" + newVariableName + ", " + oldVariableName + ")) {");
                    code.addLine("                auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", " + oldVariableName + ", " + newVariableName + ");");
                    code.addLine("            }");
                    code.addLine();
                } else {                    
                    String newVariableName = "new" + field.getName();
                    String oldVariableName = "old" + field.getName();
                    code.addLine("            " + field.getJavaType() + " " + oldVariableName + " = " + oldValueObjectName + ".get" + field.getName() + "();");
                    code.addLine("            " + field.getJavaType() + " " + newVariableName + " = " + newValueObjectName + ".get" + field.getName() + "();");
                    code.addLine("            if (auditUtil.compareValuesDiffer(" + newVariableName + ", " + oldVariableName + ")) {");
                    code.addLine("                auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", " + oldVariableName + ", " + newVariableName + ");");
                    code.addLine("            }");
                    code.addLine();
                }

            }
            code.addLine("        }");
        }
        
        code.addLine("    }");
    }

    private void generateSaveDeleteAudit() {
        code.addLine("    public void saveDeleteAuditData(TransactionContext transactionContext, ValueObject valueObject) {");

        if (objectDescriptor.getSupportsAudit()) { 
            ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
            String valueObjectInterface = valueObjectDescriptor.getClassName();

            String tableName = objectDescriptor.getTableName();
            String valueObjectName = valueObjectDescriptor.getJavaVariableName();


            code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
            code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
            code.addLine();
            code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");

            code.addLine();
            code.addLine("        AuditUtil auditUtil = new AuditUtil();");        
            code.addLine("        Audit audit = auditUtil.createAuditRecord(transactionContext, \"delete\");");        
            code.addLine();

            for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
                if (!StringUtil.isEmpty(field.getSql())) {
                	// do nothing
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.COALESCE) {
                    // do nothing
                } else if (field.getType() == FieldTypeEnum.CLASS) {
                    String variableName = field.getJavaVariableName() + "Id";
                    code.addLine("        Identity  " + variableName + " = SubObjectHelper.getSubObjectId(" + valueObjectName + ", \"" + field.getName() + "Id\");");
                    code.addLine("        auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", " + variableName + ", " + null + ");");
                    code.addLine();
                } else {                    
                    String variableName = field.getJavaVariableName();
                    code.addLine("        " + field.getJavaType() + " " + variableName + " = " + valueObjectName + ".get" + field.getName() + "();");
                    code.addLine("        auditUtil.createAuditDetailRecord(transactionContext, audit, valueObject.getId(), \"" + tableName + "\", \"" + field.getName() + "\", " + variableName + ", null);");
                    code.addLine();
                }

            }
        }
        
        code.addLine("    }");
    }
    
    private void generateQueries(ObjectDescriptor objectDescriptor) {
        for (QueryDescriptor query : objectDescriptor.getQueries()) {
            Assert.check(query.getType() != null, "query.getType() != null");
            generateFindByQuery(objectDescriptor, query);
        }
    }

    private void generateFindByQuery(ObjectDescriptor objectDescriptor, QueryDescriptor query) {
        FieldDescriptor field = objectDescriptor.findField(query.getFieldName()); 
        Assert.check(field != null, "field != null");
        Logger.debug(this, field);

        QueryTypeEnum queryType = query.getType();
        //String listInterfaceName = objectDescriptor.getListInterface().getClassName();

        String returnType = null;
        if (queryType == QueryTypeEnum.FINDBY_SINGLE) { 
            returnType = objectDescriptor.getValueObjectInterface().getClassName();
        } else {
            returnType = objectDescriptor.getListInterface().getClassName();
        }       
        
        generateUserContextFind(query.getMethodName(), field, queryType, returnType);
        
        generateFind(query.getMethodName(), field, queryType, returnType, query.getOrderBy());
        
        
    }
    private void generateUserContextFind(String methodName, FieldDescriptor findField, QueryTypeEnum queryType, String returnType) {
        ClassDescriptor classDescriptor = findField.getClassDescriptor();        
        Assert.check(classDescriptor != null, "classDescriptor != null");
        Logger.debug(this, classDescriptor);
        String varName = findField.getJavaVariableName();

        code.addLine("    public " + returnType + " " + methodName + "(UserContext userContext, " + findField.getJavaType() + " " + varName + ") {");
        code.addLine("        Assert.check(userContext != null, \"userContext != null\");");
        code.addLine("        Assert.check(" + varName + " != null, \"" + varName + " != null\");");
        code.addLine("        TransactionContext transactionContext = new TransactionContext(userContext);");
        code.addLine("        try {");
        code.addLine("            return " + methodName + "(transactionContext, " + varName + ");");
        code.addLine("        } finally {");
        code.addLine("            transactionContext.close();");
        code.addLine("        }");
        code.addLine("    }");
    }
    
    private void generateUserContextSearch() {
        //ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        code.addLine("    public " + listInterfaceName + " search(UserContext userContext, SearchCriteria searchCriteria, boolean deepCopy) {");
        code.addLine("        Assert.check(userContext != null, \"userContext != null\");");
        code.addLine("        TransactionContext transactionContext = new TransactionContext(userContext);");
        code.addLine("        try {");
        code.addLine("            return search(transactionContext, searchCriteria, deepCopy);");
        code.addLine("        } finally {");
        code.addLine("            transactionContext.close();");
        code.addLine("        }");
        code.addLine("    }");
    }
    
    private void generateSearch() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        String listVariableName = objectDescriptor.getListInterface().getJavaVariableName();
        String voInterfaceName = valueObjectDescriptor.getClassName();
        String voVariableName = valueObjectDescriptor.getJavaVariableName();

        code.addLine("    public " + listInterfaceName + " search(TransactionContext transactionContext, SearchCriteria searchCriteria, boolean deepCopy) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Map<Identity,ValueObject> loadedObjects = new HashMap<Identity,ValueObject>();");
        code.addLine("        return search(transactionContext, searchCriteria, deepCopy, loadedObjects);");
        code.addLine("    }");
        code.addLine();
        
        code.addLine("    public " + listInterfaceName + " search(TransactionContext transactionContext, SearchCriteria searchCriteria, boolean deepCopy, Map<Identity,ValueObject> loadedObjects) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        if (searchCriteria == null) {");
        code.addLine("            searchCriteria = new SearchCriteriaBase();");
        code.addLine("        }");
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();       
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        
        String objectTableAlias = objectDescriptor.getTableAlias();
        if (objectTableAlias == null) {
            objectTableAlias = "obj";
        }
        String baseTableAlias = "base";
        if (baseObjectDescriptor != null) {
            if (baseObjectDescriptor.getTableAlias() != null) {
                baseTableAlias = baseObjectDescriptor.getTableAlias();;
            }
        }
        
        String previousAlias = objectTableAlias;
        
        code.addLine("            StringBuilder sql = new StringBuilder();");
        code.addLine("            sql.append(\"select \");");
        code.addLine("            sql.append(getVendorSpecificRowLimitPrefix(connection, searchCriteria.getTopAmount()));");

        //code.addLine("            if (searchCriteria.getTopAmount() > 0) {");
        //code.addLine("                sql.append(\"top \");");
        //code.addLine("                sql.append(searchCriteria.getTopAmount());");
        //code.addLine("                sql.append(\" \");");
        //code.addLine("            }");
        code.addLine("            sql.append(sqlSelect);");
        code.addLine("            sql.append(\"from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate +" as " + objectTableAlias + " \");");
        if (baseObjectDescriptor != null) {
            code.addLine("            sql.append(\"inner join " +  baseObjectDescriptor.getTableName() + " as " + baseTableAlias + " on " + objectTableAlias + ".id = " + baseTableAlias + ".id \");");            
            previousAlias = baseTableAlias;
        }
        addJoins(objectDescriptor);
        
        
        code.addLine("            searchCriteria.setPreviousAlias(\"" + previousAlias + "\");");
        code.addLine("            sql.append(searchCriteria.getFromClause());");
        
        //code.addLine("            sql.append(\"where \");");
        
        code.addLine("            String whereClause = searchCriteria.getWhereClause();");      
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("            sql.append(\"where " + previousAlias + ".tid = ? \");");
            code.addLine("            if (!StringUtil.isEmpty(whereClause)) { ");      
            code.addLine("                sql.append(\" and (\");");      
            code.addLine("                sql.append(whereClause);");      
            code.addLine("                sql.append(\")\");");      
            code.addLine("            }");      
        } else {
            code.addLine("            if (!StringUtil.isEmpty(whereClause)) { ");      
            code.addLine("                sql.append(\"where \");");      
            code.addLine("                sql.append(whereClause);");      
            code.addLine("            }");      
        }

        code.addLine("            sql.append(searchCriteria.getOrderBy());");      
        code.addLine("            sql.append(getVendorSpecificRowLimitPostfix(connection, searchCriteria.getTopAmount()));");
        
        
        code.addLine("            Logger.debug(this, \"select from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + "\");");        
        code.addLine("            Logger.debug(this, sql);");
        code.addLine("            PreparedStatement statement = connection.prepareStatement(sql.toString());");
        code.addLine();
        if (objectDescriptor.getMultiTenant()) {
        	code.addLine("            Identity tenantId = transactionContext.getUserContext().getTenantId();");
        	code.addLine("            Assert.check(tenantId != null, \"tenantId != null\");");
        	code.addLine("            JDBCUtil.setStatement(statement, 1, tenantId, false);");
        	code.addLine("            searchCriteria.setParameters(statement, 2);");
        } else {
        	code.addLine("            searchCriteria.setParameters(statement, 1);");        	
        }
        
        code.addLine("            ResultSet resultSet = statement.executeQuery();");                
        code.addLine();
        
        
        code.addLine("            " + listInterfaceName + " " + listVariableName + " = get" + listInterfaceName +  "FromResultSet(transactionContext, resultSet, loadedObjects);");

        code.addLine("            if (deepCopy) {");
        //code.addLine("                Iterator i = " + listVariableName + ".iterator();");
        //code.addLine("                while (i.hasNext()) {");
        //code.addLine("                    " + voInterfaceName + " " + voVariableName + " = (" + voInterfaceName + ")i.next();");
        code.addLine("                for (" + voInterfaceName + " " + voVariableName + " : " + listVariableName + ") {");
        code.addLine("                    loadChildren(transactionContext, " + voVariableName + ", loadedObjects);");
        code.addLine("                }");
        code.addLine("            }");

        code.addLine("            return " + listVariableName + ";");
        
        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException (\"Error finding record \", e);");
        code.addLine("        }");
        code.addLine("    }");
    }

    private void generateUserContextSearchCount() {
        //ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        code.addLine("    public int searchCount(UserContext userContext, SearchCriteria searchCriteria) {");
        code.addLine("        Assert.check(userContext != null, \"userContext != null\");");
        code.addLine("        TransactionContext transactionContext = new TransactionContext(userContext);");
        code.addLine("        try {");
        code.addLine("            return searchCount(transactionContext, searchCriteria);");
        code.addLine("        } finally {");
        code.addLine("            transactionContext.close();");
        code.addLine("        }");
        code.addLine("    }");
    }

    private void generateSearchCount() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String listInterfaceName = objectDescriptor.getListInterface().getClassName();
        String listVariableName = objectDescriptor.getListInterface().getJavaVariableName();
        String voInterfaceName = valueObjectDescriptor.getClassName();
        String voVariableName = valueObjectDescriptor.getJavaVariableName();

        code.addLine("    public int searchCount(TransactionContext transactionContext, SearchCriteria searchCriteria) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        if (searchCriteria == null) {");
        code.addLine("            searchCriteria = new SearchCriteriaBase();");
        code.addLine("        }");
        code.addLine("        try {");
        code.addLine("            Connection connection = transactionContext.findConnection(daoDescriptor.getConnectionName());");
        code.addLine("            Assert.check(connection != null, \"connection != null\");");
        code.addLine();

        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();

        String objectTableAlias = objectDescriptor.getTableAlias();
        if (objectTableAlias == null) {
            objectTableAlias = "obj";
        }
        String baseTableAlias = "base";
        if (baseObjectDescriptor != null) {
            if (baseObjectDescriptor.getTableAlias() != null) {
                baseTableAlias = baseObjectDescriptor.getTableAlias();;
            }
        }

        String previousAlias = objectTableAlias;

        code.addLine("            StringBuilder sql = new StringBuilder();");
        code.addLine("            sql.append(\"select count(*)\");");
        code.addLine("            sql.append(\"from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate +" as " + objectTableAlias + " \");");
        if (baseObjectDescriptor != null) {
            code.addLine("            sql.append(\"inner join " +  baseObjectDescriptor.getTableName() + " as " + baseTableAlias + " on " + objectTableAlias + ".id = " + baseTableAlias + ".id \");");
            previousAlias = baseTableAlias;
        }
        addJoins(objectDescriptor);


        code.addLine("            searchCriteria.setPreviousAlias(\"" + previousAlias + "\");");
        code.addLine("            sql.append(searchCriteria.getFromClause());");

        //code.addLine("            sql.append(\"where \");");

        code.addLine("            String whereClause = searchCriteria.getWhereClause();");
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("            sql.append(\"where " + previousAlias + ".tid = ? \");");
            code.addLine("            if (!StringUtil.isEmpty(whereClause)) { ");
            code.addLine("                sql.append(\" and (\");");
            code.addLine("                sql.append(whereClause);");
            code.addLine("                sql.append(\")\");");
            code.addLine("            }");
        } else {
            code.addLine("            if (!StringUtil.isEmpty(whereClause)) { ");
            code.addLine("                sql.append(\"where \");");
            code.addLine("                sql.append(whereClause);");
            code.addLine("            }");
        }

        code.addLine("            Logger.debug(this, \"select from " + openEncapsulate + objectDescriptor.getTableName() + closeEncapsulate + "\");");
        code.addLine("            Logger.debug(this, sql);");
        code.addLine("            PreparedStatement statement = connection.prepareStatement(sql.toString());");
        code.addLine();
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("            Identity tenantId = transactionContext.getUserContext().getTenantId();");
            code.addLine("            Assert.check(tenantId != null, \"tenantId != null\");");
            code.addLine("            JDBCUtil.setStatement(statement, 1, tenantId, false);");
            code.addLine("            searchCriteria.setParameters(statement, 2);");
        } else {
            code.addLine("            searchCriteria.setParameters(statement, 1);");
        }

        code.addLine("            ResultSet resultSet = statement.executeQuery();");
        code.addLine();
        code.addLine("            resultSet.next();");
        code.addLine("            return resultSet.getInt(1);");

        code.addLine("        } catch (SQLException e) {");
        code.addLine("            throw new DataAccessException (\"Error finding record \", e);");
        code.addLine("        }");
        code.addLine("    }");
    }

    private void generateLoadChildrenMethod() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        
        code.addLine("    public void loadChildren(TransactionContext transactionContext, ValueObject valueObject, Map<Identity,ValueObject> loadedObjects) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
        code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");
        code.addLine();       
        code.addLine("        /*");
        
        generateLoadChildrenCalls(valueObjectName);
        
        code.addLine("        */");
        code.addLine("    }");

    }
    
    private void generateLoadChildrenCalls(String valueObject) {
        for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
            ObjectDescriptor childObjectDesciptor = referenceDescriptor.getTargetObjectDescriptor();

            String daoInterface = childObjectDesciptor.getDAOInterface().getClassName();
            String daoVariableName = childObjectDesciptor.getDAOInterface().getJavaVariableName();
            String childValueClass = childObjectDesciptor.getListInterface().getClassName();
            String childValueObject = childObjectDesciptor.getListInterface().getJavaVariableName();

            // LookupDataDAO lookupDataDAO = (LookupDataDAO)DataAccessLocator.findDAO("com.modelgenerated.lookup.LookupData");
            code.addLine("        {");                
            code.addLine("            " + daoInterface + " " + daoVariableName + " = (" + daoInterface + ")DataAccessLocator.findDAO(\"" + referenceDescriptor.getTargetClass().getFQN() + "\");");                

            code.addLine("            " + childValueClass + " " + childValueObject + " = " + daoVariableName + "." + referenceDescriptor.getTargetMethod() + "(transactionContext, " + valueObject + ", loadedObjects);");                
            code.addLine("            " + valueObject + ".set" + referenceDescriptor.getName() + "(" + childValueObject + ");");                
            code.addLine("        }");                
            code.addLine();
        }        
        
        Model model = objectDescriptor.getModel();
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS) {
                ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
                if (childObjectDesciptor != null) {
                    String daoInterface = childObjectDesciptor.getDAOInterface().getClassName();
                    String daoVariableName = childObjectDesciptor.getDAOInterface().getJavaVariableName();
                    String childValueClass = childObjectDesciptor.getValueObjectInterface().getClassName();
                    String childValueObject = childObjectDesciptor.getValueObjectInterface().getJavaVariableName();
                    
                    String valueObjectIdName = field.getJavaVariableName() + "Id";

                    code.addLine("        Identity " + valueObjectIdName + " = SubObjectHelper.getSubObjectId(" + valueObject + ", \"" + field.getName() + "Id\");");                
                    code.addLine("        if (" + valueObjectIdName + " != null) {");                
                    
                    // LookupData lookupData = (ContactInfo)contactInfoDAO.find(transactionContext, SubObjectHelper.getSubObjectId(company, "ContactInfoId")));
                    code.addLine("            " + childValueClass + " " + childValueObject + " = (" + childValueClass + ")loadedObjects.get(" + valueObjectIdName + ");");                
                    code.addLine("            if (" + childValueObject + " == null) {");                
                    
                    // lookupDataDAO = (LookupDataDAO)DataAccessLocator.findDAO("com.modelgenerated.lookup.LookupData");
                    code.addLine("                " + daoInterface + " " + daoVariableName + " = (" + daoInterface + ")DataAccessLocator.findDAO(\"" + field.getClassDescriptor().getFQN() + "\");");                

                    // LookupData lookupData = (ContactInfo)contactInfoDAO.find(transactionContext, SubObjectHelper.getSubObjectId(company, "ContactInfoId")));
                    code.addLine("                " + childValueObject + " = (" + childValueClass + ")" + daoVariableName + ".find(transactionContext, " + valueObjectIdName + ", loadedObjects);");                
                    code.addLine("            }");                
                    
                    // company.setContactInfo(lookupData);
                    code.addLine("            " + valueObject + ".set" + field.getName() + "(" + childValueObject + ");");                
                    code.addLine("        }");                
                    code.addLine();
                }
            }
        }
    }
    
    
    private void generateSaveChildrenMethod() {
        ClassDescriptor valueObjectDescriptor = objectDescriptor.getValueObjectInterface();
        String valueObjectInterface = valueObjectDescriptor.getClassName();
        String valueObjectName = valueObjectDescriptor.getJavaVariableName();
        
        code.addLine("    public void saveChildren(TransactionContext transactionContext, ValueObject valueObject, Map<Identity,ValueObject> savedObjects) {");
        code.addLine("        Assert.check(transactionContext != null, \"transactionContext != null\");");
        code.addLine("        Assert.check(valueObject != null, \"valueObject != null\");");
        code.addLine("        " + valueObjectInterface + " " + valueObjectName + " = (" + valueObjectInterface + ")valueObject;");
        code.addLine();       

        for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
            ObjectDescriptor childObjectDesciptor = referenceDescriptor.getTargetObjectDescriptor();

            String fieldName = referenceDescriptor.getName();
            String fieldClassFQN = referenceDescriptor.getTargetClass().getFQN();
            String voVariableName = referenceDescriptor.getJavaVariableName();
            Logger.debug(this, "referenceDescriptor.getType()" + referenceDescriptor.getType());

            if (referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_MANY) { 
                generateSaveChildCall(childObjectDesciptor,  valueObjectName, fieldName, fieldClassFQN, voVariableName, true);
            } else if (referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_ONE) { 
                generateSaveChildCall(childObjectDesciptor,  valueObjectName, fieldName, fieldClassFQN, voVariableName, false);
            } else {
                Assert.check(false, "Unsupported referenceTypeEnum");
            }
        }        

        Model model = objectDescriptor.getModel();
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS 
                                && field.getPersisted()
                                ) {
                ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
                if (childObjectDesciptor != null) {               
                    String fieldName = field.getName();
                    String fieldClassFQN = field.getClassDescriptor().getFQN();
                    String voVariableName = field.getJavaVariableName();

                    generateSaveChildCall(childObjectDesciptor,  valueObjectName, fieldName, fieldClassFQN, voVariableName, false);
                }
            }
        }
        
        code.addLine();       
        code.addLine("    }");
        
    }
    
    protected void generateSaveChildCall(ObjectDescriptor childObjectDesciptor,  String valueObjectName, String fieldName, String fieldClassFQN, String voVariable, boolean bList) {
        String daoInterface = childObjectDesciptor.getDAOInterface().getClassName();
        String daoVariableName = childObjectDesciptor.getDAOInterface().getJavaVariableName();
        
        ClassDescriptor classDescriptor = null;         
        if (bList) {
            classDescriptor = childObjectDesciptor.getListInterface();
        } else {
            classDescriptor = new ClassDescriptor(fieldClassFQN);
        }
        String voClass = classDescriptor.getClassName();
        
        code.addLine("        " + voClass + " " + voVariable + " = null;");                
        code.addLine("        try {");
        code.addLine("            " + voVariable + " = " + valueObjectName + ".get" + fieldName + "();");                
        code.addLine("        } catch (ObjectNotLoadedException e) {}");
        code.addLine("        if (" + voVariable + " != null) {");                

        // LookupDataDAO lookupDataDAO = (LookupDataDAO)DataAccessLocator.findDAO("com.modelgenerated.lookup.LookupData");
        code.addLine("            " + daoInterface + " " + daoVariableName + " = (" + daoInterface + ")DataAccessLocator.findDAO(\"" + fieldClassFQN + "\");");                

        code.addLine("            " + daoVariableName + ".save(transactionContext, " + voVariable + ", savedObjects);");                
        code.addLine("        }");                
        code.addLine();
    }
    
    protected String getFullyQualifiedName() {
        ClassDescriptor classDescriptor = objectDescriptor.getDAOImplementation(); 
        return classDescriptor.getFQN();
    }


}
