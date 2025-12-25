/*
 * TableGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;



/**
 *
 * @author  kevind
 */
public class MssqlCreateTableGenerator extends CreateTableGenerator {
    
    /** Creates a new instance of ValueObjectGenerator */
    public MssqlCreateTableGenerator() {
    }
    
    protected String getEOL() {
        return "\r\n";
    }

    protected String getStatementTerminator() {
        return "go";
    }

    protected String getPlatformDir() {
        return "mssql";
    }

    protected String getBinaryType() {
        return " binary(16)";
    }
    
    protected String getDateType() {
        return "datetime";
    }

    protected String getDateTimeType() {
        return "datetime";
    }

    protected void generateIndex(String tableName, String indexName, String columns, boolean unique) {
    	StringBuilder str = new StringBuilder();
    	
    	str.append("create ");
    	if (unique) {
        	str.append("unique ");
    	}
    	str.append("index  ");
    	str.append(indexName);
    	str.append(" on  ");
    	str.append(tableName);
    	str.append(" (");
    	str.append(columns);
    	str.append(")");
    	
        code.addLine(str.toString());
    }
    
    
    
    
}
