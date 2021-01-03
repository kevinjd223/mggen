/*
 * MysqlRecreateIndexesGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import java.io.File;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.modelmetadata.IndexDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;


/**
 *
 * @author  kevind
 */
public class MysqlRecreateIndexesGenerator {
    CodeBuffer code;
    Model model;
    
    /** Creates a new instance of ValueObjectGenerator */
    public MysqlRecreateIndexesGenerator() {
    }
    
        
    public void generate(Model model, String scriptPath) {
        this.model = model;
        
        code = new CodeBuffer("\n");
        generateFileContent();
        Logger.debug(this, code.toString());        

        
        String packagePath = scriptPath + "mysql/tables";
        Logger.debug(this, "packagePath " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();        
        Logger.debug(this, "bCreated: " +bCreated);

        String filePath = packagePath + "recreateIndexesMysql.sql";
        FileUtil.writeFile(filePath, code.toString());

    }

    
    private void generateFileContent() {
        generateHeader();
        generateScriptCalls();        
    }

    
    protected void generateHeader() {
        code.addLine("/* recreateIndexesMysql.sql - generated index update code");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine(" * " + copyrightNotice);
        }
        code.addLine(" */");
        code.addLine();
    }
    
    protected void generateScriptCalls() {
        for (ObjectDescriptor objectDescriptor : model.getObjects().values()) {
            if (objectDescriptor.getPersisted()) { 
                code.addLine("/* " + objectDescriptor.getTableName());
                code.addLine("*/");
                
                code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " DROP PRIMARY KEY");
                code.addLine(";");
                code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " ADD PRIMARY KEY (tid, Id)");
                code.addLine(";");

                code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " DROP INDEX " + objectDescriptor.getTableName() + "IdIndex");
                code.addLine(";");
                code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " ADD INDEX " + objectDescriptor.getTableName() + "IdIndex(Id)");
                code.addLine(";");
                
                for (IndexDescriptor index : objectDescriptor.getIndicies()) {
                    createIndexWithoutTID(objectDescriptor.getTableName(), index.getIndexName(), index.getColumns());
                    createIndexWithTID(objectDescriptor.getTableName(), index.getIndexName(), index.getColumns(), objectDescriptor.getMultiTenant(), index.getUnique());
                }
            }
        }       
    }

    private void createIndexWithoutTID(String tableName, String indexName, String columns) {
        code.addLine("ALTER TABLE " + tableName + " DROP INDEX " + indexName);
        code.addLine(";");
        code.add    ("ALTER TABLE " + tableName + " ADD INDEX " + indexName + "(");
        /*
         * Don't include the tid because if the tid is not part of the "where" then this index won't be used. This may change in the  future if we
         * start usind TID for all joins.  
        if (objectDescriptor.getMultiTenant()) {
            code.add    ("tid, ");
        }
        */
		code.addLine(columns + ")");
        code.addLine(";");
    	
    }
    
    private void createIndexWithTID(String tableName, String indexName, String columns, boolean multiTenant, boolean unique) {
        code.addLine("ALTER TABLE " + tableName + " DROP INDEX " + indexName + "Tid");
        code.addLine(";");
        code.add    ("ALTER TABLE " + tableName + " ADD ");
        if (unique) {
            code.add    ("UNIQUE ");
        }
        code.add    ("INDEX " + indexName + "Tid(");
        if (multiTenant) {
            code.add    ("tid, ");
        }
		code.addLine(columns + ")");
        code.addLine(";");
    	
    }
    
    
    
}
