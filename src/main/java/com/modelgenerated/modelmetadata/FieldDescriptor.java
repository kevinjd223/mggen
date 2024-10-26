/* FieldDescriptor.java
 *
 * Copyright 2002-2024 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;

/**
 * Describes a field within an objects. 
 * 
 */
public class FieldDescriptor {
    private ObjectDescriptor parentObject;
    private String name;
    private String description;
    private String columnName;
    private FieldTypeEnum type; 
    private ClassDescriptor classDescriptor; 
    private int size;
    private boolean persisted;
    private boolean nullable;
    private boolean useInTest;
    private ReferenceTypeEnum referenceType;
    private String joinField;
    private String alias;
    private String tableName;
    private String version;
    private String sql;
    private String coalesce1;
    private String coalesce2;
    private String coalesce3;
    private String coalesce4;
    
    
	/** Creates a new instance of FieldDescriptor */
    public FieldDescriptor(ObjectDescriptor parentObject) {
        this.parentObject = parentObject;
    }
    
    public String getName() {
        return name;
    }    
    public void setName(String newName) {
        name = newName;
    }
    
    /**
     * A description of this fields.
     * This is used to generate javadoc comments
     */
    public String getDescription() {
        return description;
    }    
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /**
     * The column name to use to generate database and dao code for persisted objects
     * For joined fields this will be <tablename>.<columnname>
     */
    public String getColumnName() {
        return columnName;
    }    
    public void setColumnName(String newColumnName) {
        columnName = newColumnName;
    }

    /**
     * For joined fields this separates off the leading column names and returns the columnname 
     */
    public String getRealColumnName() {
        String delimiter = "\\x2E"; // dot separated.
        Logger.debug(this, "delimiter: " + delimiter);

        String[] columnList = this.getColumnName().split(delimiter);
        String actualColumn = columnList[columnList.length-1];
        return actualColumn;
    }    
    
    /** 
     * The type of this column
     */
    public FieldTypeEnum getType() {
        return type;
    }
    
    public void setType(FieldTypeEnum newType) {
        type = newType;
    }

    /**
     * This is only used for FieldDescriptors of type=FieldTypeEnum.READONLYJOIN. 
     */
    public String getJoinField() {
        return joinField;
    }
    
    public void setJoinField(String newJoinField) {
        joinField = newJoinField;
    }
    
    /** 
     * For fields that reference other objects, this is the alias to use if a join needs to be performed.  
     * For read only joins this is the alias on the field that is being used to perform the join.
     */
    public String getAlias() {
    	if (getType() == FieldTypeEnum.READONLYJOIN) {
            FieldDescriptor joinFieldDescriptor = this.getJoinedFieldAlias();
            return joinFieldDescriptor.getAlias();
        } 
    	return alias;
    }
    
    public void setAlias(String newAlias) {
        alias = newAlias;
    }

    /** 
     * For fields that reference other objects, this is the table to use if a join needs to be performed.
     * This field is optional. If it is missing for a join object then the joined object is read to get the table name. 
     * This field is needed when this model needs to join objects in another model.   
     * For read only joins this is the Table off the field that is being used to perform the join
     */
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String newTableName) {
        tableName = newTableName;
    }

    public String getVersion() {
        return version;
    }    
    public void setVersion(String newVersion) {
        version = newVersion;
    }
    
    public String getSql() {
		return sql;
	}
	public void setSql(String newSql) {
		this.sql = newSql;
	}

    
    
    public ClassDescriptor getClassDescriptor() {
        if (getType() == FieldTypeEnum.READONLYJOIN) {
            FieldDescriptor joinedFieldDescriptor = getJoinedFieldDescriptor();
            System.out.println("joinedFieldDescriptor.getJavaType() " + joinedFieldDescriptor.getName() +  " " + joinedFieldDescriptor.getJavaType());
            
            return joinedFieldDescriptor.getClassDescriptor();
        } else {
            return classDescriptor;
        }
    }
    
    public void setClassDescriptor(ClassDescriptor newClassDescriptor) {
        classDescriptor = newClassDescriptor;
    }
    
    public String getJavaType() {
        if (getType() == FieldTypeEnum.READONLYJOIN) {
            FieldDescriptor joinedFieldDescriptor = getJoinedFieldDescriptor();
            // System.out.println("joinedFieldDescriptor.getJavaType() " + joinedFieldDescriptor.getName() +  " " + joinedFieldDescriptor.getJavaType());
            
            return joinedFieldDescriptor.getJavaType();
        } else if (getType() == FieldTypeEnum.COALESCE) {
            return "String";
        } else if (getType() == FieldTypeEnum.CLASS || getType() == FieldTypeEnum.ENUM) {
            return getClassDescriptor().getClassName();
        } else if (getType() == FieldTypeEnum.TEXT) {
            return "String";
        } else if (getType() == FieldTypeEnum.DATETIME) {
            return "Date";
        } else {
            return getType().toString();
        }
    }
    
    /* 
     * How are getJoinedFieldAlias() and getJoinedFieldDescriptor() different? How can the common stuff be refactored?
     * It appears getJoinedFieldAlias() gets an earlier object
     */    
    protected FieldDescriptor getJoinedFieldAlias() {
        String delimiter = "\\x2E"; // dot separated.
        Logger.debug(this, "delimiter: " + delimiter);
        // Assert.check(false, "stop getJoinedFieldAlias()");

        String[] columnList = this.getColumnName().split(delimiter);
        String actualColumn = columnList[columnList.length-1];
        Logger.debug(this, "actualColumn: " + actualColumn);
        Logger.debug(this, "columnList.length: " + columnList.length);
        System.out.println("actualColumn: " + actualColumn); 

        Model model = parentObject.getModel();
        ObjectDescriptor joinObjectDescriptor = parentObject;
        FieldDescriptor joinedFieldDescriptor = null;

        // walk forward through the list to get the field descriptor from 
        for (int i = 0; i < columnList.length-1; i++) {            
            Logger.debug(this, "columnList[" + i + "]: " + columnList[i]);                

            joinedFieldDescriptor = joinObjectDescriptor.findField(columnList[i]);
            Logger.debug(this, "joinedFieldDescriptor: " + joinedFieldDescriptor);                

            ClassDescriptor joinClassDescriptor = joinedFieldDescriptor.getClassDescriptor();
            Logger.debug(this, "joinClassDescriptor: " + joinClassDescriptor);                
            
            joinObjectDescriptor = model.findObject(joinClassDescriptor.getFQN());
            Logger.debug(this, "joinObjectDescriptor: " + joinObjectDescriptor);                
        }
        //joinedFieldDescriptor = joinObjectDescriptor.findField(actualColumn);

        return joinedFieldDescriptor;
    }

    /* 
     * This is called on 
     * Return the object of the fieldDescriptor for the field that is from the object just prior to this fieldDescriptor.
     *   
     *  <Field>
     *      <Name>Customer</Name>
     *      <ColumnName>CustomerId</ColumnName>
     *      <Type>Class</Type>
     *      <Class>com.absco.organization.Organization</Class>
     *      <Nullable>yes</Nullable>
     *      <Alias>c</Alias>
     *      <TableName>Organization</TableName>
     *  </Field>
     *  <Field>
     *      <Name>CustomerName</Name>
     *      <Type>ReadOnlyJoin</Type>
     *      <JoinField>Customer</JoinField>
     *      <ColumnName>Customer.Name</ColumnName>
     *  </Field>
     *   
     *   
     */
    public FieldDescriptor getJoinedFieldDescriptor() {
        Assert.check(FieldTypeEnum.READONLYJOIN == this.getType(), "FieldTypeEnum.READONLYJOIN == this.getType()");
        String delimiter = "\\x2E"; // dot separated.
        Logger.debug(this, "delimiter: " + delimiter);

        String[] columnList = this.getColumnName().split(delimiter);
        String actualColumn = columnList[columnList.length-1];
        Logger.debug(this, "actualColumn: " + actualColumn);
        Logger.debug(this, "columnList.length: " + columnList.length);

        Model model = parentObject.getModel();
        ObjectDescriptor joinObjectDescriptor = parentObject;
        FieldDescriptor joinedFieldDescriptor = null;

        for (int i = 0; i < columnList.length-1; i++) {            
            joinedFieldDescriptor = joinObjectDescriptor.findField(columnList[i]);

            ClassDescriptor joinClassDescriptor = joinedFieldDescriptor.getClassDescriptor();
            
            joinObjectDescriptor = model.findObject(joinClassDescriptor.getFQN());
        }
        Assert.check(joinObjectDescriptor != null, "joinObjectDescriptor != null");
        
        // Assert.check(joinObjectDescriptor == null, "joinedFieldDescriptor.equals(joinObjectDescriptor.findField(actualColumn))");
        joinedFieldDescriptor = joinObjectDescriptor.findField(actualColumn);

        return joinedFieldDescriptor;
    }

    public String getJavaVariableName() {
        return StringUtil.getJavaVariableFromClassName(name);
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int newSize) {
        size = newSize;
    }
    
    public boolean getPersisted() {
        return persisted;
    }
    
    public void setPersisted(boolean newPersisted) {
        persisted = newPersisted;
    }
    
    public boolean getNullable() {
        return nullable;
    }
    
    public void setNullable(boolean newNullable) {
        nullable = newNullable;
    }
    
    public boolean getUseInTest() {
        return useInTest;
    }
    
    public void setUseInTest(boolean newUseInTest) {
        useInTest = newUseInTest;
    }

    public ReferenceTypeEnum getReferenceType() {
        return referenceType;
    }
    
    public void setReferenceType(ReferenceTypeEnum newReferenceType) {
        referenceType = newReferenceType;
    }
    
    public String getObjectField() {
        // I think this is using table.column so the jsp doesn't have to be refactored if absco is refactored 
        // to use project and proposal instead of job and estimate.
        // TODO: getObjectField needs to use object and field name. modelgenerated bug 28
        return parentObject.getTableName() + "." + this.getColumnName();        
    }
    
    /**
     * Returns the list of joins associated with this Field. 
     * If Type == READONLYJOIN return a set of unique JoinDescriptors based on column field.
     * If Type != READONLYJOIN return empty list.
     * 
     */
    public JoinList getJoins() {
        JoinList joinList = new JoinList();
                
        if (this.getType() == FieldTypeEnum.READONLYJOIN) {
            
            Logger.debug(this, "this.getColumnName(): " + this.getColumnName());
            
            String delimiter = "\\x2E"; // dot separated.
            Logger.debug(this, "delimiter: " + delimiter);
            String[] columnList = this.getColumnName().split(delimiter);
            Logger.debug(this, "columnList.length: " + columnList.length);
            Logger.debug(this, "columnList[0]: " + columnList[0]);
            
            Assert.check(columnList.length >= 2, "columnList.length >= 2");
            
            String actualColumn = columnList[columnList.length-1];
            Logger.debug(this, "actualColumn: " + actualColumn);
            
            ObjectDescriptor joinObjectDescriptor = parentObject;
            
            FieldDescriptor joinFieldDescriptor = null;            
            //FieldDescriptor joinFieldDescriptor = parentObject.findField(columnList[0]);            
            //Logger.debug(this, "joinFieldDescriptor: " + joinFieldDescriptor);
            
            String nextLeftAlias = parentObject.getTableAlias();
            
            for (int i = 0; i < columnList.length-1; i++) {
            
                JoinDescriptor joinDescriptor = new JoinDescriptor();                        
                
                joinFieldDescriptor = joinObjectDescriptor.findField(columnList[i]);                

                joinDescriptor.leftAlias = nextLeftAlias;
                joinDescriptor.leftColumn = joinFieldDescriptor.getColumnName();

                ClassDescriptor joinClassDescriptor = joinFieldDescriptor.getClassDescriptor();
                Assert.check(joinClassDescriptor != null, "joinClassDescriptor != null");
                Logger.debug(this, "joinClassDescriptor: " + joinClassDescriptor);                
                
                
                joinDescriptor.rightAlias = joinFieldDescriptor.getAlias();
                
                // If the join field descriptor has a table name use it. Otherwise, get the table name from the joind object. 
                // The preferred way is the get the table name from the joined object, but if the join is to an object in a 
                // different model then the joined object won't be available.
                if (!StringUtil.isEmpty(joinFieldDescriptor.getTableName())) {
                    joinDescriptor.rightTable = joinFieldDescriptor.getTableName();
                } else {
                    Model model = parentObject.getModel();
                    joinObjectDescriptor = model.findObject(joinClassDescriptor.getFQN());
                    Assert.check(joinObjectDescriptor != null, "joinObjectDescriptor != null");
                    Logger.debug(this, "joinClassDescriptor: " + joinClassDescriptor);                

                    joinDescriptor.rightTable = joinObjectDescriptor.getTableName();
                }
                joinDescriptor.rightColumn = "Id";

                if (!joinList.contains(joinDescriptor)) {
                    joinList.add(joinDescriptor);
                }
                nextLeftAlias = joinDescriptor.rightAlias;
            }
        }
        return joinList;
    }

    public boolean isDeprecated() {
        return description != null && description.contains("@deprecated");
    }

    public String getCoalesce1() {
		return coalesce1;
	}
	public void setCoalesce1(String coalesce1) {
		this.coalesce1 = coalesce1;
	}

	public String getCoalesce2() {
		return coalesce2;
	}
	public void setCoalesce2(String coalesce2) {
		this.coalesce2 = coalesce2;
	}

	public String getCoalesce3() {
		return coalesce3;
	}
	public void setCoalesce3(String coalesce3) {
		this.coalesce3 = coalesce3;
	}

	public String getCoalesce4() {
		return coalesce4;
	}
	public void setCoalesce4(String coalesce4) {
		this.coalesce4 = coalesce4;
	}


}
