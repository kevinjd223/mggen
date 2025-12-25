/*
 * TableGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class MssqlRecreateIndexesGenerator {
    CodeBuffer code;
    Model model;
    
    /** Creates a new instance of ValueObjectGenerator */
    public MssqlRecreateIndexesGenerator() {
    }
    
        
    public void generate(Model model, String scriptPath) {
        this.model = model;
        
        code = new CodeBuffer("\n");
        generateFileContent();
        Logger.debug(this, code.toString());        

        
        String packagePath = scriptPath + "mssql/";
        Logger.debug(this, "packagePath " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        Logger.debug(this, "bCreated: " +bCreated);

        String filePath = packagePath + "recreateIndexesMssql.sql";
        FileUtil.writeFile(filePath, code.toString());

    }

    
    private void generateFileContent() {
        generateHeader();
        generateScriptCalls();        
    }

    
    protected void generateHeader() {
        code.addLine("/* recreateIndexesMssql.sql - generated index update code");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine(" * " + copyrightNotice);
        }
        code.addLine(" */");
        code.addLine();
    }
    
    public Iterator<ObjectDescriptor> getPersistedObjectDescriptorsSorted(Map<String, ObjectDescriptor> objectDescriptorMap) {
    	List<ObjectDescriptor> sortList = new ArrayList<ObjectDescriptor>();

    	for (ObjectDescriptor objectDescriptor : objectDescriptorMap.values()) {
            if (objectDescriptor.getPersisted()) { 
            	sortList.add(objectDescriptor);
            }
    	}
    			
		Collections.sort(sortList , new Comparator<ObjectDescriptor>() {
            @Override
            public int compare(ObjectDescriptor o1, ObjectDescriptor o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getTableName(), o2.getTableName());
            }
    	});
    			
        return sortList.iterator();
    }

    
    protected void generateScriptCalls() {
        code.addLine("DECLARE @sql_dropprimarykey NVARCHAR(MAX);");
        code.addLine("");

        Iterator<ObjectDescriptor> i = getPersistedObjectDescriptorsSorted(model.getObjects());
        while (i.hasNext()) {
            ObjectDescriptor objectDescriptor = i.next();
                
            code.addLine("/* " + objectDescriptor.getTableName());
            code.addLine("*/");
            code.addLine("print '" + objectDescriptor.getTableName() + "';");
            code.addLine("set @sql_dropprimarykey = null;");
            
            /*
             * The drop primary key looks like this
             * DECLARE @sql_dropprimarykey NVARCHAR(MAX);
        	 * SELECT @sql_dropprimarykey = 'ALTER TABLE dbo.JsrCustomerCommunication DROP CONSTRAINT ' + name + ';'
             * FROM sys.key_constraints WHERE [type] = 'PK' AND [parent_object_id] = OBJECT_ID('dbo.JsrCustomerCommunication');
        	 * EXEC sp_executeSQL @sql_dropprimarykey;
             * 
             */
            code.addLine("SELECT @sql_dropprimarykey = 'ALTER TABLE " + objectDescriptor.getTableName() + " DROP CONSTRAINT ' + name + ';'");
            code.addLine("FROM sys.key_constraints WHERE [type] = 'PK' AND [parent_object_id] = OBJECT_ID('" + objectDescriptor.getTableName() + "');");
            code.addLine("print @sql_dropprimarykey;");
            code.addLine("EXEC sp_executeSQL @sql_dropprimarykey;");
            code.addLine("");
            
            code.addLine("ALTER TABLE " + objectDescriptor.getTableName() + " ADD CONSTRAINT PK_" + objectDescriptor.getTableName() + " PRIMARY KEY (tid, Id)");
            code.addLine(";");
            
            for (IndexDescriptor index : objectDescriptor.getIndicies()) {
                code.add    ("DROP INDEX " + index.getIndexName() + " ON " + objectDescriptor.getTableName() );
                code.addLine(";");

                code.add    (getIndexCreateLine(index.getIndexName(), objectDescriptor.getTableName(), index.getColumns(), index.getUnique(), objectDescriptor.getMultiTenant()));
                code.addLine(";");
            }
        }       
    }
    
    
    
    private String getIndexCreateLine(String indexName, String tableName, String columns, boolean unique, boolean multitenant) {
    	StringBuilder str = new StringBuilder();
    	str.append("create ");
    	if (unique) {
        	str.append("unique ");
    	}
    	str.append("index  ");
    	str.append(indexName);
    	str.append(" on  ");
    	str.append(tableName);
    	str.append("(");
    	if (multitenant) {
        	str.append("tid, ");
    	}
    	str.append(columns);
    	str.append(")");
    	
    	return str.toString();
    }
    	

}
