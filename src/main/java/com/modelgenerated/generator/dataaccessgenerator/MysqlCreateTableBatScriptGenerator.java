/*
 * MysqlCreateTableBatScriptGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;


/**
 *
 * @author  kevind
 */
public class MysqlCreateTableBatScriptGenerator extends CreateTableScriptGenerator {
    
    /** Creates a new instance of ValueObjectGenerator */
    public MysqlCreateTableBatScriptGenerator() {
    }
    
    protected String getEOL() {
        return "\r\n";
    }

    protected String getPlatformDir() {
        return "mysql/tables";
    }

    public String getScriptName() {
        return "/createGeneratedTables.bat";
    }
    
    protected void generateScriptCall(String tableName) {    
        code.addLine("mysql --user=%DBUSER% --password=%DBPASSWORD% --database=%DATABASE% --force -v -v -v < " + tableName + ".sql");        
    }
    
    
}
