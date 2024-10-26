/*
 * TableGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.CodeGenerator;
import com.modelgenerated.generator.GeneratorEventListener;
import com.modelgenerated.modelmetadata.ObjectDescriptor;
import com.modelgenerated.modelmetadata.FieldDescriptor;
import com.modelgenerated.modelmetadata.FieldTypeEnum;
import com.modelgenerated.modelmetadata.IndexDescriptor;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author  kevind
 */
public abstract class CreateTableGenerator implements CodeGenerator {
    CodeBuffer code;
    ObjectDescriptor objectDescriptor;
    Model model;
    GeneratorEventListener eventListener;

    
    /** Creates a new instance of ValueObjectGenerator */
    public CreateTableGenerator() {
    }
    
    protected abstract String getEOL();
    protected abstract String getStatementTerminator();
    protected abstract String getPlatformDir();
    protected abstract String getBinaryType();
    protected abstract String getDateType();
    protected abstract String getDateTimeType();
    protected abstract void generateIndex(String tableName, String indexName, String columns, boolean unique);
    
    public void setEventListener(GeneratorEventListener newEventListener) {
        eventListener = newEventListener;
    }
    protected boolean shouldGenerate() {
        Logger.debug(this, "in base class shouldGenerate()");
        if (objectDescriptor.getImplementationName() == null) {
            return false;
        }
        if (!objectDescriptor.getPersisted()) {
            return false;
        }
        
        Model model = objectDescriptor.getModel();
        
        String packagesToGenerate = model.getPackagesToGenerate();
        Assert.check(packagesToGenerate != null, "packagesToGenerate != null");
        
        String packageName = objectDescriptor.getImplementationName().getPackage(); 
        Assert.check(packageName != null, "packageName != null");        
        
        return packageName.startsWith(packagesToGenerate);
    }
    
    public void generate(String rootPath, ObjectDescriptor initObjectDescriptor) {
        objectDescriptor = initObjectDescriptor;        
        if (!shouldGenerate()) {
            return;
        }
        model = objectDescriptor.getModel();
        
        code = new CodeBuffer(getEOL());
        generateFileContent();
        
        Logger.debug(this, code.toString());
        
        String packagePath = rootPath + getPlatformDir() + "/";
        Logger.debug(this, "packagePath " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        
        String filePath = packagePath + "/" + objectDescriptor.getTableName() + ".sql";
        FileUtil.writeFile(filePath, code.toString());

    }

    
    private void generateFileContent() {
        Logger.debug(this, objectDescriptor);
        generateHeader();
        generateTable();        
        generatePrimaryKey();        
        generateIndicies();        
        
    }

    
    protected void generateHeader() {
        code.addLine("/* " + objectDescriptor.getTableName() + ".sql");
        code.addLine("* Generated table creation script");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine("*/");
    }
    
    protected void generateTable() {
        code.addLine("drop table " + objectDescriptor.getTableName());
        code.addLine(getStatementTerminator());
        code.addLine("create table " + objectDescriptor.getTableName() + " (");
        if (objectDescriptor.getMultiTenant()) {
            code.addLine("    tid " + getBinaryType() + " not null,");
        }
        code.addLine("    Id " + getBinaryType() + " not null,");
        if (objectDescriptor.getCreatedModifiedFields()) {
            code.addLine("    createdDate " + getDateTimeType() + " null,");
            code.addLine("    createdBy varchar(20) null,");
            code.addLine("    modifiedDate " + getDateTimeType() + " null,");
            code.addLine("    modifiedBy varchar(20) null,");
        }
        
        int fieldCount = 0;
        for (FieldDescriptor field : objectDescriptor.getPersistedFields()) {
        	Logger.debug(this, "field.getType(): " + field.getType().toString());
            if (field.getType() == FieldTypeEnum.STRING) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " varchar(" + field.getSize() + ")");
            } else if (field.getType() == FieldTypeEnum.TEXT) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " TEXT");
            } else if (field.getType() == FieldTypeEnum.BOOLEAN) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " tinyint");
            } else if (field.getType() == FieldTypeEnum.CLASS) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " " + getBinaryType());
            } else if (field.getType() == FieldTypeEnum.DATE) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " " + getDateType());
            } else if (field.getType() == FieldTypeEnum.DATETIME) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " " + getDateTimeType());
            } else if (field.getType() == FieldTypeEnum.DOUBLE) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " Double Precision");
            } else if (field.getType() == FieldTypeEnum.IDENTITY) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " " + getBinaryType());
            } else if (field.getType() == FieldTypeEnum.INSTANT) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " datetime");
            } else if (field.getType() == FieldTypeEnum.INT) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " integer");
			} else if (field.getType() == FieldTypeEnum.INTEGER) {
				commaIfNeeded(fieldCount++);
				code.add("    " + field.getColumnName() + " integer");
            } else if (field.getType() == FieldTypeEnum.LOCALDATE) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " date");
            } else if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.COALESCE) {
                // do nothing
            } else if (field.getType() == FieldTypeEnum.ENUM) {
                commaIfNeeded(fieldCount++);
                code.add("    " + field.getColumnName() + " varchar(50)");
            } else {
                Assert.check(false, "Unknown ");
            }
    
        }
        
        code.addLine(")");        
        code.addLine(getStatementTerminator());
        code.addLine();        
    }
    
    private void commaIfNeeded(int fieldIndex) {
        if (fieldIndex > 0) {
            code.addLine(",");
        } 
    }
    
    protected void generatePrimaryKey() {
    	if (objectDescriptor.getMultiTenant()) {
            code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " ADD PRIMARY KEY (tid, Id)");
            code.addLine(getStatementTerminator());
            code.addLine();        
    	} else {
            code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " ADD PRIMARY KEY (Id)");
            code.addLine(getStatementTerminator());
            code.addLine();        
    	}
    }

    protected void generateIndicies() {
        for (IndexDescriptor index : objectDescriptor.getIndicies()) {
            generateIndex(objectDescriptor.getTableName(), index.getIndexName(), index.getColumns(), index.getUnique());
            code.addLine(getStatementTerminator());
            code.addLine();        
        }
    }
    
}
