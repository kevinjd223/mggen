/*
 * ValueObjectGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.modelmetadata.ReferenceTypeEnum;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class ValueObjectGenerator  extends JavaCodeBaseGenerator {
    private static final int DECLARE_PADDING=36;
    /** Creates a new instance of ValueObjectGenerator */
    public ValueObjectGenerator() {
    }
    
    protected void generateHeader() {
        Logger.debug(this, objectDescriptor);
        
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
        
		ImportGenerator importGenerator = new ImportGenerator();
        
		// import value object interface
		importGenerator.addImport(objectDescriptor.getValueObjectInterface().getFQN());
        // import base value object interface        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
			importGenerator.addImport(baseObjectDescriptor.getValueObjectInterface().getFQN());
        }
        
		importGenerator.addImport("com.modelgenerated.foundation.debug.Displayable");
		importGenerator.addImport("com.modelgenerated.foundation.debug.DisplayBuffer");
		importGenerator.addImport("com.modelgenerated.foundation.dataaccess.AbstractValueObject");
        if (objectDescriptor.hasFieldType(FieldTypeEnum.CLASS) || objectDescriptor.hasReferences()) { 
			importGenerator.addImport("com.modelgenerated.foundation.dataaccess.DataAccessLocator");
			importGenerator.addImport("com.modelgenerated.foundation.dataaccess.LoadedObjectVisitor");
			importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ObjectNotLoadedException");
			importGenerator.addImport("com.modelgenerated.foundation.dataaccess.TransactionContext");
			importGenerator.addImport("com.modelgenerated.foundation.dataaccess.UserContext");        
        }
		importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ValueObject");
        if (objectDescriptor.hasReferences()) {
			importGenerator.addImport("com.modelgenerated.foundation.factory.Factory");
        }
        if (objectDescriptor.hasFieldType(FieldTypeEnum.CLASS) 
                || objectDescriptor.hasFieldType(FieldTypeEnum.IDENTITY)
                || objectDescriptor.hasReferences()) {
			importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        }       
		importGenerator.addImport("com.modelgenerated.util.DateUtil");
        
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            ClassDescriptor type = field.getClassDescriptor();
            if (type != null && type.getFQN() != null) {
                importGenerator.addImport(type.getFQN());                    
            } 
        }

        for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
            ObjectDescriptor childObjectDesciptor = referenceDescriptor.getTargetObjectDescriptor();
			String valueObjectInterface = childObjectDesciptor.getValueObjectInterface().getFQN();
			String daoInterface = childObjectDesciptor.getDAOInterface().getFQN();
            String listInterface = childObjectDesciptor.getListInterface().getFQN();
			importGenerator.addImport(valueObjectInterface);
            if (childObjectDesciptor.getPersisted()) {            
                importGenerator.addImport(daoInterface);            
            }
			importGenerator.addImport(listInterface);
            ClassDescriptor searchCriteria = referenceDescriptor.getSearchCriteria();
            if (searchCriteria != null) {
                importGenerator.addImport(searchCriteria.getFQN());            
            } 

        }                

        //Logger.debug(this, "generating imports for: " + objectDescriptor.getImplementationName().getFQN()); 
        //System.out.println("generating imports for: " + objectDescriptor.getImplementationName().getFQN()); 
        Model model = objectDescriptor.getModel();
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS || field.getType() == FieldTypeEnum.ENUM) {
                ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
                if (childObjectDesciptor != null) {
                    String daoInterface = childObjectDesciptor.getDAOInterface().getFQN();
                    Assert.check(daoInterface != null, "daoInterface != null");
					importGenerator.addImport(daoInterface);            
                }

                ClassDescriptor classDescriptor = field.getClassDescriptor();
				importGenerator.addImport(classDescriptor.getFQN());                    
            }
        }                
        
		importGenerator.addImport("java.io.Serializable");
		importGenerator.addImport("java.util.ArrayList");
		importGenerator.addImport("java.util.Date");
		importGenerator.addImport("java.util.HashMap");
		importGenerator.addImport("java.util.Iterator");
		importGenerator.addImport("java.util.List");
		importGenerator.addImport("java.util.Map");
		
		code.add(importGenerator.getImports(objectDescriptor.getImplementationName().getPackage()));
		code.addLine();
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        code.addLine();
        String abstractDeclaration = "";
        List<Method> methodList = objectDescriptor.getMethods();
        if (methodList.size() > 0) {
            abstractDeclaration = "abstract ";
        }
        code.addLine("public " + abstractDeclaration + "class " + getClassName() + " extends AbstractValueObject implements " + objectDescriptor.getValueObjectInterface().getClassName() + ", Serializable, Displayable {");

        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            code.addLine("    // " + baseObjectDescriptor.getValueObjectInterface().getFQN() + " declarations");
            generateDeclarations(baseObjectDescriptor );
        }
        // This is here to fix a problem with junit calling remote interfaces
        // TODO: Let the vm generate serialVersionUID if possible.
        code.addLine("    final static long serialVersionUID = 1L;");
        code.addLine();        
        
        code.addLine("    // " + objectDescriptor.getValueObjectInterface().getFQN() + " declarations");
        generateDeclarations(objectDescriptor);
        code.addLine();        

        generateIsMultiTenant(objectDescriptor);
        
        if (baseObjectDescriptor != null) {
            code.addLine("    // " + baseObjectDescriptor.getValueObjectInterface().getFQN() + " interfaces");
            generateSettersAndGetters(baseObjectDescriptor );
            code.addLine();        
        }
        
        code.addLine("    // " + objectDescriptor.getValueObjectInterface().getFQN() + " interfaces");
        generateSettersAndGetters(objectDescriptor);
        code.addLine();        
        
        generateGetReferencedObjects();
        
        if (objectDescriptor.getCloneable()) {
            code.addLine("    // Cloneable");
            code.addLine("    public Object clone () {");
            code.addLine("        Object clone = null;");
            code.addLine("        try { ");
            code.addLine("            clone = super.clone();");
            code.addLine("        } catch (Exception e) {");
            code.addLine("            return new RuntimeException(e);");
            code.addLine("        }");
            code.addLine("        return clone;");            
            code.addLine("    }");

        } 

        
        generateDisplayMethods();
        
        code.addLine("}");
    }

    private void generateDeclarations(ObjectDescriptor objectDescriptor) {
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS) {
                code.addLine("    protected " + StringUtil.padString(field.getClassDescriptor().getClassName(), DECLARE_PADDING) + toJavaVariableName(field.getName()) + ";");
                if (field.getPersisted() && objectDescriptor.getPersisted()) { 
                    code.addLine("    protected " + StringUtil.padString("Identity", DECLARE_PADDING) + toJavaVariableName(field.getName()) + "Id;");
                }
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                    code.addLine("    protected " + StringUtil.padString(field.getClassDescriptor().getClassName(), DECLARE_PADDING) + toJavaVariableName(field.getName()) + ";");
            } else {                
                code.addLine("    protected " + StringUtil.padString(field.getJavaType(), DECLARE_PADDING) + toJavaVariableName(field.getName()) + ";");
            }
        }
        for (ReferenceDescriptor reference : objectDescriptor.getReferences()) {
            code.addLine("    protected " + StringUtil.padString(reference.getClassDescriptor().getClassName(), DECLARE_PADDING) + toJavaVariableName(reference.getName()) + ";");
        }
    }
    
    private void generateIsMultiTenant(ObjectDescriptor objectDescriptor) {
        code.addLine("    /** ");
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("     * Returns true to indicate the database table behind this class is multi-tenant.");
        } else {
            code.addLine("     * Returns false to indicate the database table behind this class is NOT multi-tenant.");
        }
        code.addLine("     */");
        code.addLine("    @Override");
        code.addLine("    public boolean getIsMultiTenant() {");
        code.addLine("        return " + (objectDescriptor.getMultiTenant() ? "true;" : "false;"));
        code.addLine("    }");
		code.addLine();    	
    }    
    
    private void generateSettersAndGetters(ObjectDescriptor objectDescriptor) {
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS && field.getPersisted() && objectDescriptor.getPersisted()) {
                generateFieldGetter(field);
                generateFieldSetter(field);            
                generateIdGetter(field);
                generateIdSetter(objectDescriptor, field);            
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // todo: for now only supporting strings
                FieldDescriptor joinedField = field.getJoinedFieldDescriptor();
                generateFieldGetter(joinedField, field.getName());
                generateFieldSetter(joinedField, field.getName());            
            } else {
                generateFieldGetter(field);
                generateFieldSetter(field);            
            }            
            
        }

        for (ReferenceDescriptor reference : objectDescriptor.getReferences()) {
            generateReferenceGetter(reference);
            generateReferenceSetter(reference);
        }
    }
    
    private void generateFieldGetter(FieldDescriptor field) {
        generateFieldGetter(field, field.getName());
    }

    private void generateFieldGetter(FieldDescriptor field, String fieldName) {
        String type = field.getJavaType();

        code.addLine("    @Override");
        code.addLine("    public " + type + " get" + fieldName + "() {");
        
        // UNDONE: should expand this exceptions to all getters,but to do that we need to allow DAOs alternative access.
        if (field.getType() == FieldTypeEnum.CLASS && field.getPersisted() && objectDescriptor.getPersisted()) {
            code.addLine("        if (" + toJavaVariableName(fieldName) + " == null && " + toJavaVariableName(fieldName) + "Id != null) {");
            code.addLine("            if (!getIsJITLoadingEnabled()) {");
            code.addLine("                throw new ObjectNotLoadedException(\"Object " + fieldName + " is not loaded\");");            
            code.addLine("            }");
            
            Model model = objectDescriptor.getModel();
            ObjectDescriptor childObjectDesciptor = model.findObject(field.getClassDescriptor().getFQN());
            if (childObjectDesciptor != null) {               
                String daoInterface = childObjectDesciptor.getDAOInterface().getClassName();
                String daoVariableName = childObjectDesciptor.getDAOInterface().getJavaVariableName();
                String childValueClass = field.getClassDescriptor().getClassName();

                String valueObjectIdName = field.getJavaVariableName() + "Id";
                
                // lookupDataDAO = (LookupDataDAO)DataAccessLocator.findDAO("com.modelgenerated.lookup.LookupData");
                code.addLine("            " + daoInterface + " " + daoVariableName + " = (" + daoInterface + ")DataAccessLocator.findDAO(\"" + field.getClassDescriptor().getFQN() + "\");");                

                //code.addLine("            UserContext userContext = new UserContext();");                
                //code.addLine("            userContext.setTenantId(tenantId);");                
                code.addLine("            TransactionContext transactionContext = new TransactionContext(this.getUserContext());");

                code.addLine("            Map<Identity,ValueObject> loadedObjects = LoadedObjectVisitor.getLoadedObjects(this);");                
                // LookupData lookupData = (ContactInfo)contactInfoDAO.find(transactionContext, SubObjectHelper.getSubObjectId(company, "ContactInfoId")));
                code.addLine("            try {");                
                code.addLine("                " + toJavaVariableName(fieldName) + " = (" + childValueClass + ")" + daoVariableName + ".find(transactionContext, " + valueObjectIdName + ", loadedObjects);");                
                code.addLine("            } finally {");                
                code.addLine("                transactionContext.close();");                
                code.addLine("            }");                
                code.addLine("            // 'if (... != null)' to handle broken links ");                
                code.addLine("            if (" + toJavaVariableName(fieldName) + " != null) {");
                code.addLine("                " + toJavaVariableName(fieldName) + ".addUnresolvedReference(this);");
                code.addLine("            }");                

            }
                    
            
            code.addLine("        }");
        }
        code.addLine("        return " + toJavaVariableName(fieldName) + ";");
        code.addLine("    }");
    }
    private void generateFieldSetter(FieldDescriptor field) {
        generateFieldSetter(field, field.getName());
    }            

    private void generateFieldSetter(FieldDescriptor field, String fieldName) {            

        String type = field.getJavaType();
        String propertyName = toJavaVariableName(fieldName);
        String newParameterName = "new" + fieldName;
        
        code.addLine("    @Override");
        code.addLine("    public void set" + fieldName + "(" + type + " " + newParameterName + ") {");
        
        // TODO: are there redundant tests here?
        if (objectDescriptor.getPersisted() && field.getPersisted()) {
            if (field.getType() == FieldTypeEnum.INT) {
                code.addLine("        if (" + newParameterName + " != " + propertyName + " ) { ");   
                code.addLine("            isDirty = true;");
                code.addLine("        }");
            } else if (field.getType() == FieldTypeEnum.CLASS) {
                String oldObjectId = propertyName + "Id";
                code.addLine("        if (!same(" + oldObjectId + ", " + newParameterName + " )) { ");   
                code.addLine("            isDirty = true;");
                code.addLine("        }");
            } else {
                code.addLine("        if (!same(" + newParameterName + ", " + propertyName + " )) { ");   
                code.addLine("            isDirty = true;");
                code.addLine("        }");
            }             
        }
        code.addLine("        " + toJavaVariableName(fieldName) + " = new" + fieldName + ";");
        if (field.getType() == FieldTypeEnum.CLASS && field.getPersisted() && objectDescriptor.getPersisted()) {
            code.addLine("        if (" + newParameterName + " == null) {");
            code.addLine("            " + propertyName + "Id = null;");            
            code.addLine("        } else {");
            code.addLine("            " + propertyName + ".addUnresolvedReference(this);");
            code.addLine("        }");
        }
        code.addLine("    }");

        if (field.getType() == FieldTypeEnum.ENUM) {
            // also create a string setter for the DAO to use.
            code.addLine("    public void set" + fieldName + "(String " + newParameterName + ") {");
            if (objectDescriptor.getPersisted() && field.getPersisted()) {
                code.addLine("        if (!same(" + type + ".get(" + newParameterName + "), " + propertyName + " )) { ");   
                code.addLine("            isDirty = true;");
                code.addLine("        }");
            }
            
            code.addLine("        " + propertyName + " = " + type + ".get(" + newParameterName + ");" );
            code.addLine("    }");

            code.addLine("    @Override");
            code.addLine("    public String get" + fieldName + "Key() {");
            code.addLine("        if (" + propertyName + " != null) {" );
            code.addLine("            return " + propertyName + ".getValue();");
            code.addLine("        }");
            code.addLine("        return null;");
            code.addLine("    }");

            code.addLine("    @Override");
            code.addLine("    public String get" + fieldName + "Display() {");
            code.addLine("        if (" + propertyName + " != null) {" );
            code.addLine("            return " + propertyName + ".getDisplay();");
            code.addLine("        }");
            code.addLine("        return null;");
            code.addLine("    }");

        }
    }
    
    private void generateReferenceGetter(ReferenceDescriptor reference) {
        String variableName = toJavaVariableName(reference.getName());
        //String variableClass = reference.getTargetClass().getClassName();
        
        ObjectDescriptor childObjectDesciptor = reference.getTargetObjectDescriptor();

        String daoInterface = childObjectDesciptor.getDAOInterface().getClassName();
        String daoVariableName = childObjectDesciptor.getDAOInterface().getJavaVariableName();
        
        String referenceClassType = reference.getJavaType();
        
        code.addLine("    @Override");
        code.addLine("    public " + referenceClassType + " get" + reference.getName() + "() {");

        if (objectDescriptor.getPersisted()) {
            code.addLine("        if (" + variableName + " == null) {");
            code.addLine("            if (isNew) {");
            code.addLine("                " + variableName + " = (" + referenceClassType + ")Factory.createObject(" + referenceClassType + ".class);");
            code.addLine("            } else if (getIsJITLoadingEnabled()) {");
            // Example: LookupDataDAO lookupDataDAO = (LookupDataDAO)DataAccessLocator.findDAO("com.modelgenerated.lookup.LookupData");
            code.addLine("                " + daoInterface + " " + daoVariableName + " = (" + daoInterface + ")DataAccessLocator.findDAO(\"" + reference.getTargetClass().getFQN() + "\");");                
    
            //code.addLine("                UserContext userContext = new UserContext();");                
            //code.addLine("                userContext.setTenantId(tenantId);");                
            code.addLine("                TransactionContext transactionContext = new TransactionContext(this.getUserContext());");
            
            code.addLine("                Map<Identity,ValueObject> loadedObjects = LoadedObjectVisitor.getLoadedObjects(this);");                
            code.addLine("                try {");
            String targetMethod = reference.getTargetMethod();                
            if (!StringUtil.isEmpty(targetMethod)) {
                code.addLine("                    " + variableName + " = " + daoVariableName + "." + targetMethod + "(transactionContext, this, loadedObjects);");                
            } else {
                ClassDescriptor searchCriteria = reference.getSearchCriteria();
                if (searchCriteria != null) { 
                    String searchCriteriaClass = searchCriteria.getClassName();
                    String searchCriteriaVariableName = searchCriteria.getJavaVariableName();

                    code.addLine("                    " + searchCriteriaClass + "  " + searchCriteriaVariableName + " = new " + searchCriteriaClass + "();");
                    code.addLine("                    " + searchCriteriaVariableName + ".setParentObject(this);");
                    code.addLine("                    " + variableName + " = " + daoVariableName + ".search(transactionContext, " + searchCriteriaVariableName + ", false);");                
                }   
            }


            code.addLine("                } finally {");                
            code.addLine("                    transactionContext.close();");                
            code.addLine("                }");                
            code.addLine("            } else {");
            code.addLine("                throw new ObjectNotLoadedException(\"Object " + reference.getName() + " is not loaded\");");            
            code.addLine("            }");
            code.addLine("        }");
        } else {
            code.addLine("        if (" + variableName + " == null) {");
            code.addLine("            " + variableName + " = (" + referenceClassType + ")Factory.createObject(" + referenceClassType + ".class);");
            code.addLine("        }");            
        }
        
        code.addLine("        return " + variableName + ";");
        code.addLine("    }");
    }
    private void generateReferenceSetter(ReferenceDescriptor reference) {            
        code.addLine("    @Override");
        code.addLine("    public void set" + reference.getName() + "(" + reference.getJavaType() + " new" + reference.getName() + ") {");
        code.addLine("        " + toJavaVariableName(reference.getName()) + " = new" + reference.getName() + ";");
        code.addLine("    }");
    }

    private void generateIdGetter(FieldDescriptor field) {
        code.addLine("    @Override");
        code.addLine("    public Identity get" + field.getName() + "Id() {");
        code.addLine("        if (" + toJavaVariableName(field.getName()) + " != null) {");
        code.addLine("            return " + toJavaVariableName(field.getName()) + ".getId();");
        code.addLine("        } else {");
        code.addLine("            return " + toJavaVariableName(field.getName()) + "Id;");
        code.addLine("        }");
        code.addLine("    }");
    }
    private void generateIdSetter(ObjectDescriptor objectDescriptor, FieldDescriptor field) {            
        code.addLine("    /**");
        code.addLine("     * Sets the " + field.getName() + "Id.");
        // code.addLine("     * <p>This is protected as it is only set from subObjectHelper using reflection. ");
        code.addLine("     * <p>It is not part of the " + objectDescriptor.getValueObjectInterface() + ".");
        code.addLine("     */ ");
        code.addLine("    public void set" + field.getName() + "Id(Identity new" + field.getName() + "Id) {");
        code.addLine("        if (!same(" + toJavaVariableName(field.getName()) + "Id, new" + field.getName() + "Id )) { ");   
        code.addLine("            isDirty = true;");
        code.addLine("        }");
        code.addLine("        " + toJavaVariableName(field.getName()) + "Id = new" + field.getName() + "Id;");
        code.addLine("    }");
    }
    private void generateGetReferencedObjects() {
        code.addLine("    @Override");
        code.addLine("    public Iterator<ValueObject> getReferencedObjects() {");
        if (objectDescriptor.getPersisted()) {
            code.addLine("        List<ValueObject> referencedList = new ArrayList<ValueObject>();");
            code.addLine();
            code.addLine("        referencedList.addAll(unresolvedReferences.values());");

            for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
                if (field.getType() == FieldTypeEnum.CLASS) {
                    String fieldVariableName = toJavaVariableName(field.getName());
                    code.addLine("        if (" + fieldVariableName + " != null) {");
                    code.addLine("            referencedList.add(" + fieldVariableName + ");");            
                    code.addLine("        }");
                    code.addLine();
                }
            }
    
            for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
                if (referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_MANY) { 
                    String listName = toJavaVariableName(referenceDescriptor.getName());
                    String varType = referenceDescriptor.getTargetClass().getClassName();
                    String varName = referenceDescriptor.getTargetClass().getJavaVariableName();
                    /*
                    code.addLine("        if (" + varName + " != null) {");
                    code.addLine("            i = " + varName + ".iterator();");
                    code.addLine("            while (i.hasNext()) {");
                    code.addLine("                referencedList.add(i.next());");            
                    code.addLine("            }");
                    code.addLine("        }");
                    */          
                    code.addLine("        if (" + listName  + " != null) {");
                    code.addLine("            for (" + varType + " " + varName + " : " + listName + ") {");
                    code.addLine("                referencedList.add(" + varName + ");");            
                    code.addLine("            }");
                    code.addLine("        }");
                
                } else if (referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_ONE) { 
                    String varName = toJavaVariableName(referenceDescriptor.getName());
                    code.addLine("        if (" + varName + " != null) {");
                    code.addLine("            referencedList.add(" + varName + ");");            
                    code.addLine("        }");          
                
                } else {
                    Assert.check(false, "Unsupported referenceTypeEnum");
                }
            }
            code.addLine("        return referencedList.iterator();");
        } else {
            code.addLine("        return null;");                   
        }
                
        code.addLine("    }");
    }
    
    private void generateDisplayMethods() {
        code.addLine();    
        code.addLine("    // Displayable methods");    
        code.addLine("    @Override");
        code.addLine("    public String display() {");
        code.addLine("        return display (\"\");");
        code.addLine("    }");
        code.addLine();    
        code.addLine("    @Override");
        code.addLine("    public String display(String objectDescription) {");
        code.addLine("        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();");
        code.addLine("        return display (objectDescription, 0, 0, displayedObjects);");
        code.addLine("    }");
        code.addLine();
        code.addLine("    @Override");
        code.addLine("    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {");
        code.addLine("        DisplayBuffer displayBuffer = DisplayBuffer.newInstance(\"" + getClassName() + "\", objectDescription, level, maxLevels);");
        code.addLine("        if (displayBuffer == null) {");
        code.addLine("            return \"\";");
        code.addLine("        }");
        code.addLine("        if (this.getId() != null && displayedObjects.get(this.getId()) != null) {");
        code.addLine("            displayBuffer.addLine(level+1, \"id: \" + id);");
        code.addLine("            return displayBuffer.toString();");
        code.addLine("        }");        
        code.addLine("        displayedObjects.put(this.getId(), this);");        

        code.addLine("        displayBuffer.addLine(level+1, \"id: \" + id);");
        code.addLine("        displayBuffer.addLine(level+1, \"isDirty: \" + isDirty);");
        code.addLine("        displayBuffer.addLine(level+1, \"isDeleted: \" + isDeleted);");
        code.addLine("        displayBuffer.addLine(level+1, \"isNew: \" + isNew);");
        code.addLine("        displayBuffer.addLine(level+1, \"createdDate: \" + DateUtil.formatDateTime(createdDate));");
        code.addLine("        displayBuffer.addLine(level+1, \"createdBy: \" + createdBy);");
        code.addLine("        displayBuffer.addLine(level+1, \"modifiedDate: \" + DateUtil.formatDateTime(modifiedDate));");
        code.addLine("        displayBuffer.addLine(level+1, \"modifiedBy: \" + modifiedBy);");
        code.addLine("        displayBuffer.addLine(level+1, \"isJITLoadingEnabled: \" + getIsJITLoadingEnabled());");

        code.addLine("        displayBuffer.addLine(level+1, \"unresolvedReferences: \");");
        code.addLine("        Iterator<ValueObject> i = unresolvedReferences.values().iterator();");
        code.addLine("        while (i.hasNext()) {");
        code.addLine("            ValueObject childObject = (ValueObject)i.next();");
        code.addLine("            displayBuffer.addLine(level+2, \"id: \" + childObject.getId() + \" class \" + childObject.getClass().getName());");
        code.addLine("        }");


        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            for (FieldDescriptor field : baseObjectDescriptor.getFields()) {
                if (field.getType() != FieldTypeEnum.CLASS) {
                    code.addLine("        displayBuffer.addLine(level+1, \"" + toJavaVariableName(field.getName()) + ": \" + " + toJavaVariableName(field.getName()) + ");");
                }
            }
        }

        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() != FieldTypeEnum.CLASS) {
                code.addLine("        displayBuffer.addLine(level+1, \"" + toJavaVariableName(field.getName()) + ": \" + get" + field.getName() + "());");
            }
        }
        
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            if (field.getType() == FieldTypeEnum.CLASS && field.getPersisted()) {
                String varName = toJavaVariableName(field.getName());
                code.addLine("        if (" + varName + " == null) {");
                if (field.getPersisted() && objectDescriptor.getPersisted()) {
                    code.addLine("            displayBuffer.addLine(level+1, \"" + varName + "Id: \" + " + varName + "Id);");
                } else {
                    code.addLine("            displayBuffer.addLine(level+1, \"" + varName + ": \" + " + varName + ");");
                }
                code.addLine("        } else {");
                code.addLine("            displayBuffer.append(" + varName + ".display(\"" + varName + "\", level+1, maxLevels, displayedObjects));");
                code.addLine("        }");
            }
        }

        for (ReferenceDescriptor referenceDescriptor : objectDescriptor.getReferences()) {
            if ((referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_MANY) 
                || (referenceDescriptor.getType() == ReferenceTypeEnum.ONE_TO_ONE)) { 
                String varName = toJavaVariableName(referenceDescriptor.getName());
                code.addLine("        if (" + varName + " == null) {");
                code.addLine("            displayBuffer.addLine(level+1, \"" + varName + ": \" + " + varName + ");");
                code.addLine("        } else {");
                code.addLine("            displayBuffer.append(" + varName + ".display(\"" + varName + "\", level+1, maxLevels, displayedObjects));");
                code.addLine("        }");          
            } else {
                Assert.check(false, "Unsupported referenceTypeEnum");
            }
        }        
        
        code.addLine("        return displayBuffer.toString();");
        code.addLine("    }");
    }    
    
    protected String getFullyQualifiedName() {
        Assert.check(objectDescriptor != null, "objectDescriptor != null");
        ClassDescriptor implementation = objectDescriptor.getImplementationName();
        Assert.check(implementation != null, "implementation != null");
        return implementation.getFQN();
    }
    
    
    
}
