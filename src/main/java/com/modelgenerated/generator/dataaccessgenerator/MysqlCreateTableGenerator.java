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
public class MysqlCreateTableGenerator extends CreateTableGenerator {
    
    /** Creates a new instance of ValueObjectGenerator */
    public MysqlCreateTableGenerator() {
    }
    
    protected String getEOL() {
        return "\n";
    }

    protected String getStatementTerminator() {
        return ";";
    }

    protected String getPlatformDir() {
        return "mysql/tables";
    }

    protected String getBinaryType() {
        return "binary(16)";
    }
    
    protected String getDateType() {
        return "date";
    }

    protected String getDateTimeType() {
        return "datetime";
    }

    protected void generateIndex(String tableName, String indexName, String columns, boolean unique) {
        code.addLine("ALTER TABLE " + tableName + " ADD INDEX " + indexName +  " (" + columns + ");");
    }
    
    
}
