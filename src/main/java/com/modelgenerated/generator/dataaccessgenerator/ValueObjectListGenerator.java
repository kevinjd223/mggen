/*
 * ValueObjectGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.util.StringUtil;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class ValueObjectListGenerator  extends JavaCodeBaseGenerator  {
    /** Creates a new instance of ValueObjectGenerator */
    public ValueObjectListGenerator() {
    }
   
    

    protected void generateHeader() {
        code.addLine("/* " + getClassName() + ".java");
        code.addLine("* Generated value object list code");
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
        String listInterfacePackage = objectDescriptor.getListInterface().getPackage();
        code.addLine("import " + objectDescriptor.getValueObjectInterface().getFQN() + ";");            
        if (!listInterfacePackage.equals(getPackageName())) {
            code.addLine("import " + objectDescriptor.getListInterface().getFQN() + ";");            
        }

        code.addLine("import com.modelgenerated.foundation.dataaccess.ValueObjectListBase;");
        code.addLine("import java.io.Serializable;");
    }

    protected void generateClassJavaDocs() {
    }
    
    protected void generateClass() {
        
        String abstractDeclaration = "";
        List methodList = objectDescriptor.getListMethods();
        if (methodList.size() > 0) {
            abstractDeclaration = "abstract ";
        }
        
        code.addLine();
        code.addLine("public " + abstractDeclaration + "class " + getClassName() + " extends ValueObjectListBase<" + objectDescriptor.getValueObjectInterface().getClassName() + "> implements " + objectDescriptor.getListInterface().getClassName() + ", Serializable {");

        code.addLine("}");
    }

    protected String getFullyQualifiedName() {
        return objectDescriptor.getListImplementation().getFQN();
    }
    
}
