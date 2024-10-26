/*
 * ValueObjectDescription.java
 *
 * Created on November 3, 2002, 9:09 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.foundation.identity.Identity;
import com.modelgenerated.util.Assert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This object describes a value object, its interface, DAO and database table.
 *
 * It parses an XML file that looks likes this.
 * 
 * <ObjectDescriptor>
 *     <Interface>com.modelgenerated.domain.party.Company</Interface>
 *     <Implementation>CompanyImpl</Implementation>
 *     <Table>Company</Table>
 *     <BaseInterface>Party</BaseInterface>
 *     <Fields>
 *         <Field>
 *             <Name>Name</Name>
 *             <ColumnName>Name</ColumnName>
 *             <Type>String</Type>
 *             <Size>50</Size>
 *             <Nullable>no</Nullable>
 *         </Field>
 *         <Field>
 *             <Name>Name</Name>
 *             <ColumnName>Name</ColumnName>
 *             <Type>String</Type>
 *             <Size>50</Size>
 *             <Nullable>no</Nullable>
 *         </Field>        
 *     </Fields>   
 *     
 *     <Queries>
 * 
 *     </Queries>
 * </ObjectDescriptor>
 *
 *
 * @author  kevind
 */
public class ObjectDescriptor {
    private Model model;
    
    private ClassDescriptor valueObjectInterface;
    private ClassDescriptor implementationName;
    private ClassDescriptor listInterfaceName;
    private ClassDescriptor listImplementationName;
    private ClassDescriptor daoInterfaceName;
    private ClassDescriptor daoImplementationName;
    private ClassDescriptor testDataClass;
    private String tableName;
    private String tableAlias;
    private String baseInterface = null;
    private String description = null;
    private boolean cloneable;
    private boolean persisted;
    private boolean supportsAudit;
    private boolean createdModifiedFields;
    private boolean multiTenant;
    private List<FieldDescriptor> fieldList = new ArrayList<FieldDescriptor>(); // keep list in original order and map to find duplicates.
    private List<ClassDescriptor> implementsList = new ArrayList<ClassDescriptor>();
    private Map<String, FieldDescriptor> fieldMap = new HashMap<String, FieldDescriptor>();
    private Map<String, ReferenceDescriptor> references = new HashMap<String, ReferenceDescriptor>();
    private Map<String, QueryDescriptor> queries = new HashMap<String, QueryDescriptor>();
    private Map<String, IndexDescriptor> indicies = new HashMap<String, IndexDescriptor>();
    //replace operations with methods
    //private List operations = new ArrayList();
    private List<Method> methods = new ArrayList<Method>();
    //private List listOperations = new ArrayList();
    private List<Method> listMethods = new ArrayList<Method>();

    /** Creates a new instance of ObjectDescriptor */
    public ObjectDescriptor(Model model) {
        this.model = model;
    }
    
    public Model getModel() {
        return model;
    }
    
    public ClassDescriptor getValueObjectInterface() {
        return valueObjectInterface;
    }
    public void setValueObjectInterface(ClassDescriptor newValueObjectInterface) {
        valueObjectInterface  = newValueObjectInterface;
    }

	// todo: this should be getImplementation instead of getImplementationName 
    public ClassDescriptor getImplementationName() {
        return implementationName;
    }
    public void setImplementationName(ClassDescriptor newImplementationName) {
        implementationName = newImplementationName;
    }
    
    public ClassDescriptor getListInterface() {
        if (listInterfaceName != null) {
            return listInterfaceName;
        } else {           
            Assert.check(valueObjectInterface != null, "valueObjectInterface != null");
            String listInterfaceString = valueObjectInterface.getFQN() + "List";
            return new ClassDescriptor(listInterfaceString);
        }
    }
    public void setListInterface(ClassDescriptor newListInterface) {
        listInterfaceName = newListInterface;
    }
    
    public ClassDescriptor getListImplementation() {
        if (listImplementationName != null) {
            return listImplementationName;
        } else {           
            // create list interface name based on the valueObjectInterface.
            Assert.check(valueObjectInterface != null, "valueObjectInterface != null");
            String fqn = valueObjectInterface.getPackage() + ".impl.gen." + valueObjectInterface.getClassName() + "ListImpl";
            return new ClassDescriptor(fqn);
        }
    }
    public void setListImplementation(ClassDescriptor newListImplementation) {
        listImplementationName = newListImplementation;
    }

    public ClassDescriptor getDAOInterface() {
        if (daoInterfaceName != null) {
            return daoInterfaceName;
        } else {           
            Assert.check(valueObjectInterface != null, "valueObjectInterface != null");
            ClassDescriptor valueObjectClass = getValueObjectInterface(); 
            String daoInterfaceString = valueObjectClass.getPackage() + ".dao." + valueObjectClass.getClassName() + "DAO";
            return new ClassDescriptor(daoInterfaceString);
        }
    }
    public void setDAOInterface(ClassDescriptor newDAOInterface) {
        daoInterfaceName = newDAOInterface;
    }
    public ClassDescriptor getDAOImplementation() {
        if (daoImplementationName != null) {
            return daoImplementationName;
        } else {           
            // create list interface name based on the valueObjectInterface.
            ClassDescriptor valueObjectClass = getValueObjectInterface(); 
            String fqn = valueObjectClass.getPackage() + ".impl.gen." + valueObjectClass.getClassName() + "DAOImpl";
            return new ClassDescriptor(fqn);
        }
    }
    public void setDAOImplementation(ClassDescriptor newDAOImplementation) {
        daoImplementationName = newDAOImplementation;
    }

    
    public ClassDescriptor getDAOTestClass() {
        // Default name for now. Later allow user to specify.
        ClassDescriptor daoInterface = getDAOInterface(); 
        String daoTestClass = daoInterface.getFQN() + "Test";
        return new ClassDescriptor(daoTestClass);
    }
    
    public ClassDescriptor getTestDataClass() {
        return testDataClass;
    }
    public void setTestDataClass(ClassDescriptor newTestDataClass) {
    	testDataClass = newTestDataClass;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String newTableName) {
        tableName = newTableName;
    }
    
    public String getTableAlias() {
        return tableAlias;
    }
    public void setTableAlias(String newTableAlias) {
        tableAlias = newTableAlias;
    }
    
    public boolean getCloneable() {
        return cloneable;
    }
    public void setCloneable(boolean newCloneable) {
        cloneable = newCloneable;
    }
    
    public boolean getPersisted() {
        return persisted;
    }
    public void setPersisted(boolean newPersisted) {
        persisted = newPersisted;
    }
    
    public boolean getSupportsAudit() {
        return supportsAudit;
    }
    public void setSupportsAudit(boolean newSupportsAudit) {
        supportsAudit = newSupportsAudit;
    }
    
    public boolean getCreatedModifiedFields() {
        return createdModifiedFields;
    }
    public void setCreatedModifiedFields(boolean newCreatedModifiedFields) {
        createdModifiedFields = newCreatedModifiedFields;
    }
    
    public boolean getMultiTenant() {
        return multiTenant;
    }
    public void setMultiTenant(boolean newMultiTenant) {
        multiTenant = newMultiTenant;
    }
    
    public String getBaseInterface() {
        return baseInterface;
    }
    public void setBaseInterface(String newBaseInterface) {
        baseInterface = newBaseInterface;
    }
    
    public ObjectDescriptor getBaseObjectDescriptor() {
        if (baseInterface == null || baseInterface.length() == 0) {
            return null;
        }
        
        ObjectDescriptor objectDescriptor = model.findObject(baseInterface);
        Assert.check(objectDescriptor != null, "objectDescriptor != null - " + baseInterface);
        
        return objectDescriptor;        
    }
    
    public void addField(FieldDescriptor field) {
        Assert.check(field != null, "field != null");
        
        fieldList.add(field);        
        fieldMap.put(field.getName(), field); 
    }
    
    
    // todo: make getFields return List
    public List<FieldDescriptor> getFields() {
        return fieldList;
    }
    // todo: make getFields return List
    public List<FieldDescriptor> getFieldsSorted() {
    	List<FieldDescriptor> sortList = new ArrayList<FieldDescriptor>();
    	Iterator<FieldDescriptor> i = fieldList.iterator();
    	while (i.hasNext()) {
    		sortList.add(i.next());    		
    	}
    			
		Collections.sort(sortList , new Comparator<FieldDescriptor>() {
            @Override
            public int compare(FieldDescriptor o1, FieldDescriptor o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
            }
    	});
    			
        return sortList;
    }
    /**
     * This is still used by parser
     * @deprecated - use addField()
     */
    public void setFields(List<FieldDescriptor> newFieldList) {
        fieldList = newFieldList;
        fieldMap = new HashMap<String, FieldDescriptor>();
        if (fieldList != null) { 
            for (FieldDescriptor fieldDescriptor : fieldList) {
                fieldMap.put(fieldDescriptor.getName(), fieldDescriptor); 
            }
        }
    }

    public boolean hasFields() {
        return fieldList.size() > 0;
    }

    public List<FieldDescriptor> getPersistedFields() {
        ArrayList<FieldDescriptor> persistedFields = new ArrayList<FieldDescriptor>();
        for (FieldDescriptor field : fieldList) {
            if (field.getPersisted()) {
                persistedFields.add(field);
            }
        }        
        
        return persistedFields;
    }
    
    public FieldDescriptor findField(String name) {
        if ("Id".equals(name)) {
            // used for readonly joins to an Id 
            // the generators hard code the generation of ids so they are not in the list.
            // TODO: Consider making Id and member of the ObjectDescriptor field list.
            FieldDescriptor fieldDescriptor = new FieldDescriptor(this);
            fieldDescriptor.setName("Id");
            fieldDescriptor.setColumnName("Id");
            fieldDescriptor.setType(FieldTypeEnum.IDENTITY);
            fieldDescriptor.setPersisted(true);
            fieldDescriptor.setNullable(false);
            fieldDescriptor.setClassDescriptor(new ClassDescriptor(Identity.class.getCanonicalName()));
            return fieldDescriptor;
        } else {
            return fieldMap.get(name);
        }
    }
    
    public FieldDescriptor findFieldByColumnName(String columnName) {
        //Logger.debug(this, "findFieldByColumnName " + columnName);
        //Logger.debug(this,this);
        Assert.check(columnName != null, "columnName != null");
        for (FieldDescriptor fieldDescriptor : fieldMap.values()) {
            String enumColumnName = fieldDescriptor.getColumnName();
            if (enumColumnName != null && enumColumnName.equals(columnName)) {                
                return fieldDescriptor;
            }
        }
        return null;
    }

    /**
     * Returns this object has this field type directly or through a joined field.
     */
    public boolean hasFieldType(FieldTypeEnum type) {
        for (FieldDescriptor fieldDescriptor : fieldMap.values()) {
            FieldDescriptor joinedFieldDescriptor = fieldDescriptor.getType() == FieldTypeEnum.READONLYJOIN
                    ? fieldDescriptor.getJoinedFieldDescriptor() : null;

            if (fieldDescriptor.getType() == type
                    || (joinedFieldDescriptor != null && joinedFieldDescriptor.getType() == type)) {
                return true;
            }
        }
        return false;
    }
    
    public List<ClassDescriptor> getClassFieldClasses() {
        return this.getClassFieldClasses(this.getFields());
    }
    
    public List<ClassDescriptor> getPersistedClassFieldClasses() {
        // Iterator i = this.getPersistedFields();
        return this.getClassFieldClasses(this.getPersistedFields());
    }
    
    private List<ClassDescriptor> getClassFieldClasses(List<FieldDescriptor> fieldDescriptors) {
        List<ClassDescriptor> classes = new ArrayList<ClassDescriptor>();
        for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
            if (fieldDescriptor.getType() == FieldTypeEnum.CLASS) {
                classes.add(fieldDescriptor.getClassDescriptor());                    
            }
        }
        return classes;
    }
    
    public List<ClassDescriptor> getReferencedClasses() {
        ArrayList<ClassDescriptor> classes = new ArrayList<ClassDescriptor>();
        for (ReferenceDescriptor referenceDescriptor : this.getReferences()) {
            classes.add(referenceDescriptor.getClassDescriptor());                    
        }
        return classes;
    }


    // TODO: getReferences and setReference should be of the same type. Shouldn't set map and get collection. 
    public Collection<ReferenceDescriptor> getReferences() {
        return references.values();
    }
    
    public void setReferences(Map<String,ReferenceDescriptor> newReferences) {
        references = newReferences;
    }

    public ReferenceDescriptor findReference(String name) {
        return (ReferenceDescriptor)references.get(name);
    }
    

    public boolean hasReferences() {
            return references.size() > 0;
    }

    public Collection<QueryDescriptor> getQueries() {
        return queries.values();
    }
    public void setQueries(Map<String, QueryDescriptor> newQueries) {
        queries = newQueries;
    }
    
    public Collection<IndexDescriptor> getIndicies() {
        return indicies.values();
    }
    public void setIndicies(Map<String, IndexDescriptor> newIndicies) {
        indicies = newIndicies;
    }
    /*
    public List getOperations() {
        return operations;
    }
    public void setOperations(List newOperations) {
        operations = newOperations;
    }
    */
    public List<Method> getMethods() {
        return methods;
    }
    public void setMethods(List<Method> newMethods) {
        methods = newMethods;
    }
    /*
    public List getListOperations() {
        return listOperations;
    }
    public void setListOperations(List newListOperations) {
        listOperations = newListOperations;
    }
    */
    public List<Method> getListMethods() {
        return listMethods;
    }
    public void setListMethods(List<Method> newListMethods) {
        listMethods = newListMethods;
    }
    
    public List<ClassDescriptor> getImplementsList() {
        return implementsList;
    }
    public void setImplementsList(List<ClassDescriptor> newImplementsList) {
        implementsList = newImplementsList;
    }
    
    public String getLinkingId(String fieldName) {
        Assert.check(fieldName != null, "fieldName != null");
        //Logger.debug(this, "*******");
        for (FieldDescriptor fieldDescriptor : this.getFields()) {
            //Logger.debug(this, "field:" + fieldDescriptor.getColumnName() );
            if (fieldDescriptor.getType() == FieldTypeEnum.CLASS) {
                //ClassDescriptor classDescriptor = fieldDescriptor.getClassDescriptor();
                if (fieldName.equals(fieldDescriptor.getName())) {
                    return fieldDescriptor.getColumnName();
                }
            }                
        }
        return null;
    }
    
    public Set getJoinFields() {
        Set joinedObjectSet = new HashSet();
        
        for (FieldDescriptor field : this.getFields()) {
            if (field.getType() == FieldTypeEnum.READONLYJOIN) {
                //ObjectDescriptor joinedObject = getObjectDescriptor(field.getClassDescriptor().getFQN());
                //String className = joinedObject.getValueObjectInterface().getFQN();
                String joinFieldName = field.getJoinField();
                FieldDescriptor joinField = this.findField(joinFieldName);
                
                Assert.check(joinField != null, "joinField != null");
                
                if (!joinedObjectSet.contains(joinField)) {
                    joinedObjectSet.add(joinField);
                }
            }
        }       
        return joinedObjectSet;
        
    }
    
    public JoinList getJoins() {
        
        JoinList joinList = new JoinList();
        
        for (FieldDescriptor field : this.getFields()) {
            for (JoinDescriptor joinDescriptor : field.getJoins()) {
                if (!joinList.contains(joinDescriptor)) {
                    joinList.add(joinDescriptor);
                }
            }
        }       
        return joinList;
        
    }
    
    protected String classDescriptorToString(ClassDescriptor classDescriptor) {
        if (classDescriptor == null) { 
            return null;
        } else {            
            return classDescriptor.getFQN();
        }
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public boolean isDeprecated() {
        return description != null && description.contains("@deprecated");
    }

    

}
