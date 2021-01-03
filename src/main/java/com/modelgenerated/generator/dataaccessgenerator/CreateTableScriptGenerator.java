/*
 * CreateTableScriptGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public abstract class CreateTableScriptGenerator {
    CodeBuffer code;
    Model model;
    
    /** Creates a new instance of ValueObjectGenerator */
    public CreateTableScriptGenerator() {
    }
    
    protected abstract String getEOL();
    protected abstract String getPlatformDir();
    protected abstract String getScriptName();
    protected abstract void generateScriptCall(String tableName);
        
    public void generate(Model model, String scriptPath) {
        this.model = model;
        
        code = new CodeBuffer(getEOL());
        generateFileContent();
        Logger.debug(this, code.toString());        

        
        String packagePath = scriptPath + getPlatformDir() + "/";
        Logger.debug(this, "packagePath " + packagePath);
                
        File directory = new File(packagePath);
        // boolean bCreated = 
        directory.mkdirs();        

        String filePath = packagePath + getScriptName();
        FileUtil.writeFile(filePath, code.toString());

    }

    
    private void generateFileContent() {
        generateHeader();
        generateScriptCalls();        
    }

    
    protected void generateHeader() {
        code.addLine("# createGeneratedTables.sh - Generated table creation script");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("# " + copyrightNotice);
        }
        code.addLine();
    }
    
    protected void generateScriptCalls() {
        Map<String,ObjectDescriptor> objectDescriptorMap = model.getObjects();
        
        for (ObjectDescriptor objectDescriptor : objectDescriptorMap.values()) {
            
            if (objectDescriptor.getPersisted()) { 
                generateScriptCall(objectDescriptor.getTableName());            
            }
        }       
    }

    
}
