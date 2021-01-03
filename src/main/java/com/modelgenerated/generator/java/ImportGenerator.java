/*
 * ImportGenerator.java
 *
 * Created on October 10, 2004, 8:50 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.java;


import com.modelgenerated.modelmetadata.ClassDescriptor;
import com.modelgenerated.util.Assert;
import java.util.Set;
import java.util.TreeSet;

/**
 * Generates a jave import list from a list of added imports. 
 *  
 * @author  kevind
 */
public class ImportGenerator {
    Set<String> importSet = new TreeSet<String>();
    
    /** Creates a new instance of ImportGenerator */
    public ImportGenerator() {
    }
    
    public void addImport(String importClassFqn) {
        Assert.check(importClassFqn != null, "importClassFqn != null");
        
        importSet.add(importClassFqn);
    }
    
    public String getImports(String packageName) {
        Assert.check(packageName != null, "packageName != null");
        
        StringBuffer strBuf = new StringBuffer();
        for (String importClassFqn : importSet) {
            ClassDescriptor importClassDescriptor = new ClassDescriptor(importClassFqn);
            Assert.check(importClassDescriptor != null, "importClassDescriptor != null");
            String importPackage = importClassDescriptor.getPackage();
            
            if (!importPackage.equals(packageName) 
                    && !importPackage.equals("java.lang") 
                    && !importClassFqn.equals("boolean") 
                    && !importClassFqn.equals("byte") 
                    && !importClassFqn.equals("double") 
                    && !importClassFqn.equals("int") 
                    && !importClassFqn.equals("long") 
                    && !importClassFqn.equals("void") 
                	&& !importClassFqn.equals("")) { 
                strBuf.append("import ");
                strBuf.append(importClassFqn);
                strBuf.append(";\n");            
            }
        }
        return strBuf.toString();
    }
    
}
