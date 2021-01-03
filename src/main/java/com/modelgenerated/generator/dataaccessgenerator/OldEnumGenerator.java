/*
 * EnumGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.java.CommentGenerator;
import com.modelgenerated.generator.java.ImportGenerator;
import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.ReferenceDescriptor;
import com.modelgenerated.modelmetadata.enums.EnumDescriptor;
import com.modelgenerated.modelmetadata.enums.EnumValueDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class OldEnumGenerator extends JavaCodeBaseGenerator {
    EnumDescriptor enumDescriptor;
   
    /** Creates a new instance of ValueObjectGenerator */
    public OldEnumGenerator() {
    }


    // TODO: Only sometimes inheriting implementation?
    public void generate(String rootPath, EnumDescriptor initEnumDescriptor) {
        Logger.debug(this, "*******************************");
        Logger.debug(this, "rootPath: " + rootPath);
        Logger.debug(this, "class: " + this.getClass().getName());
        //Logger.debug(this, initObjectDescriptor);        
        //Logger.debug(this, "*******************************");

        
        enumDescriptor = initEnumDescriptor;        
        model = enumDescriptor.getModel();        
        
        code = new CodeBuffer();
        Logger.debug(this, "generate");

        generateHeader(); 
        generatePackage();
        generateImports();
        generateClassJavaDocs();
        generateClass();

        Logger.debug(this, "done generate");
        
        //Logger.debug(this, code.toString());
        
        String relativePath = getPackageName().replaceAll("\\.", "/");
        String packagePath = rootPath + relativePath;
        String fileName = packagePath + "/" + getClassName() + ".java";
        Logger.debug(this, "packagePath: " + packagePath);
        Logger.debug(this, "fileName: " + fileName);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        FileUtil.writeFile(fileName, code.toString());

    }
    


	/* 
	 * 
	 * @author kevin
	 *
	 * override to generate if it has an interface.
	 */

	protected boolean shouldGenerate() {
		return true;
	}
    



    protected void generateHeader() {
        code.addLine("/* " + getClassName() + ".java");
        code.addLine("* Generated enum class");
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
        
        importGenerator.addImport("com.modelgenerated.foundation.dataaccess.EnumBase");
        importGenerator.addImport("com.modelgenerated.util.Assert");
        importGenerator.addImport("java.io.Serializable");
        importGenerator.addImport("java.util.ArrayList");
        importGenerator.addImport("java.util.List");
        
        
        code.add(importGenerator.getImports(enumDescriptor.getImplementation().getPackage()));
        code.addLine();
        
    }

    protected void generateClassJavaDocs() {
        code.addLine();
        CommentGenerator.writeJavaDocComment(code, "", enumDescriptor.getDescription());
    }
    
    
    protected void generateClass() {
        code.addLine();
        code.add("public class " + getClassName() + " extends EnumBase implements Comparable, Serializable ");
        code.addLine(" {");
        
        generateValues();
        generateList();
        generateConstructor();
        generateOtherMethods();
        
        code.addLine("}");
    }

    
    private void generateValues() {
        List valueList = enumDescriptor.getValues();
        Iterator i = valueList.iterator();
        while (i.hasNext()) {
            EnumValueDescriptor enumValueDescriptor = (EnumValueDescriptor)i.next();
            
            
            String enumValue = enumValueDescriptor.getKey();
            Assert.check(enumValue != null, "enumValue != null");
            String WHITESPACE = "\\s";
            String enumName = enumValue.toUpperCase().replaceAll(WHITESPACE, "");
            String enumDisplay = enumValueDescriptor.getDescription();
            code.addLine("    public static " + this.getClassName() + " " + enumName + " = new " + this.getClassName() + "(\"" + enumValue + "\", \"" + enumDisplay + "\");" );
        }
    }

    private void generateList() {
        code.add("    private static " + this.getClassName() + "[] list = {");
        
        List valueList = enumDescriptor.getValues();
        Iterator i = valueList.iterator();
        while (i.hasNext()) {
            EnumValueDescriptor enumValueDescriptor = (EnumValueDescriptor)i.next();
            
            String enumValue = enumValueDescriptor.getKey();
            String WHITESPACE = "\\s";
            String enumName = enumValue.toUpperCase().replaceAll(WHITESPACE, "");
            code.add(enumName);
            if (i.hasNext()) {
                code.add(", ");
            }
        }
        code.addLine("};");
        /*
        code.addLine("    private String value;");
        code.addLine("    private String display;");
        */
        code.addLine();
    }
    private void generateConstructor() {
        code.addLine("    private " + this.getClassName() + "(String initValue, String initDisplay) {");
        code.addLine("        super(initValue, initDisplay);");        
        code.addLine("    }");
        code.addLine();
    }
    private void generateOtherMethods() {
        code.addLine("    public static " + this.getClassName() + " get(String value) {");
        code.addLine("        for (int i = 0; i < list.length; i++) {");
        code.addLine("            if (list[i].value.equals(value)) {");
        code.addLine("                return (" + this.getClassName() + ")list[i];");
        code.addLine("            }");
        code.addLine("        }");
        code.addLine("        return null;");
        code.addLine("    }");
        code.addLine();
    
        code.addLine("    public static List getValueDisplayPairs() {");
        code.addLine("        List tempList = new ArrayList();");
        code.addLine("        for (int i = 0; i< list.length; i++) {");
        code.addLine("            tempList.add(list[i]);");
        code.addLine("        }");
        code.addLine("        return tempList;");
        code.addLine("    }");
        code.addLine();
        
        // maybe this can go in the base class.
        // should we also define equals()?
        code.addLine("    public int compareTo(Object o2) {");
        code.addLine("        Assert.check(o2 instanceof EnumBase, \"o2 instanceof EnumBase\");");
        code.addLine("");
        code.addLine("        int position1 = findPosintion(list, this);"); 
        code.addLine("        int position2 = findPosintion(list, o2);");
        code.addLine("");
        code.addLine("        return position1 - position2;");
        code.addLine("    }");
        
        code.addLine("    private int findPosintion(Object[] list, Object o) {");
        code.addLine("        for (int i = 0; i < list.length; i++) {");
        code.addLine("            if (o == list[i]) {");
        code.addLine("                return i;");
        code.addLine("            }");
        code.addLine("        }");
        code.addLine("        return -1;");         
        code.addLine("    }");


        /*
        code.addLine("    public String getDisplay() {");
        code.addLine("        return display;");
        code.addLine("    }");
        code.addLine("    public String getValue() {");
        code.addLine("        return value;");
        code.addLine("    }");
        code.addLine();
        code.addLine("    public String toString() {");
        code.addLine("        return value;");
        code.addLine("    }");
        code.addLine();
    
        */
    }


    
    protected String getFullyQualifiedName() {
        return enumDescriptor.getImplementation().getFQN();
    }
    
}
