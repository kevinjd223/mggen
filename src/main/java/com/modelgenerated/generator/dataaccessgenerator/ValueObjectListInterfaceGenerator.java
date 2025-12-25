/*
 * ValueObjectGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.java.CommentGenerator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.Method;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.Parameter;
import com.modelgenerated.modelmetadata.Prototype;
import com.modelgenerated.util.StringUtil;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class ValueObjectListInterfaceGenerator extends JavaCodeBaseGenerator  {
    /** Creates a new instance of ValueObjectGenerator */
    public ValueObjectListInterfaceGenerator() {
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
		importGenerator.addImport("com.modelgenerated.foundation.dataaccess.ValueObjectList");
		
        for (Method method : objectDescriptor.getListMethods()) {
            Prototype prototype = method.getPrototype();
			ClassDescriptor returnType = prototype.getReturnType(); 

            importGenerator.addImport(returnType.getFQN());
            
            for (Parameter parameter : prototype.getParameters()) {
                ClassDescriptor parameterType = parameter.getType();
                importGenerator.addImport(parameterType.getFQN());
            }
        }

        code.add(importGenerator.getImports(objectDescriptor.getImplementationName().getPackage()));
		code.addLine();
    }

    protected void generateClassJavaDocs() {
        StringBuilder comment = new StringBuilder();
        comment.append("Model generated list class for the class {@link ");
        comment.append(objectDescriptor.getValueObjectInterface().getFQN());
        comment.append(" ");
        comment.append(objectDescriptor.getValueObjectInterface().getClassName()); 
        comment.append("}.");
        code.addLine();
        CommentGenerator.writeJavaDocComment(code, "", comment.toString());
    }
    
    protected void generateClass() {
        code.addLine();
        code.addLine("public interface " + getClassName() + " extends ValueObjectList<" + objectDescriptor.getValueObjectInterface().getClassName() + ">");

        Model model = objectDescriptor.getModel();

        List implementsList = objectDescriptor.getImplementsList();
        Iterator i = implementsList.iterator();
        while (i.hasNext()) {
            ClassDescriptor implementsInterface = (ClassDescriptor)i.next();
            
            ObjectDescriptor implementsDescriptor = model.findObject(implementsInterface.getFQN());
            if (implementsDescriptor != null) {  
                code.add(", " + implementsDescriptor.getListInterface().getClassName());            
            } 
        }

        code.addLine(" {");

        generateOperations();
            
        code.addLine("}");
    }

    private void generateOperations() {
        List methodList = objectDescriptor.getListMethods();
        Iterator i = methodList.iterator();
        while (i.hasNext()) {
            Method method = (Method)i.next();
            String protoType = method.getMethodNameAndParameters();
            code.addLine("    " + protoType + ";");
        }
    }
    
    
    protected String getFullyQualifiedName() {
        return objectDescriptor.getListInterface().getFQN();
    }
    
}
