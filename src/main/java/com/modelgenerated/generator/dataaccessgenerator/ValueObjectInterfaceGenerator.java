/*
 * ValueObjectInterfaceGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.java.CommentGenerator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.Parameter;
import com.modelgenerated.modelmetadata.Prototype;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;
import java.util.Iterator;
import java.util.List;

/**
 * Generates value object interfaces.  
 */
public class ValueObjectInterfaceGenerator extends JavaCodeBaseGenerator {
   
    /** Creates a new instance of ValueObjectGenerator */
    public ValueObjectInterfaceGenerator() {
    }

	/* 
	 * 
	 * @author kevin
	 *
	 * override to generate if it has an interface.
	 */

	protected boolean shouldGenerate() {
		Logger.debug(this, "in base class shouldGenerate()");
		if (objectDescriptor.getValueObjectInterface() == null) {
			return false;
		}
		Model model = objectDescriptor.getModel();
        
		String packagesToGenerate = model.getPackagesToGenerate();
		Assert.check(packagesToGenerate != null, "packagesToGenerate != null");
        
		String packageName = objectDescriptor.getValueObjectInterface().getPackage(); 
		Assert.check(packageName != null, "packageName != null");        
        
		return packageName.startsWith(packagesToGenerate);
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
        
		ImportGenerator importGenerator = new ImportGenerator();
        
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.FieldAttribute");
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ValueObject");
		importGenerator.addImport("com.modelgenerated.foundation.debug.Displayable");
        //importGenerator.addImport("com.modelgenerated.foundation.debug.DisplayUtil");
		importGenerator.addImport("com.modelgenerated.foundation.identity.Identity");
        if (objectDescriptor.hasFieldType(FieldTypeEnum.DATE)
                || objectDescriptor.hasFieldType(FieldTypeEnum.DATETIME)) {
					importGenerator.addImport("java.util.Date");
        }
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            ClassDescriptor type = field.getClassDescriptor();
            if (type != null && type.getFQN() != null) {
                importGenerator.addImport(type.getFQN());                    
            } 
        }
        for (ClassDescriptor classDescriptor : objectDescriptor.getClassFieldClasses()) {
			importGenerator.addImport(classDescriptor.getFQN());                    
        }
        for (ClassDescriptor classDescriptor : objectDescriptor.getReferencedClasses()) {
			importGenerator.addImport(classDescriptor.getFQN());                    
        }
        for (ClassDescriptor classDescriptor : objectDescriptor.getImplementsList()) {
			importGenerator.addImport(classDescriptor.getFQN());
        }
        for (Method method : objectDescriptor.getMethods()) {
            Prototype prototype = method.getPrototype();
			ClassDescriptor returnType = prototype.getReturnType(); 

            importGenerator.addImport(returnType.getFQN());
            
            for (Parameter parameter : prototype.getParameters()) {
                ClassDescriptor parameterType = parameter.getType();
                importGenerator.addImport(parameterType.getFQN());
            }
        }
        importGenerator.addImport("java.util.Date");
        
        
		code.add(importGenerator.getImports(objectDescriptor.getValueObjectInterface().getPackage()));
		code.addLine();
        
    }

    @Override
    protected void generateClassJavaDocs() {
        code.addLine();
        CommentGenerator.writeJavaDocComment(code, "", objectDescriptor.getDescription());
    }
    
    @Override
    protected void generateClass() {
        code.addLine();
        code.add("public interface " + getClassName() + " extends ValueObject, Displayable");
        if (objectDescriptor.getCloneable()) { 
            code.add(", Cloneable");
        }
        for (ClassDescriptor implementsInterface : objectDescriptor.getImplementsList()) {
            code.add(", " + implementsInterface.getClassName());            
        }
        
        ObjectDescriptor baseObjectDescriptor = objectDescriptor.getBaseObjectDescriptor();
        if (baseObjectDescriptor != null) {
            code.add(", " + baseObjectDescriptor.getValueObjectInterface().getClassName());
        }

        code.addLine(" {");
        
        generateAttributeConstants();
        generateSettersAndGetters();
        generateMethods();

        if (objectDescriptor.getCloneable()) {
            code.addLine("    // Cloneable");
            code.addLine("    Object clone();");
            code.addLine();
        } 
        
        code.addLine("}");
    }

    private void generateAttributeConstants() {
        for (FieldDescriptor field : objectDescriptor.getFieldsSorted()) {
            if (field.getType() != FieldTypeEnum.CLASS) {

                String fieldName = field.getName();
                String columnReference = null;
                if (!StringUtil.isEmpty(field.getSql())) {
                	columnReference = "\"" + field.getColumnName() + "\"";              	
                } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                    //String joinFieldName = field.getJoinField();
                    //FieldDescriptor joinField = objectDescriptor.findField(joinFieldName);
                    String alias = field.getAlias();                    
                    columnReference = "\"" + alias + "." + field.getRealColumnName() + "\"";
                } else if (field.getType() == FieldTypeEnum.COALESCE) {
                    //String joinFieldName = field.getJoinField();
                    //FieldDescriptor joinField = objectDescriptor.findField(joinFieldName);
                    columnReference = "\"" + field.getName() + "\"";                
                }
                int length = field.getType() == FieldTypeEnum.STRING ? field.getSize() : 0;
                
                StringBuilder str = new StringBuilder();
                str.append("    FieldAttribute ATTRIB_").append(fieldName.toUpperCase());
                str.append(" = new FieldAttribute(\"").append(fieldName).append("\", ");  
                str.append(columnReference).append(", ");  
                str.append(length).append(");");  

                code.addLine(str.toString());  
            } 
        }
    }

    private void generateSettersAndGetters() {
        Logger.debug(this, "object name" + objectDescriptor.getValueObjectInterface().getFQN());
        for (FieldDescriptor field : objectDescriptor.getFields()) {
            
            if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                FieldDescriptor joinedField = field.getJoinedFieldDescriptor();
                generateFieldGetter(joinedField, field.getName());
                generateFieldSetter(joinedField, field.getName());            
            	//generateFieldGetter(joinedField, field.getName());
                //generateFieldSetter(joinedField, field.getName());            
            } else {
                generateFieldGetter(field);
                generateFieldSetter(field);            
            }           

        }
        for (ReferenceDescriptor reference : objectDescriptor.getReferences()) {
            String type = reference.getClassDescriptor().getClassName();
            CommentGenerator.writeJavaDocComment(code, "    ", reference.getDescription());
            code.addLine("    " + type + " get" + reference.getName() + "();");
            code.addLine("    void set" + reference.getName() + "(" + type + " new" + reference.getName() + ");");
        }
    }
    private void generateFieldGetter(FieldDescriptor field) {
        generateFieldGetter(field, field.getName());
    }

    private void generateFieldGetter(FieldDescriptor field, String fieldName) {
        String type = field.getJavaType();

        CommentGenerator.writeJavaDocComment(code, "    ", field.getDescription());

        Logger.debug(this, "field name" + field.getJavaVariableName());
            
        code.addLine("    " + type + " get" + fieldName + "();");
        if (field.getType() == FieldTypeEnum.CLASS && field.getPersisted() && objectDescriptor.getPersisted()) {
            code.addLine("    Identity get" + fieldName + "Id();");
        } 
    }    
    private void generateFieldSetter(FieldDescriptor field) {
        if (field.isDeprecated()) {
            CommentGenerator.writeJavaDocComment(code, "    ", "@deprecated");
        }
        generateFieldSetter(field, field.getName());
    }

    private void generateFieldSetter(FieldDescriptor field, String fieldName) {
        String type = field.getJavaType();
        
        if (field.getType() != FieldTypeEnum.READONLYJOIN) {
            code.addLine("    void set" + fieldName + "(" + type + " new" + field.getName() + ");");
        }
        if (field.getType() == FieldTypeEnum.ENUM) {
            code.addLine("    void set" + fieldName + "(String new" + fieldName + ");");
            code.addLine("    String get" + fieldName + "Key();");
            code.addLine("    String get" + fieldName + "Display();");
        }
    }
    
    private void generateMethods() {
        for (Method method : objectDescriptor.getMethods()) {
            CommentGenerator.writeJavaDocComment(code, "    ", method.getDescription());
            String protoType = method.getMethodNameAndParameters();
            code.addLine("    " + protoType + ";");
        }
    }
    
    @Override
    protected String getFullyQualifiedName() {
        return objectDescriptor.getValueObjectInterface().getFQN();
    }
    
}
